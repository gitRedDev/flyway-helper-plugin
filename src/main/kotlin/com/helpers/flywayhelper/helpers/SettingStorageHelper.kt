package com.helpers.flywayhelper.helpers

import com.helpers.flywayhelper.Constants.MIGRATION_ROOT_FOLDER_PATH_SETTING_KEY
import com.helpers.flywayhelper.Constants.ON_INSTALL_SETTING_KEY
import com.helpers.flywayhelper.Constants.SYNC_BRANCH_SETTING_KEY
import com.helpers.flywayhelper.enums.ScopeEnum
import com.helpers.flywayhelper.utils.storage.GlobalSettingStorage
import com.helpers.flywayhelper.utils.storage.ProjectSettingStorage
import com.intellij.openapi.project.Project

class SettingStorageHelper(project: Project) {

    private val globalSettingStorage = GlobalSettingStorage()
    private val projectSettingStorage = ProjectSettingStorage(project)


    fun isOnInstall(): Boolean {
        return getSetting(ON_INSTALL_SETTING_KEY, ScopeEnum.GLOBAL) as Boolean
    }

    fun markFalseOnInstall() {
        putSetting(ON_INSTALL_SETTING_KEY, false, ScopeEnum.GLOBAL)
    }

    fun getSyncBranch(): String? {
        return getSetting(SYNC_BRANCH_SETTING_KEY, ScopeEnum.PROJECT) as String?
    }

    fun setSyncBranch(branch: String): String? {
        return putSetting(SYNC_BRANCH_SETTING_KEY, branch, ScopeEnum.PROJECT) as String?
    }

    fun getMigrationRootFolderPath(): String? {
        return getSetting(MIGRATION_ROOT_FOLDER_PATH_SETTING_KEY, ScopeEnum.PROJECT) as String?
    }

    fun setMigrationRootFolderPath(path: String): String? {
        return putSetting(MIGRATION_ROOT_FOLDER_PATH_SETTING_KEY, path, ScopeEnum.PROJECT) as String?
    }

    fun removeMigrationRootFolderPath(): String? {
        return putSetting(MIGRATION_ROOT_FOLDER_PATH_SETTING_KEY, "", ScopeEnum.PROJECT) as String?
    }

    private fun getSetting(settingKey: String, scope: ScopeEnum): Any? {
        return when (scope) {
            ScopeEnum.GLOBAL -> globalSettingStorage.getSetting(settingKey)
            ScopeEnum.PROJECT -> projectSettingStorage.getSetting(settingKey)
        }
    }

    private fun putSetting(settingKey: String, settingValue: Any, scope: ScopeEnum): Any? {
        return when (scope) {
            ScopeEnum.GLOBAL -> globalSettingStorage.putSetting(settingKey, settingValue)
            ScopeEnum.PROJECT -> projectSettingStorage.putSetting(settingKey, settingValue as String)
        }
    }
}