package com.mercuriy94.startup

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.mercuriy94.service.MirakleNotificationService
import com.mercuriy94.service.MiraklePropertiesService
import com.mercuriy94.service.MiraklePropertiesService.Companion.MIRAKLE_ENABLE_PROPERTY_KEY

class ToggleMirakleStartup : StartupActivity {

    override fun runActivity(project: Project) {
        project.service<MiraklePropertiesService>().apply {
            val localProps = getMirakleFileProperties() ?: createLocalPropsFile()
            if (localProps == null) {
                project.service<MirakleNotificationService>().showErrorCreatePropsFile()
            } else {
                if (localProps.findPropertyByKey(MIRAKLE_ENABLE_PROPERTY_KEY) == null &&
                    !setEnableMirakle(localProps, true)
                ) {
                    project.service<MirakleNotificationService>().showErrorCreateMirakleProperty()
                }
            }
        }
    }
}
