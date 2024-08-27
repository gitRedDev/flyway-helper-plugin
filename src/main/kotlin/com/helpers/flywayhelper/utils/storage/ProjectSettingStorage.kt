package com.helpers.flywayhelper.utils.storage

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.helpers.flywayhelper.Constants
import com.helpers.flywayhelper.Constants.BY_PROJECT_SETTING_FILE
import com.helpers.flywayhelper.Constants.PLUGIN_DIRECTORY_PATH
import com.helpers.flywayhelper.utils.notifications.Notifier
import com.helpers.flywayhelper.utils.vfs.CustomVfsUtil
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.jetbrains.rd.util.string.printToString
import java.nio.file.Paths

class ProjectSettingStorage(val project: Project): SettingStorage<String> {

    private val customVfsUtil = CustomVfsUtil(project)

    override fun putSetting(key: String, value: String): String {

        val r = Runnable {
            val fileVf = customVfsUtil.findOrCreateFileAndOpen(PLUGIN_DIRECTORY_PATH, BY_PROJECT_SETTING_FILE, false) ?: return@Runnable

            val fileMap: MutableMap<Any?, Any?> = try {
                ObjectMapper().readValue(fileVf.inputStream, Map::class.java).toMutableMap()
            } catch (e: MismatchedInputException) {
                mutableMapOf()
            }

            fileMap[key] = value
            customVfsUtil.createOrUpdateFileAndOpen(PLUGIN_DIRECTORY_PATH, BY_PROJECT_SETTING_FILE, ObjectMapper().writeValueAsString(fileMap), false)
        }
        WriteCommandAction.runWriteCommandAction(project, r)

        return value
    }

    override fun getSetting(key: String): String? {
        val baseDir = project.guessProjectDir()

        val settingFileVf = baseDir?.findFileByRelativePath("$PLUGIN_DIRECTORY_PATH/$BY_PROJECT_SETTING_FILE")?: return null
        return ObjectMapper().readTree(settingFileVf.inputStream)[key]?.toString()?.replace("\"", "");
}
}