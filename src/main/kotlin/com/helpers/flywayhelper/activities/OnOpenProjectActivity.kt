package com.helpers.flywayhelper.activities

import com.helpers.flywayhelper.utils.notifications.Notifier
import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class OnOpenProjectActivity : StartupActivity, DumbAware {
    override fun runActivity(project: Project) {
        if (SettingStorageHelper.isOnInstall()) {
            val notifier = Notifier(project)
            notifier.howToUseNotify()
            SettingStorageHelper.markFalseOnInstall()
        }
    }
}