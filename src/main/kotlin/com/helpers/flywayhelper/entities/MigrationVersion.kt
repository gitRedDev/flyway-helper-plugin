package com.helpers.flywayhelper.entities

class MigrationVersion(private val version: String) {

    private val SEPARATORS = listOf("_", ".")

    fun getVersionString(): String? {
        return if (isValidVersion()) version.replace(otherVersionSeparator()!!, getVersionSeparator()!!) else null
    }

    private fun getMinimalVersionString(): String? {
        if (!isValidVersion()) {
            return null
        }
        return getMinimalVersionParts()!!.joinToString(getVersionSeparator()!!)
    }

    fun getVersionNumberRepresentation(): String? {
        return if (isValidVersion()) getVersionString()!!.replace(getVersionSeparator()!!, "") else null
    }

    private fun getVersionSeparator(): String? {
        if (!isValidVersion()) {
            return null
        }

        val underscoreNb = version.split("").filter { it == "_" }.size
        val dotNb = version.split("").filter { it == "." }.size
        return if (dotNb >= underscoreNb) "." else "_"
    }

    private fun otherVersionSeparator(): String? {
        if (getVersionSeparator() == null) {
            return null
        }

        return SEPARATORS.findLast { it != getVersionSeparator() }
    }

    private fun getVersionParts(): List<Int>? {
        return if (isValidVersion()) getVersionString()!!.split(getVersionSeparator()!!).map { it.toInt() } else null
    }

    private fun getDetailedVersionParts(): List<Pair<Int, Int>>? {
        return if (isValidVersion()) getVersionString()!!.split(getVersionSeparator()!!).map { Pair(it.length, it.toInt()) } else null
    }

    fun getMinimalVersionParts(): List<Int>? {
        if (!isValidVersion()) {
            return null
        }
        val versionParts = getVersionParts()!!
        val offset = versionParts.reversed().indexOfFirst { it != 0 }
        return versionParts.reversed().subList(offset, versionParts.size).reversed()
    }

    fun nextMigrationVersion(): MigrationVersion? {
        if (!isValidVersion()) {
            return null
        }
        val versionNumbers = (getVersionString()!!.replace(getVersionSeparator()!!, "").toInt() + 1).toString().split("")
                .filter { it.isNotBlank() }.toMutableList()
        var dotIndexes = getDetailedVersionParts()!!.map { p -> p.first }.runningReduce { acc, i -> acc + i  }
        dotIndexes = dotIndexes.subList(0, dotIndexes.size - 1)
        dotIndexes.forEach { versionNumbers[it] = getVersionSeparator()!! + versionNumbers[it] }
        return MigrationVersion(versionNumbers.joinToString(""))
    }

    fun isValidVersion(): Boolean {
        return try {
            val parts = version.replace("_", ".").split(".")
            parts.map { it.toInt() }
            return parts.isNotEmpty() && parts.all { it.isNotBlank() }
        } catch (_: Exception) {
            false
        }
    }

    override fun toString(): String {
        return version
    }
    override fun equals(other: Any?): Boolean {
        if (other != null && other is MigrationVersion) {
            return getMinimalVersionParts()?.size == other.getMinimalVersionParts()?.size
                    && getMinimalVersionParts()?.mapIndexed { index, p -> p == other.getMinimalVersionParts()?.get(index)}?.all { b -> b } ?: false
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return getVersionParts()?.joinToString("").hashCode()
    }
}
