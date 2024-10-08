package com.helpers.flywayhelper.actions

import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager


class UnMarkAsMigrationRootAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

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

        SettingStorageHelper(project).removeMigrationRootFolderPath()
        ApplicationManager.getApplication().invokeLater { ProjectView.getInstance(project).refresh() }
    }

    override fun update(e: AnActionEvent) {
        val launchedFromFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val migrationRootFolderPath = SettingStorageHelper(project).getMigrationRootFolderPath()
        e.presentation.isEnabledAndVisible = launchedFromFile?.isDirectory == true &&
                launchedFromFile.path == migrationRootFolderPath
    }
}