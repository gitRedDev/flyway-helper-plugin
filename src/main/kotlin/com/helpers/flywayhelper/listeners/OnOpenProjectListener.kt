package com.helpers.flywayhelper.listeners

import com.helpers.flywayhelper.utils.notifications.Notifier
import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener


class OnOpenProjectListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        //onInstall
        if (SettingStorageHelper.isOnInstall()) {
            val notifier = Notifier(project)
            notifier.howToUseNotify()
            SettingStorageHelper.markFalseOnInstall()
        }
    }
}