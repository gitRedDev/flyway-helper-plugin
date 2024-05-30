package com.helpers.flywayhelper

import com.helpers.flywayhelper.utils.FlywayMigrationHelper
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import org.apache.commons.lang3.StringUtils


class CreateMigrationAction : AnAction() {

    /**
     * @param e
     */
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.getRequiredData(CommonDataKeys.PROJECT)

        val launchedFromFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
        val launchedFromDir = if (launchedFromFile.isDirectory) launchedFromFile else launchedFromFile.parent

        val flywayMigrationHelper = FlywayMigrationHelper(project)
        val nextMigrationFileVersion = flywayMigrationHelper.nextMigrationFileVersion()

//        val migrationFileName = Messages.showInputDialog(
//            e.project,
//            "Enter migration file name: ",
//            "New File",
//            Messages.getInformationIcon(),
//                "${nextMigrationFileVersion}__.sql",
//            null,
//            TextRange.from(nextMigrationFileVersion.length + 2 , 0)
//        );

        val migrationFileInputPair = Messages.showInputDialogWithCheckBox(
                "Enter migration file name: ",
                "New File",
                "Use this file as reference",
                false,
                true,
                Messages.getInformationIcon(),
                "${nextMigrationFileVersion}__.sql",
                null,
        );

        if (StringUtils.isNotEmpty(migrationFileInputPair.getFirst())) {

            // Create a new file in the project base directory
            val r = Runnable {
                val newFile = launchedFromDir?.createChildData(this, migrationFileInputPair.getFirst());

                if (newFile == null) {
                    Notifier(project).notifyError("Error while creating the file")
                    return@Runnable
                }

                if (migrationFileInputPair.getSecond()) {
                    VfsUtil.saveText(newFile, VfsUtil.loadText(launchedFromFile))
                }

                // Refresh the virtual file system to show the new file
                VirtualFileManager.getInstance().syncRefresh();
                FileEditorManager.getInstance(project).openFile(newFile, true)
            }
            WriteCommandAction.runWriteCommandAction(project, r)
        }
    }


    /**
     * Only make this action visible when text is selected.
     * @param e
     */
    override fun update(e: AnActionEvent) {
        val launchedFromFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        if (launchedFromFile?.path?.contains("migration/(ddl|dml)") != true) {
            e.presentation.isEnabledAndVisible = true
        }
    }
}