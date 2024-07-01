package com.helpers.flywayhelper.enums

enum class MigrationTypeEnum(private val alias: Char) {

    VERSIONED('V'),
    UNDO('U'),
    REPEATABLE('R');

    fun getAlias(): Char {
        return alias
    }

    companion object {
        @JvmStatic
        fun byAlias(alias: Char): MigrationTypeEnum? {
            return MigrationTypeEnum.values().find { it.getAlias() == alias }
        }
    }
}