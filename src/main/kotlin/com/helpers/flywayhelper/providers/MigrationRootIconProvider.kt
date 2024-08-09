package com.helpers.flywayhelper.providers

import com.helpers.flywayhelper.helpers.SettingStorageHelper
import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import icons.CustomIcons
import org.apache.commons.lang.StringUtils
import javax.swing.Icon


class MigrationRootIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val migrationRootPath = SettingStorageHelper.getMigrationRootFolderPath()

        if (StringUtils.isNotBlank(migrationRootPath) && PsiUtilCore.getVirtualFile(element)?.path == migrationRootPath) {
            return CustomIcons.Dark.MigrationFolderRootIcon
        }
        return null
    }
}
