package com.helpers.flywayhelper.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import java.util.*

class FlywayMigrationHelper(private val project: Project) {

    private fun getMigrationFiles(): Map<String, List<FlywayMigrationFile>> {
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
        val (firstPartVersion, secondPartVersion, thirdPartVersion) = migrationFiles.values.flatten()
                .filter { it.isValidMigration() && it.getType() == MigrationType.VERSIONED}
                .maxByOrNull { it.getVersion()!! }!!
                .getVersionParts()!!
                .map { it.toInt() }
        val intermediateNextMigration = ("$firstPartVersion$secondPartVersion$thirdPartVersion".toInt() + 1).toString()

        return try {
            "V${intermediateNextMigration[0]}_${intermediateNextMigration[1]}_${intermediateNextMigration.subSequence(2, intermediateNextMigration.length).padStart(3, '0')}"
        }
        catch (_: Exception) {
            ""
        }
    }

    fun exists(flywayMigrationFile: FlywayMigrationFile): Boolean {
        val migrationFiles = getMigrationFiles()
        return Objects.nonNull(
                migrationFiles.values.flatten()
                .filter { it.isValidMigration() }
                .find { it.hasConflict(flywayMigrationFile) }
        )
    }
}
