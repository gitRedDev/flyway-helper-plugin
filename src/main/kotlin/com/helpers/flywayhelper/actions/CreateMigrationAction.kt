package com.helpers.flywayhelper.actions

import com.helpers.flywayhelper.Constants
import com.helpers.flywayhelper.Constants.LOCAL_BRANCH
import com.helpers.flywayhelper.entities.FlywayMigrationFile
import com.helpers.flywayhelper.helpers.FlywayMigrationHelper
import com.helpers.flywayhelper.enums.MigrationNature
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import org.apache.commons.lang3.StringUtils


class CreateMigrationAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.getData(CommonDataKeys.PROJECT)
        if (project == null) {
            Notifier(null).notifyError("An error has occurred")
            return
        }

        val launchedFromFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (launchedFromFile == null) {
            Notifier(null).notifyError("An error has occurred")
            return
        }
        val launchedFromDir = if (launchedFromFile.isDirectory) launchedFromFile else launchedFromFile.parent

        val syncBranch = SettingStorageHelper.getSyncBranch() ?: LOCAL_BRANCH
        val syncBranchStringMessage = if (syncBranch != LOCAL_BRANCH) "(synced with $syncBranch)" else ""

        val flywayMigrationHelper = FlywayMigrationHelper(project, syncBranch)
        val nextMigrationFileVersion = flywayMigrationHelper.nextMigrationFileVersion()

        val migrationFileInputPair = Messages.showInputDialogWithCheckBox(
                "Enter migration file name: $syncBranchStringMessage",
                "New File",
                "Use this file as reference",
                false,
                !launchedFromFile.isDirectory,
                Messages.getInformationIcon(),
                "${nextMigrationFileVersion}__.sql",
                object : InputValidatorEx {
                    override fun checkInput(inputString: String?): Boolean {
                        return getErrorText(inputString) == null
                    }

                    override fun canClose(inputString: String?): Boolean {
                        return checkInput(inputString)
                    }

                    override fun getErrorText(inputString: String?): String? {
                        val flywayMigrationFile = FlywayMigrationFile.of(MigrationNature.UNKNOWN, inputString!!)

                        return if (StringUtils.isBlank(inputString) || !flywayMigrationFile.isValidMigration()) {
                            "Not a valid migration name"
                        } else if (flywayMigrationHelper.exists(flywayMigrationFile)) {
                            "Migration with version ${flywayMigrationFile.getVersion()} already exists"
                        } else {
                            null
                        }
                    }
                }
        )

        if (StringUtils.isNotEmpty(migrationFileInputPair.getFirst())) {
            var newFile: VirtualFile? = null
            val r = Runnable {
                newFile = launchedFromDir?.createChildData(this, migrationFileInputPair.getFirst())

                if (newFile == null) {
                    Notifier(project).notifyError("Error while creating the file")
                    return@Runnable
                }

                if (migrationFileInputPair.getSecond()) {
                    VfsUtil.saveText(newFile!!, VfsUtil.loadText(launchedFromFile))
                }

                // Refresh the virtual file system to show the new file
                VirtualFileManager.getInstance().syncRefresh()
            }

            WriteCommandAction.runWriteCommandAction(project, r)
            newFile?.let { FileEditorManager.getInstance(project).openFile(it, true) }

        }
    }


    /**
     * Only make this action visible when new file on migration sub folders
     * @param e
     */
    override fun update(e: AnActionEvent) {
        val launchedFromFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val migrationRootFolderPath = SettingStorageHelper.getMigrationRootFolderPath() ?: ""

        e.presentation.isEnabledAndVisible = StringUtils.isNotBlank(migrationRootFolderPath) &&
                launchedFromFile?.path?.contains(migrationRootFolderPath) == true
    }
}