package com.helpers.flywayhelper

import com.helpers.flywayhelper.utils.notifications.Notifier
import com.intellij.ide.impl.ProjectUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.treeStructure.Tree
import com.intellij.vcsUtil.VcsUtil


class FlywayMigrationTreeWindow: ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val migrations = getFlywayMigrations(project)
        val conflictMigrations: List<FlywayMigration> = migrations.values.flatten().groupBy { it.getVersion() }.filter { it.value.size > 1 }.values.flatten()

        val root = FlywayMigrationTree("Migrations", migrations)
        val tree = Tree(root)
        tree.cellRenderer = FlywayMigrationTreeRenderer(conflictMigrations)
        val contentFactory = ContentFactory.getInstance()
        toolWindow.contentManager.addContent(contentFactory.createContent(JBScrollPane(tree), "", false))

    }

    private fun getFlywayMigrations(project: Project): Map<String, List<FlywayMigration>> {
        val baseDir: VirtualFile? = project.guessProjectDir()
        val ddlDir: VirtualFile? = baseDir?.findFileByRelativePath("src/main/resources/db/migration/ddl")
        val dmlDir: VirtualFile? = baseDir?.findFileByRelativePath("src/main/resources/db/migration/dml")

        val ddlMigrations = ddlDir?.children?.map { FlywayMigration(getAuthorOfFile(project, it), it.name) }?.sortedBy { it.getVersion() }.orEmpty()
        val dmlMigrations = dmlDir?.children?.map { FlywayMigration(getAuthorOfFile(project, it), it.name) }?.sortedBy { it.getVersion() }.orEmpty()

        return mapOf(
            "ddl" to ddlMigrations,
            "dml" to dmlMigrations
        )
    }

    private fun getAuthorOfFile(project: Project, file: VirtualFile): String {
        val process = Runtime.getRuntime().exec("git log -n 1 --format='%cn' " + file.path, null, project.guessProjectDir()?.let { VcsUtil.getFilePath(it).ioFile })
        process.waitFor()
        val authorName: String? = process.inputStream.bufferedReader().use { it.readLine()?.replace("'","") }
        if (authorName != null) return authorName

        val process2 = Runtime.getRuntime().exec("git config user.name ")
        process2.waitFor()
        val currentEditorName: String? = process2.inputStream.bufferedReader().use { it.readLine()?.replace("'","") }
        return currentEditorName?: "Unknown"
    }

    companion object {
        @JvmStatic
        fun refresh() {
            print("begin refresh")
            val project: Project? = ProjectUtil.getActiveProject()
            val toolWindow: ToolWindow? = project?.let { ToolWindowManager.getInstance(it).getToolWindow("Flyway Migration Helper") }
            toolWindow?.contentManager?.removeAllContents(true)
            if (project != null && toolWindow != null) {
                FlywayMigrationTreeWindow().createToolWindowContent(project, toolWindow)
            }
            print("end refresh")
        }
        @JvmStatic
        fun refresh(createEvent: VFileCreateEvent) {
            refresh()
            val createdFlywayMigration = createEvent.file?.name?.let { FlywayMigration("Unknown", it) } ?: return
            val hasConflict: Boolean? = ProjectUtil.getActiveProject()?.let { p -> FlywayMigrationTreeWindow().getFlywayMigrations(p).values.flatten().any { it.hasConflict(createdFlywayMigration) } }
            if (hasConflict == true) {
                Notifier(ProjectUtil.getActiveProject()).notify("Flyway migration conflict: <b>${createdFlywayMigration.getVersion()}</b> version already exists", NotificationType.ERROR)
            }
            print("end notify")

        }
    }

}




