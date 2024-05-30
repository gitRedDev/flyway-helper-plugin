package com.helpers.flywayhelper.utils;

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

class FlywayMigrationHelper(private val project: Project) {


    fun getMigrationFiles(): Map<String, List<FlywayMigrationFile>> {
        val baseDir: VirtualFile? = project.guessProjectDir()
        val ddlDir: VirtualFile? = baseDir?.findFileByRelativePath("src/main/resources/db/migration/ddl")
        val dmlDir: VirtualFile? = baseDir?.findFileByRelativePath("src/main/resources/db/migration/dml")

        val ddlMigrations = ddlDir?.children?.map { FlywayMigrationFile(MigrationNature.DDL, it.name) }?.sortedBy { it.getVersion() }.orEmpty()
        val dmlMigrations = dmlDir?.children?.map { FlywayMigrationFile(MigrationNature.DML, it.name) }?.sortedBy { it.getVersion() }.orEmpty()

        return mapOf(
                "ddl" to ddlMigrations,
                "dml" to dmlMigrations
        )
    }

    fun nextMigrationFileVersion(): String {
        val migrationFiles = getMigrationFiles()
        val maxVersionSplit = migrationFiles.values.flatten().map { it.getVersion() }.maxOf { it }.split("_")
        return try {
            "${maxVersionSplit[0]}_${maxVersionSplit[1]}_${(maxVersionSplit[2].toInt() + 1).toString().padStart(3, '0')}"
        }
        catch (_: Exception) {
            ""
        }
    }

    class FlywayMigrationFile(private val nature: MigrationNature, private val name: String){
        override fun toString(): String {
            return "${getNature()}: ${getVersion()} => ${getDescriptiveName()}"
        }

        private fun getNature(): String {
            return nature.name
        }

        private fun getDescriptiveName(): String {
            return name.split("__")[1]
        }

        fun getVersion(): String {
            return name.split("__")[0]
        }

    }

    enum class MigrationNature {
        DDL,
        DML
    }
}
