package com.helpers.flywayhelper

import javax.swing.tree.DefaultMutableTreeNode


class FlywayMigrationTree(name: String?, migrations: Map<String, List<FlywayMigration>>) : DefaultMutableTreeNode(name, true) {
    init {
        for ((key, value) in migrations) {
            val migrationType = DefaultMutableTreeNode(key)
            for (migration in value) {
                migrationType.add(DefaultMutableTreeNode(migration))
            }
            add(migrationType)
        }
    }
}

class FlywayMigration(private val author: String, private val name: String){
    override fun toString(): String {
        return "$name ($author)"
    }

    fun getAuthor(): String {
        return author
    }

    fun getName(): String {
        return name
    }

    fun getVersion(): String {
        return name.split("__")[0]
    }

    fun hasConflict(migration: FlywayMigration): Boolean {
        return migration.getVersion() == getVersion()
    }

}

