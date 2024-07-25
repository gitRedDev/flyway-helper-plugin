package com.helpers.flywayhelper.helpers

import com.helpers.flywayhelper.Constants.LOCAL_BRANCH
import com.helpers.flywayhelper.Constants.MIGRATION_DIR_PATH
import com.helpers.flywayhelper.entities.FlywayMigrationFile
import com.helpers.flywayhelper.enums.MigrationNature
import com.helpers.flywayhelper.enums.MigrationTypeEnum
import com.helpers.flywayhelper.utils.terminal.TerminalClient
import com.intellij.openapi.project.Project
import java.util.*


class FlywayMigrationHelper(project: Project, private val branch: String = LOCAL_BRANCH) {

    private val terminalClient = TerminalClient(project)
    private var migrationFiles: List<FlywayMigrationFile>? = null

    init {
        migrationFiles = getSyncedMigrationFiles()
    }

    private fun getLocalMigrationFiles(): List<FlywayMigrationFile> {
        val trackedMigrations = terminalClient.exec("git ls-files $MIGRATION_DIR_PATH")
                .map { it.split(Regex("$MIGRATION_DIR_PATH/(dml|ddl)/"))[1] }
                .map { FlywayMigrationFile(MigrationNature.UNKNOWN, it) }
        val untrackedMigrations = terminalClient.exec("git ls-files -o $MIGRATION_DIR_PATH")
                .map { it.split(Regex("$MIGRATION_DIR_PATH/(dml|ddl)/"))[1] }
                .map { FlywayMigrationFile(MigrationNature.DML, it) }

        return listOf(trackedMigrations, untrackedMigrations).flatten()
    }

    private fun getMigrationFiles(branch: String): List<FlywayMigrationFile> {

        return terminalClient.exec("git ls-tree -r --name-only $branch $MIGRATION_DIR_PATH")
                .map { it.split(Regex("$MIGRATION_DIR_PATH/(dml|ddl)/"))[1] }
                .map { FlywayMigrationFile(MigrationNature.UNKNOWN, it) }
    }

    private fun getSyncedMigrationFiles(): List<FlywayMigrationFile> {
        val localMigrations = getLocalMigrationFiles()
        if (branch == LOCAL_BRANCH) {
            return localMigrations
        }
        val branchMigrations = getMigrationFiles(branch)

        return listOf(localMigrations, branchMigrations).flatten().distinct()
    }

    fun nextMigrationFileVersion(): String {
        if (migrationFiles == null) {
            migrationFiles = getSyncedMigrationFiles()
        }
        val nextMigrationVersion = migrationFiles!!
                .filter { it.isValidMigration() && it.getType() == MigrationTypeEnum.VERSIONED }
                .maxByOrNull { it.getVersion()!!.getVersionNumberRepresentation()!! }!!
                .getVersion()!!
                .nextMigrationVersion()!!

        return try {
            "V${nextMigrationVersion.getVersionString()}"
        } catch (_: Exception) {
            ""
        }
    }

    fun exists(flywayMigrationFile: FlywayMigrationFile): Boolean {
        if (migrationFiles == null) {
            migrationFiles = getSyncedMigrationFiles()
        }
        return Objects.nonNull(
                migrationFiles!!
                        .filter { it.isValidMigration() }
                        .find { it.hasConflict(flywayMigrationFile) }
        )
    }

    fun refreshMigrations() {
        terminalClient.execVoid("git fetch", true)
    }
}