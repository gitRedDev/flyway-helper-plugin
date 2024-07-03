package com.helpers.flywayhelper.helpers

import com.helpers.flywayhelper.Constants.ON_INSTALL_SETTING_KEY
import com.helpers.flywayhelper.Constants.SYNC_BRANCH_SETTING_KEY
import com.helpers.flywayhelper.utils.storage.SettingStorage

class SettingStorageHelper {

    companion object {

        private val settingStorage = SettingStorage()

        @JvmStatic
        fun isOnInstall(): Boolean {
            return getSetting(ON_INSTALL_SETTING_KEY) as Boolean
        }

        @JvmStatic
        fun markFalseOnInstall() {
            setSetting(ON_INSTALL_SETTING_KEY, false)
        }

        @JvmStatic
        fun getSyncBranch(): String? {
            return getSetting(SYNC_BRANCH_SETTING_KEY) as String?
        }

        @JvmStatic
        fun setSyncBranch(branch: String): String? {
            return setSetting(SYNC_BRANCH_SETTING_KEY, branch) as String?
        }

        @JvmStatic
        fun getSetting(settingKey: String): Any? {
            return settingStorage.getSettings()[settingKey]
        }

        @JvmStatic
        fun setSetting(settingKey: String, settingValue: Any): Any? {
            return settingStorage.setSetting(settingKey, settingValue)
        }
    }
}