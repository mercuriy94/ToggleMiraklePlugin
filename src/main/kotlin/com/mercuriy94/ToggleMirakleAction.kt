package com.mercuriy94

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.mercuriy94.service.MirakleNotificationService
import com.mercuriy94.service.MiraklePropertiesService
import icons.PluginIcons

class ToggleMirakleAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        project.service<MiraklePropertiesService>().apply {
            val localProps = getMirakleFileProperties() ?: createLocalPropsFile()
            if (localProps == null) {
                project.service<MirakleNotificationService>().showErrorCreatePropsFile()
            } else {
                val enabled = isMirakleEnabled(localProps)
                val newValue = !enabled
                val newIcon = if (setEnableMirakle(localProps, newValue)) {
                    if (newValue) {
                        PluginIcons.MIRAKLE_ON
                    } else {
                        PluginIcons.MIRAKLE_OFF
                    }
                } else {
                    project.service<MirakleNotificationService>().showErrorCreateMirakleProperty()
                    PluginIcons.MIRAKLE_OFF
                }
                e.presentation.icon = newIcon
            }
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val project = e.project ?: return
        val propsService = project.service<MiraklePropertiesService>()
        val currentValue = propsService.getMirakleFileProperties()
            ?.let { propsService.isMirakleEnabled(it) }
            ?: false

        if (currentValue) {
            e.presentation.icon = PluginIcons.MIRAKLE_ON
        } else {
            e.presentation.icon = PluginIcons.MIRAKLE_OFF
        }
    }
}
