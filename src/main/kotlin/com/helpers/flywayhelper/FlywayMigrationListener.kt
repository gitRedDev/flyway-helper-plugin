package com.helpers.flywayhelper

import com.helpers.flywayhelper.utils.GitClient
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class FlywayMigrationListener() : BulkFileListener {
    override fun after(events: MutableList<out VFileEvent>) {
        events.forEach {
            if (it is VFileCreateEvent) {
                val mi = ProjectUtil.getActiveProject()?.let { p -> GitClient(p).getRemoteMigrations() }
                FlywayMigrationTreeWindow.refresh(it)
                return
            }
            FlywayMigrationTreeWindow.refresh()
        }

    }
}
