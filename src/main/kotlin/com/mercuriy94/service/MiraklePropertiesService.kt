package com.mercuriy94.service

import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex.getFilesByName
import com.intellij.psi.search.GlobalSearchScope.projectScope

@Service
class MiraklePropertiesService(private val project: Project) {

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

    fun isMirakleEnabled(propsFile: PropertiesFile): Boolean =
        propsFile.findPropertyByKey(MIRAKLE_ENABLE_PROPERTY_KEY)
            ?.let { it.value == ENABLE_MIRAKLE }
            ?: false

    fun setEnableMirakle(propsFile: PropertiesFile, enable: Boolean): Boolean {
        val enableStr = if (enable) ENABLE_MIRAKLE else DISABLE_MIRAKLE
        val property = propsFile.findPropertyByKey(MIRAKLE_ENABLE_PROPERTY_KEY)
            ?: propsFile.addNewProperty(MIRAKLE_ENABLE_PROPERTY_KEY, DISABLE_MIRAKLE)
            ?: return false

        return runWriteCommandAction<Boolean>(project) {
            try {
                property.setValue(enableStr)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun getMirakleFileProperties(): PropertiesFile? {
        val localPropsPsiFile = getFilesByName(project, LOCAL_PROPS_FILE_NAME, projectScope(project))
            .firstOrNull { it.parent?.virtualFile?.isProjectRootFile(project) ?: false }
        return localPropsPsiFile as? PropertiesFile
    }

    fun createLocalPropsFile(): PropertiesFile? {
        runWriteCommandAction(project) {
            ProjectRootManager.getInstance(project).contentRoots
                .firstOrNull { it.isProjectRootFile(project) }
                ?.createChildData(null, LOCAL_PROPS_FILE_NAME)
        }
        return getMirakleFileProperties()
    }

    companion object {
        const val ENABLE_MIRAKLE = "true"
        const val DISABLE_MIRAKLE = "false"
        const val LOCAL_PROPS_FILE_NAME = "local.properties"
        const val MIRAKLE_ENABLE_PROPERTY_KEY = "mirakle_enabled"
    }
}
