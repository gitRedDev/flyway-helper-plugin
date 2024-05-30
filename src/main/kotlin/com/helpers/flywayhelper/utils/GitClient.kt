package com.helpers.flywayhelper.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.vcsUtil.VcsUtil
import java.io.File


class GitClient(private val project: Project){

    private val projectDir: File? = project.guessProjectDir()?.let { VcsUtil.getFilePath(it).ioFile }


    private fun getRemoteBranches(): List<String> {
        val process = Runtime.getRuntime().exec("git branch -r", null, projectDir)
        process.waitFor()
        return process.inputStream.bufferedReader().use { it.readLines().map { line -> line.split("  ")[1] } }
    }

    private fun getDirFiles(branch: String, dir: String): List<String> {
        val process = Runtime.getRuntime().exec("git ls-tree --name-only -r $branch $dir", null, projectDir)
        process.waitFor()
        return process.inputStream.bufferedReader().use { it.readLines() }
    }

    fun getRemoteMigrations(): Map<String, MutableSet<String>> {
        val dir = "src/main/resources/db/migration"
        val ddlDir = "$dir/ddl/"
        val dmlDir = "$dir/dml/"
        val remoteBranches = getRemoteBranches()

        val ddlMigrations = mutableSetOf<String>()
        val dmlMigrations = mutableSetOf<String>()

        remoteBranches.forEach { branch ->
            val branchDDlMigrations = getDirFiles(branch, ddlDir).map { p -> p.replace(ddlDir, "") }
            ddlMigrations.addAll(branchDDlMigrations)

            val branchDMlMigrations = getDirFiles(branch, dmlDir).map { p -> p.replace(dmlDir, "") }
            dmlMigrations.addAll(branchDMlMigrations)
        }
        return mapOf(
            "ddl" to ddlMigrations,
            "dml" to dmlMigrations
        )
    }
}
