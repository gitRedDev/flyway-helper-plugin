package com.helpers.flywayhelper

import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

class FlywayMigrationTreeRenderer(private val conflictMigrations: List<FlywayMigration>) : DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(tree: JTree?, value: Any?, sel: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean): Component? {
        val component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        if (value is DefaultMutableTreeNode) {
            val userObject = value.userObject
            if (userObject is FlywayMigration) {
                val panel = JPanel(FlowLayout(FlowLayout.LEFT))
                val nameLabel = JLabel(userObject.getName())
                if (conflictMigrations.contains(userObject)) {
                    nameLabel.apply { foreground = JBColor.RED }
                }
                val authorLabel = JLabel(userObject.getAuthor()).apply { foreground = JBColor.GRAY }
                panel.add(nameLabel)
                panel.add(authorLabel)
                return panel
            }
        }
        this.icon = getIconForMigration(true)
        return component
    }

    private fun getIconForMigration(folder: Boolean): Icon {
        return if (folder) {
            AllIcons.Nodes.Folder
        }
        else {
            AllIcons.FileTypes.Any_type
        }
    }
}