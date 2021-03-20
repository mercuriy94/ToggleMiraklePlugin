package com.mercuriy94

import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope.projectScope
import icons.PluginIcons
import res.*

class ToggleMirakleAction : AnAction() {

    private val stringResourceService by lazy { service<StringResourceService>() }
    private val errorNotificationGroup by lazy {
        NotificationGroup(
            displayId = "Mirakle error notification group",
            displayType = NotificationDisplayType.BALLOON,
            isLogByDefault = true
        )
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val localProps = getLocalPropertiesFile(project) ?: createLocalPropsFile(project)
        if (localProps == null) {
            showErrorCreatePropsFile(project)
        } else {
            toggleMirakle(e, localProps)
        }
    }

    private fun toggleMirakle(e: AnActionEvent, fileProps: PropertiesFile) {
        val property = fileProps.findPropertyByKey(MIRAKLE_ENABLE_PROPERTY_KEY)
            ?: fileProps.addNewProperty(MIRAKLE_ENABLE_PROPERTY_KEY, DISABLE_MIRAKLE)
            ?: run {
                showErrorCreateMirakleProperty(fileProps.project)
                return
            }
        val currentValue = property.value
        val (newIcon, newValue) = if (currentValue == ENABLE_MIRAKLE) {
            PluginIcons.MIRAKLE_OFF to DISABLE_MIRAKLE
        } else {
            PluginIcons.MIRAKLE_ON to ENABLE_MIRAKLE
        }
        e.presentation.icon = newIcon
        runWriteCommandAction(e.project) { property.setValue(newValue) }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val project = e.project ?: return
        val currentValue = getLocalPropertiesFile(project)
            ?.findPropertyByKey(MIRAKLE_ENABLE_PROPERTY_KEY)?.value
            ?: false
        if (currentValue == ENABLE_MIRAKLE) {
            e.presentation.icon = PluginIcons.MIRAKLE_ON
        } else {
            e.presentation.icon = PluginIcons.MIRAKLE_OFF
        }
    }

    private fun showErrorCreatePropsFile(project: Project) {
        errorNotificationGroup.createNotification(
            stringResourceService.getString(ERROR),
            stringResourceService.getString(TOGGLE_MIRAKLE),
            stringResourceService.getString(ERROR_CREATE_LOCAL_PROPS),
            NotificationType.ERROR
        ).notify(project)
    }

    private fun showErrorCreateMirakleProperty(project: Project) {
        errorNotificationGroup.createNotification(
            stringResourceService.getString(ERROR),
            stringResourceService.getString(TOGGLE_MIRAKLE),
            stringResourceService.getString(ERROR_CREATE_MIRAKLE_TOGGLE_PROPERTY),
            NotificationType.ERROR
        ).notify(project)
    }

    private fun getLocalPropertiesFile(project: Project): PropertiesFile? {
        val localPropsPsiFile = FilenameIndex.getFilesByName(project, LOCAL_PROPS_FILE_NAME, projectScope(project))
            .firstOrNull { it.parent?.virtualFile?.isProjectRootFile(project) ?: false }
        return localPropsPsiFile as? PropertiesFile
    }

    private fun createLocalPropsFile(project: Project): PropertiesFile? {
        runWriteCommandAction(project) {
            ProjectRootManager.getInstance(project)
                .contentRoots.firstOrNull { it.isProjectRootFile(project) }
                ?.createChildData(null, LOCAL_PROPS_FILE_NAME)
        }
        return getLocalPropertiesFile(project)
    }

    private fun PropertiesFile.addNewProperty(key: String, value: String): IProperty? {
        return runWriteCommandAction<IProperty>(project) {
            try {
                addPropertyAfter(key, value, properties.lastOrNull())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun VirtualFile.isProjectRootFile(project: Project): Boolean {
        return isDirectory && path == project.basePath
    }

    private companion object {
        const val ENABLE_MIRAKLE = "true"
        const val DISABLE_MIRAKLE = "false"
        const val LOCAL_PROPS_FILE_NAME = "local.properties"
        const val MIRAKLE_ENABLE_PROPERTY_KEY = "mirakle_enabled"
    }
}
