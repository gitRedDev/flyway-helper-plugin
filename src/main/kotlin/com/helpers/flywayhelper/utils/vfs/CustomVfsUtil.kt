package com.helpers.flywayhelper.utils.vfs

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.InputStream

class CustomVfsUtil(private val project: Project) {


    fun findOrCreateFileAndOpen(dir: String, filename: String, open: Boolean): VirtualFile? {
        val baseDir = project.guessProjectDir()

        val dirVf = VfsUtil.createDirectoryIfMissing(baseDir, dir)
        val fileVf = dirVf?.findOrCreateChildData(this, filename) ?: return null

        if (open) {
            FileEditorManager.getInstance(project).openFile(fileVf, true)
        }
        return fileVf
    }

    fun createOrUpdateFileAndOpen(dir: String, filename: String, content: String, open: Boolean): VirtualFile? {
        val baseDir = project.guessProjectDir()

        val dirVf = VfsUtil.createDirectoryIfMissing(baseDir, dir)
        val fileVf = dirVf?.findOrCreateChildData(this, filename) ?: return null

        VfsUtil.saveText(fileVf, content);
        if (open) {
            FileEditorManager.getInstance(project).openFile(fileVf, true)
        }
        return fileVf
    }

    private fun createOrUpdateFileAndOpen(dir: String, filename: String, content: InputStream, open: Boolean): VirtualFile? {
        val baseDir = project.guessProjectDir()

        val dirVf = VfsUtil.createDirectoryIfMissing(baseDir, dir)
        val fileVf = dirVf?.findOrCreateChildData(this, filename) ?: return null

        content.use { input ->
            fileVf.getOutputStream(this).use { output ->
                input.copyTo(output)
            }
        }

        if (open) {
            FileEditorManager.getInstance(project).openFile(fileVf, true)
        }

        return fileVf
    }


    /**
    *
    *  @param dir the directory where to create the files in.
    *  @param files Map<filename, content>
    *  @param toOpen the filename of the file to open (from "files")
    *
     */
    fun createOrUpdateFilesAndOpen(dir: String, files: Map<String, String>, toOpen: String): List<VirtualFile?> {
        return files.entries.map { (k,v) ->  createOrUpdateFileAndOpen(dir, k, v, toOpen == k)}
    }

    /**
     *
     *  @param dir the directory where to create the files in.
     *  @param files Map<filename, content>
     *  @param toOpen the filename of the file to open (from "files")
     *
     */
    fun createOrUpdateFilesFromInputStreamAndOpen(dir: String, files: Map<String, InputStream>, toOpen: String): List<VirtualFile?> {
        return files.entries.map { (k,v) ->  createOrUpdateFileAndOpen(dir, k, v, toOpen == k)}
    }
}