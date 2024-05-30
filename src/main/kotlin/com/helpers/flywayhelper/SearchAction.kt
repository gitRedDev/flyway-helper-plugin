package com.helpers.flywayhelper

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import java.util.*

class SearchAction : AnAction() {
    /**
     * Convert selected text to a URL friendly string.
     * @param e
     */
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = editor.caretModel

        // For searches from the editor, we should also get file type information
        // to help add scope to the search using the Stack overflow search syntax.
        //
        // https://stackoverflow.com/help/searching
        var languageTag = ""
        val file = e.getData(CommonDataKeys.PSI_FILE)
        if (file != null) {
            val lang = e.getData(CommonDataKeys.PSI_FILE)!!.language
            languageTag = "+[" + lang.displayName.lowercase(Locale.getDefault()) + "]"
        }

        // The update method below is only called periodically so need
        // to be careful to check for selected text
        if (caretModel.currentCaret.hasSelection()) {
            val query = caretModel.currentCaret.selectedText!!.replace(' ', '+') + languageTag
            BrowserUtil.browse("https://stackoverflow.com/search?q=$query")
        }
    }

    /**
     * Only make this action visible when text is selected.
     * @param e
     */
    override fun update(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = editor.caretModel
        e.presentation.isEnabledAndVisible = true
    }

    //nc => not covered
}