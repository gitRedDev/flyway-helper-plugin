package com.helpers.flywayhelper.helpers

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

class CustomVfsUtil(private val project: Project) {

    private fun findOrCreateFileAndOpen(dir: String, filename: String, content: String, open: Boolean) {
        val baseDir = project.guessProjectDir()

        val dirVf = VfsUtil.createDirectoryIfMissing(baseDir, dir)
        val fileVf = dirVf?.findOrCreateChildData(this, filename) ?: return

        VfsUtil.saveText(fileVf, content);
        if (open) {
            FileEditorManager.getInstance(project).openFile(fileVf, true)
        }
    }

    private fun findOrCreateFileAndOpen(dir: String, filename: String, content: InputStream, open: Boolean) {
        val baseDir = project.guessProjectDir()

        val dirVf = VfsUtil.createDirectoryIfMissing(baseDir, dir)
        val fileVf = dirVf?.findOrCreateChildData(this, filename) ?: return

        content.use { input ->
            fileVf.getOutputStream(this).use { output ->
                input.copyTo(output)
            }
        }

        if (open) {
            FileEditorManager.getInstance(project).openFile(fileVf, true)
        }
    }


    /**
    *
    *  @param dir the directory where to create the files in.
    *  @param files Map<filename, content>
    *  @param toOpen the filename of the file to open (from "files")
    *
     */
    fun findOrCreateFilesAndOpen(dir: String, files: Map<String, String>, toOpen: String) {
        files.entries.forEach { (k,v) ->  findOrCreateFileAndOpen(dir, k, v, toOpen == k)}
    }

    /**
     *
     *  @param dir the directory where to create the files in.
     *  @param files Map<filename, content>
     *  @param toOpen the filename of the file to open (from "files")
     *
     */
    fun findOrCreateFilesFromInputStreamAndOpen(dir: String, files: Map<String, InputStream>, toOpen: String) {
        files.entries.forEach { (k,v) ->  findOrCreateFileAndOpen(dir, k, v, toOpen == k)}
    }
}