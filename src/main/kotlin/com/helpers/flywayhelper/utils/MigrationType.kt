package com.helpers.flywayhelper.utils

enum class MigrationType(private val alias: Char) {
    VERSIONED('V'),
    UNDO('U'),
    REPEATABLE('R');

    fun getAlias(): Char {
        return alias
    }

    companion object {
        @JvmStatic
        fun byAlias(alias: Char): MigrationType? {
            return MigrationType.values().find { it.getAlias() == alias }
        }
    }
}