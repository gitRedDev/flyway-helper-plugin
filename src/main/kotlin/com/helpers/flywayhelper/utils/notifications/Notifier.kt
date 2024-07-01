package com.helpers.flywayhelper.utils.notifications

import com.helpers.flywayhelper.Constants.CONFIGURE_SYNC_BRANCH_ACTION
import com.helpers.flywayhelper.Constants.PLUGIN_NAME
import com.helpers.flywayhelper.Constants.NOTIFICATION_GROUP_ID
import com.helpers.flywayhelper.Constants.READ_MORE_ACTION
import com.helpers.flywayhelper.Constants.WELCOME_MESSAGE
import com.helpers.flywayhelper.actions.ConfigureSyncBranchAction
import com.helpers.flywayhelper.actions.ReadMoreAction
import com.intellij.icons.AllIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class Notifier(private val project: Project?) {


        private fun createNotification(message: String, type: NotificationType, actions: Collection<AnAction>): Notification {
            val notification = NotificationGroupManager.getInstance()
                    .getNotificationGroup(NOTIFICATION_GROUP_ID)
                    .createNotification(message, type)

            notification.setTitle(PLUGIN_NAME)
            notification.setIcon(AllIcons.Ide.Rating)
            notification.addActions(actions)
            return notification
        }

        private fun notify(message: String, type: NotificationType, actions: List<AnAction>) {
            createNotification(message, type, actions)
                    .notify(project)
        }

        fun notify(message: String, type: NotificationType = NotificationType.INFORMATION) {
            notify(message, type, emptyList())
        }

        fun notifyError(message: String) {
            notify(message, NotificationType.ERROR)
        }

        fun howToUseNotify() {
            val configureSyncBranchAction = ConfigureSyncBranchAction.getInstance()
            val readMoreAction = ReadMoreAction.getInstance()
            val actions = listOf(
                    object : NotificationAction(READ_MORE_ACTION) {
                        override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                            readMoreAction.actionPerformed(e)
                        }
                    },
                    object : NotificationAction(CONFIGURE_SYNC_BRANCH_ACTION) {
                        override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                           configureSyncBranchAction.actionPerformed(e)
                        }
                    }
            )
            notify(WELCOME_MESSAGE, NotificationType.INFORMATION, actions)
        }
}
