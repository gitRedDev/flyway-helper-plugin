package com.helpers.flywayhelper.helpers

import com.helpers.flywayhelper.Constants.LOCAL_BRANCH
import com.helpers.flywayhelper.entities.FlywayMigrationFile
import com.helpers.flywayhelper.enums.MigrationNature
import com.helpers.flywayhelper.enums.MigrationTypeEnum
import com.helpers.flywayhelper.utils.terminal.TerminalClient
import com.intellij.openapi.project.Project
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.name


class FlywayMigrationHelper(project: Project, private val branch: String = LOCAL_BRANCH) {

    private val terminalClient = TerminalClient(project)
    private var migrationFiles: List<FlywayMigrationFile>? = null

    init {
        migrationFiles = getSyncedMigrationFiles()
    }

    private fun getLocalMigrationFiles(): List<FlywayMigrationFile> {
        val migrationRootFolderPath = SettingStorageHelper.getMigrationRootFolderPath()
        val trackedMigrations = terminalClient.exec("git ls-files $migrationRootFolderPath")
                .map { Path(it).fileName.name }
                .map { FlywayMigrationFile(MigrationNature.UNKNOWN, it) }
        val untrackedMigrations = terminalClient.exec("git ls-files -o $migrationRootFolderPath")
                .map { Path(it).fileName.name }
                .map { FlywayMigrationFile(MigrationNature.UNKNOWN, it) }

        return listOf(trackedMigrations, untrackedMigrations).flatten()
    }

    private fun getMigrationFiles(branch: String): List<FlywayMigrationFile> {
        val migrationRootFolderPath = SettingStorageHelper.getMigrationRootFolderPath()

        return terminalClient.exec("git ls-tree -r --name-only $branch $migrationRootFolderPath")
                .map { Path(it).fileName.name }
                .map { FlywayMigrationFile(MigrationNature.UNKNOWN, it) }
                .filter { it.isValidMigration() }
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
                .filter { it.getType() == MigrationTypeEnum.VERSIONED }
                .maxByOrNull { it.getVersion()!!.getVersionNumberRepresentation()!! }
                ?.getVersion()
                ?.nextMigrationVersion()

        return try {
            "V${nextMigrationVersion?.getVersionString() ?: ""}"
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