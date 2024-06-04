package com.helpers.flywayhelper.utils



class FlywayMigrationFile(private val nature: MigrationNature, private val name: String){

    private val POSSIBLE_MIGRATION_COMMANDS = MigrationType.values().map { it.getAlias() }


    private fun getNature(): String {
        return nature.name
    }

    fun getType(): MigrationType? {
        return if (isValidMigration()) MigrationType.byAlias(name[0]) else null
    }

    private fun getPrefix(): Char? {
        return if (isValidPrefix()) name[0] else null
    }

    fun getVersion(): String? {
        return if (isValidVersion()) name.subSequence(1, name.length).split("__")[0] else null
    }

    fun getVersionParts(): List<String>? {
        return getVersion()?.split("_")
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
        return try {
            name[0] in POSSIBLE_MIGRATION_COMMANDS
        } catch (_: Exception) {
            false
        }
    }

    private fun isValidVersion(): Boolean {
        return try {
            name.subSequence(1, name.length).split("__")[0].isNotBlank()
        } catch (_: Exception) {
            false
        }
    }

    private fun isValidDescriptiveName(): Boolean {
        return try {
            name.subSequence(1, name.length).split("__")[1].split(".")[0].isNotBlank()
        } catch (_: Exception) {
            false
        }
    }

    private fun isValidSuffix(): Boolean {
        return try {
            name.subSequence(1, name.length).split("__")[1].split(".")[1].isNotBlank()
        } catch (_: Exception) {
            false
        }
    }

    fun isValidMigration(): Boolean {
        return isValidPrefix() && isValidVersion() && isValidDescriptiveName() && isValidSuffix()
    }

    fun hasConflict(flywayMigrationFile: FlywayMigrationFile): Boolean {
        return flywayMigrationFile.isValidMigration() &&
                this.isValidMigration() &&
                flywayMigrationFile.getVersion().equals(this.getVersion()) &&
                flywayMigrationFile.getPrefix()!! == getPrefix()
    }

    companion object {
        @JvmStatic
        fun of(nature: MigrationNature, name: String): FlywayMigrationFile {
            return FlywayMigrationFile(nature, name)
        }
    }
}