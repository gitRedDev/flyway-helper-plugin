package com.helpers.flywayhelper.entities

import com.helpers.flywayhelper.enums.MigrationNature
import com.helpers.flywayhelper.enums.MigrationTypeEnum

class FlywayMigrationFile(private val nature: MigrationNature, private val name: String){

    private val POSSIBLE_MIGRATION_COMMANDS = MigrationTypeEnum.values().map { it.getAlias() }


    private fun getNature(): String {
        return nature.name
    }

    fun getType(): MigrationTypeEnum? {
        return if (isValidMigration()) MigrationTypeEnum.byAlias(name[0]) else null
    }

    private fun getPrefix(): Char? {
        return if (isValidPrefix()) name[0] else null
    }

    fun getVersion(): MigrationVersion? {
        return if (canConstructMigrationVersion()) MigrationVersion(name.subSequence(1, name.length).split("__")[0]) else null
    }

    private fun getDescriptiveName(): String? {
        return if (isValidDescriptiveName()) name.subSequence(1, name.length).split("__")[1].split(".")[0] else null
    }

    private fun getSuffix(): String? {
        return if (isValidSuffix()) name.subSequence(1, name.length).split("__")[1].split(".")[1] else null
    }

    override fun toString(): String {
        return if (isValidMigration()) "${getNature()}: ${getPrefix()} => ${getVersion()} => ${getDescriptiveName()} => ${getSuffix()}" else "Invalid migration: $name"
    }

    private fun isValidPrefix(): Boolean {
        return prefixError() == null
    }

    private fun prefixError(): String? {
        return try {
            if (name[0] in POSSIBLE_MIGRATION_COMMANDS) null else "Prefix should be one of $POSSIBLE_MIGRATION_COMMANDS"
        } catch (_: Exception) {
            "Prefix should be one of $POSSIBLE_MIGRATION_COMMANDS"
        }
    }

    private fun canConstructMigrationVersion(): Boolean {
        return try {
            name.subSequence(1, name.length).split("__")[0].isNotBlank()
        } catch (_: Exception) {
            false
        }
    }

    private fun isValidDescriptiveName(): Boolean {
        return descriptiveNameError() == null
    }

    private fun descriptiveNameError(): String? {
        return try {
            if (name.subSequence(1, name.length).split("__")[1].split(".")[0].isNotBlank()) null else "migration name should not be blank"
        } catch (_: Exception) {
            "migration name should not be blank"
        }
    }

    private fun isValidSuffix(): Boolean {
        return suffixError() == null
    }

    private fun suffixError(): String? {
        return try {
            if (name.subSequence(1, name.length).split("__")[1].split(".")[1].isNotBlank()) null else "file extension should not be blank"
        } catch (_: Exception) {
            "file extension should not be blank"
        }
    }

    fun isValidMigration(): Boolean {
        return migrationError() == null
    }

    fun migrationError(): String? {
        return prefixError()
                ?: (if (getVersion()?.versionError() == null) null else getVersion()?.versionError())
                ?: descriptiveNameError()
                ?: suffixError()

    }

    fun hasConflict(flywayMigrationFile: FlywayMigrationFile): Boolean {
        return flywayMigrationFile.isValidMigration() &&
                this.isValidMigration() &&
                flywayMigrationFile.getVersion()!! == this.getVersion() &&
                flywayMigrationFile.getPrefix()!! == getPrefix()
    }

    companion object {
        @JvmStatic
        fun of(nature: MigrationNature, name: String): FlywayMigrationFile {
            return FlywayMigrationFile(nature, name)
        }
    }
}