package com.helpers.flywayhelper.utils.terminal

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.vcsUtil.VcsUtil
import java.io.File


class TerminalClient(private val project: Project){

    private val projectDir: File? = project.guessProjectDir()?.let { VcsUtil.getFilePath(it).ioFile }


    fun exec(command: String): List<String> {
        val process = Runtime.getRuntime().exec(command, null, projectDir)
        return process.inputStream.bufferedReader().use { it.readLines() }
    }

    fun execVoid(command: String, waitFor: Boolean) {
        val process = Runtime.getRuntime().exec(command, null, projectDir)
        if (waitFor) {
            process.waitFor()
        }
    }

}
