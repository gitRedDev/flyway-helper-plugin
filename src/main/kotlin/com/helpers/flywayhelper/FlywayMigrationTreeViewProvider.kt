package com.helpers.flywayhelper

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.fileTypes.PlainTextFileType

// {ddl: [{},{}]}
internal class FlywayMigrationTreeViewProvider : TreeStructureProvider {
    override fun modify(parent: AbstractTreeNode<*>,
                        children: Collection<AbstractTreeNode<*>>,
                        settings: ViewSettings): Collection<AbstractTreeNode<*>> {
        val nodes = ArrayList<AbstractTreeNode<*>>()
        for (child in children) {
            if (child is PsiFileNode) {
                val file = child.virtualFile
                if (file != null && !file.isDirectory && file.fileType !is PlainTextFileType) {
                    continue
                }
            }
            nodes.add(child)
        }
        return nodes
    }
}