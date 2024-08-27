package com.helpers.flywayhelper.actions

import com.helpers.flywayhelper.utils.notifications.Notifier
import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.helpers.flywayhelper.utils.terminal.TerminalClient
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages


class ConfigureSyncBranchAction : AnAction() {

    /**
     * @param e
     */
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.getData(CommonDataKeys.PROJECT)
        if (project == null) {
            Notifier(null).notifyError("An error has occurred")
            return
        }

        val terminalClient = TerminalClient(project)
        val branches = terminalClient.exec("git branch -r --format=%(refname:short)\n")

        val settingStorageHelper = SettingStorageHelper(project)
        val defaultValue =  settingStorageHelper.getSyncBranch() ?: if(branches.isNotEmpty()) branches[0] else null
        val branch = Messages.showEditableChooseDialog(
                "Choose a branch to sync your migration naming with while creating a new one",
                "Configure Branch Sync",
                Messages.getInformationIcon(),
                branches.toTypedArray(),
                defaultValue,
                object : InputValidator {
                    override fun checkInput(inputString: String?): Boolean {
                        if (inputString == null) {
                            return false
                        }
                        return branches.any { it == inputString }
                    }

                    override fun canClose(inputString: String?): Boolean {
                        return checkInput(inputString)
                    }
                },
        )

        if (branch != null) {
            settingStorageHelper.setSyncBranch(branch)
        }
    }

    companion object {

        @JvmStatic
        fun getInstance(): ConfigureSyncBranchAction {
            return ConfigureSyncBranchAction()
        }
    }
}