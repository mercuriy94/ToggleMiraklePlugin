package com.mercuriy94.service

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service
class MirakleNotificationService(private val project: Project) {

    private val errorNotificationGroup by lazy {
        NotificationGroup(
            displayId = "Mirakle error notification group",
            displayType = NotificationDisplayType.BALLOON,
            isLogByDefault = true
        )
    }
    private val stringResourceService by lazy {
        project.service<StringResourceService>()
    }

    fun showErrorCreatePropsFile() {
        errorNotificationGroup.createNotification(
            stringResourceService.getString(ERROR),
            stringResourceService.getString(TOGGLE_MIRAKLE),
            stringResourceService.getString(ERROR_CREATE_LOCAL_PROPS),
            NotificationType.ERROR
        ).notify(project)
    }

    fun showErrorCreateMirakleProperty() {
        errorNotificationGroup.createNotification(
            stringResourceService.getString(ERROR),
            stringResourceService.getString(TOGGLE_MIRAKLE),
            stringResourceService.getString(ERROR_CREATE_MIRAKLE_TOGGLE_PROPERTY),
            NotificationType.ERROR
        ).notify(project)
    }
}