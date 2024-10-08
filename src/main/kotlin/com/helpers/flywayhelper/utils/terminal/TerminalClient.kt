package com.helpers.flywayhelper.utils.terminal

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.vcsUtil.VcsUtil
import java.io.File
import java.util.*


class TerminalClient(private val project: Project){

    private val projectDir: File? = project.guessProjectDir()?.let { VcsUtil.getFilePath(it).ioFile }


    fun exec(command: String): List<String> {

        val process = Runtime.getRuntime().exec(tokenize(command), null, projectDir)
        return process.inputStream.bufferedReader().use { it.readLines() }
    }

    fun execVoid(command: String, waitFor: Boolean) {
        val process = Runtime.getRuntime().exec(tokenize(command), null, projectDir)
        if (waitFor) {
            process.waitFor()
        }
    }

    private fun tokenize(command: String): Array<String?> {

        val st = StringTokenizer(command)
        val cmdArray = arrayOfNulls<String>(st.countTokens())
        var i = 0
        while (st.hasMoreTokens()) {
            cmdArray[i] = st.nextToken()
            i++
        }
        return cmdArray
    }

}

