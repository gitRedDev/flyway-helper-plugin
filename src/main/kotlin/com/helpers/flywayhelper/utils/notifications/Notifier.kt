package com.helpers.flywayhelper.utils.notifications

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class Notifier(private val project: Project?) {


        fun notify(message: String, type: NotificationType?) {
            NotificationGroupManager.getInstance()
                    .getNotificationGroup("Default")
                    .createNotification(message, type?: NotificationType.INFORMATION)
                    .notify(project)
        }

        fun notifyError(message: String) {
            notify(message, NotificationType.ERROR)
        }
}
