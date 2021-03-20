package com.mercuriy94

import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.lang.properties.psi.impl.PropertiesFileImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.search.FilenameIndex.getFilesByName
import com.intellij.psi.search.GlobalSearchScope.projectScope
import icons.PluginIcons

class ToggleMirakleAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val localProps = getLocalPropertiesFile(e) ?: return
        val property = localProps.findPropertyByKey(MIRAKLE_ENABLE_PROPERTY_KEY) ?: return
        val currentValue = property.value
        val (newIcon, newValue) = if (currentValue == ENABLE_MIRAKLE) {
            PluginIcons.MIRAKLE_OFF to DISABLE_MIRAKLE
        } else {
            PluginIcons.MIRAKLE_ON to ENABLE_MIRAKLE
        }
        e.presentation.icon = newIcon
        WriteCommandAction.runWriteCommandAction(e.project) { property.setValue(newValue) }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val localProps = getLocalPropertiesFile(e) ?: return
        val property = localProps.findPropertyByKey(MIRAKLE_ENABLE_PROPERTY_KEY) ?: return
        val currentValue = property.value
        if (currentValue == ENABLE_MIRAKLE) {
            e.presentation.icon = PluginIcons.MIRAKLE_ON
        } else {
            e.presentation.icon = PluginIcons.MIRAKLE_OFF
        }
    }

    private fun getLocalPropertiesFile(e: AnActionEvent): PropertiesFile? {
        val project = e.project ?: return null
        val localPropsViewProvider = getFilesByName(project, LOCAL_PROPS_FILE_NAME, projectScope(project))
            .first { it.parent?.let { psiDir -> psiDir.virtualFile.path == project.basePath } ?: false }
            ?.viewProvider
            ?: return null
        return PropertiesFileImpl(localPropsViewProvider)
    }

    private companion object {
        const val ENABLE_MIRAKLE = "true"
        const val DISABLE_MIRAKLE = "false"
        const val LOCAL_PROPS_FILE_NAME = "local.properties"
        const val MIRAKLE_ENABLE_PROPERTY_KEY = "mirakle_enabled"
    }
}
