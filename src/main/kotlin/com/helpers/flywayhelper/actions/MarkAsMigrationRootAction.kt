package com.helpers.flywayhelper.actions

import com.helpers.flywayhelper.utils.notifications.Notifier
import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.helpers.flywayhelper.utils.terminal.TerminalClient
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages


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

        SettingStorageHelper.setMigrationRootFolderPath(vf.path)
    }

    override fun update(e: AnActionEvent) {
        val launchedFromFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = launchedFromFile?.isDirectory == true
    }

    companion object {

        @JvmStatic
        fun getInstance(): ConfigureSyncBranchAction {
            return ConfigureSyncBranchAction()
        }
    }
}