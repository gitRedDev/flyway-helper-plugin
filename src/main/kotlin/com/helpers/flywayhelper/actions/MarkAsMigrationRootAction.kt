package com.helpers.flywayhelper.actions

import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager


class MarkAsMigrationRootAction : AnAction() {

    /**
     * @param e
     */
    override fun actionPerformed(e: AnActionEvent) {

        val vf = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (vf == null || !vf.isDirectory) {
            Notifier(null).notifyError("An error has occurred")
            return
        }

        val project = e.getData(CommonDataKeys.PROJECT)
        if (project == null) {
            Notifier(null).notifyError("An error has occurred")
            return
        }

        SettingStorageHelper.setMigrationRootFolderPath(vf.path)
        ApplicationManager.getApplication().invokeLater { ProjectView.getInstance(project).refresh() }
    }

    override fun update(e: AnActionEvent) {
        val launchedFromFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val migrationRootFolderPath = SettingStorageHelper.getMigrationRootFolderPath()
        e.presentation.isEnabledAndVisible = launchedFromFile?.isDirectory == true &&
                launchedFromFile.path != migrationRootFolderPath
    }
}