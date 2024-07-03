package com.helpers.flywayhelper.actions

import com.helpers.flywayhelper.helpers.FlywayMigrationHelper
import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task


class RefreshMigrationAction : AnAction() {



    /**
     * @param e
     */
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val flywayMigrationHelper = FlywayMigrationHelper(project)
        val syncBranch = SettingStorageHelper.getSyncBranch()
        val syncedWithText = if (syncBranch == null) "" else "and syncing with branch $syncBranch"

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Refreshing migrations") {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Refreshing migrations ${syncedWithText}..."
                flywayMigrationHelper.refreshMigrations()
            }

            override fun onSuccess() {
                super.onSuccess()
                Notifier(project).notifyInfo("Refresh done !!")
            }

            override fun onThrowable(error: Throwable) {
                super.onThrowable(error)
                Notifier(project).notifyError("An error has occurred. Try later")
            }
        })
    }


    /**
     * Only make this action visible when new file on migration sub folders
     * @param e
     */
    override fun update(e: AnActionEvent) {
        val launchedFromFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (launchedFromFile?.path?.contains("migration/(ddl|dml)") != true) {
            e.presentation.isEnabledAndVisible = true
        }
    }
}