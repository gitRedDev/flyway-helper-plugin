package com.helpers.flywayhelper

import com.helpers.flywayhelper.utils.FlywayMigrationFile
import com.helpers.flywayhelper.utils.FlywayMigrationHelper
import com.helpers.flywayhelper.utils.MigrationNature
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import org.apache.commons.lang3.StringUtils


class CreateMigrationAction : AnAction() {

    /**
     * @param e
     */
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

        val flywayMigrationHelper = FlywayMigrationHelper(project)
        val nextMigrationFileVersion = flywayMigrationHelper.nextMigrationFileVersion()

        val migrationFileInputPair = Messages.showInputDialogWithCheckBox(
                "Enter migration file name: ",
                "New File",
                "Use this file as reference",
                false,
                true,
                Messages.getInformationIcon(),
                "${nextMigrationFileVersion}__.sql",
                object : InputValidatorEx {
                    override fun checkInput(inputString: String?): Boolean {
                        val flywayMigrationFile = FlywayMigrationFile.of(MigrationNature.UNKNOWN, inputString!!)

                        return StringUtils.isNotBlank(inputString)
                                && FlywayMigrationFile.of(MigrationNature.UNKNOWN, inputString).isValidMigration()
                                && !flywayMigrationHelper.exists(flywayMigrationFile)
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

            val r = Runnable {
                val newFile = launchedFromDir?.createChildData(this, migrationFileInputPair.getFirst())

                if (newFile == null) {
                    Notifier(project).notifyError("Error while creating the file")
                    return@Runnable
                }

                if (migrationFileInputPair.getSecond()) {
                    VfsUtil.saveText(newFile, VfsUtil.loadText(launchedFromFile))
                }

                // Refresh the virtual file system to show the new file
                VirtualFileManager.getInstance().syncRefresh()
                FileEditorManager.getInstance(project).openFile(newFile, true)
            }
            WriteCommandAction.runWriteCommandAction(project, r)
        }
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