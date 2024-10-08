package com.helpers.flywayhelper.actions

import com.helpers.flywayhelper.Constants.PLUGIN_DIRECTORY_PATH
import com.helpers.flywayhelper.Constants.PLUGIN_README_IMAGE_NAME
import com.helpers.flywayhelper.Constants.PLUGIN_README_NAME
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.helpers.flywayhelper.utils.vfs.CustomVfsUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import java.nio.file.Paths

class ReadMoreAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val baseDir = project?.guessProjectDir()
        if (project == null || baseDir == null) {
            Notifier(project).notifyError("An error has occurred")
            return
        }

        val customVfsUtil = CustomVfsUtil(project)

        val r = Runnable {
            val pluginReadmeContent = javaClass.classLoader.getResourceAsStream("plugin/${PLUGIN_README_NAME}")
            val pluginImageContent = javaClass.classLoader.getResourceAsStream("plugin/${PLUGIN_README_IMAGE_NAME}")

            if (pluginReadmeContent == null || pluginImageContent == null) {
                Notifier(project).notifyError("An error has occurred")
                return@Runnable
            }

            val files = mapOf(
                    PLUGIN_README_NAME to pluginReadmeContent,
                    PLUGIN_README_IMAGE_NAME to pluginImageContent
            )
            customVfsUtil.createOrUpdateFilesFromInputStreamAndOpen(PLUGIN_DIRECTORY_PATH, files, PLUGIN_README_NAME)
        }

        try {
            val existingReadmeVf = VfsUtil.findFile(Paths.get("${baseDir.path}/${PLUGIN_DIRECTORY_PATH}/${PLUGIN_README_NAME}"), true)
            if (existingReadmeVf != null) {
                FileEditorManager.getInstance(project).openFile(existingReadmeVf, true)
                return
            }
            WriteCommandAction.runWriteCommandAction(project, r)
        } catch (e: Exception) {
            Notifier(project).notifyError("An error has occurred.")
        }
    }

    companion object {

        @JvmStatic
        fun getInstance(): ReadMoreAction {
            return ReadMoreAction()
        }
    }
}