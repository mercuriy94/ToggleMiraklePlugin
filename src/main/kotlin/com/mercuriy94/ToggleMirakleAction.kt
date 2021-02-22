package com.mercuriy94

import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.lang.properties.psi.impl.PropertiesFileImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiManager
import icons.PluginIcons

class ToggleMirakleAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val localProps = getLocalPropertiesFile(e) ?: return
        val property = localProps.findPropertyByKey("mirakle_enabled") ?: return
        val currentValue = property.value
        val (newIcon, newValue) = if (currentValue == "true") {
            PluginIcons.MIRAKLE_OFF to "false"
        } else {
            PluginIcons.MIRAKLE_ON to "true"
        }
        e.presentation.icon = newIcon
        WriteCommandAction.runWriteCommandAction(e.project) { property.setValue(newValue) }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val localProps = getLocalPropertiesFile(e) ?: return
        val property = localProps.findPropertyByKey("mirakle_enabled") ?: return
        val currentValue = property.value
        if (currentValue == "true") {
            e.presentation.icon = PluginIcons.MIRAKLE_ON
        } else {
            e.presentation.icon = PluginIcons.MIRAKLE_OFF
        }

    }

    private fun getLocalPropertiesFile(e: AnActionEvent): PropertiesFile? {
        val project = e.project ?: return null
        val vFiles = ProjectRootManager.getInstance(project).contentRoots
        val virtualLocalProps = vFiles.first().children.find { it.name == "local.properties" } ?: return null
        val psiLocalPropsFile = PsiManager.getInstance(project).findFile(virtualLocalProps) ?: return null
        return PropertiesFileImpl(psiLocalPropsFile.viewProvider)
    }
}