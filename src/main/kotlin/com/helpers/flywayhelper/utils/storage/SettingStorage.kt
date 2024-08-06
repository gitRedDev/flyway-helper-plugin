package com.helpers.flywayhelper.utils.storage

import com.helpers.flywayhelper.Constants.ON_INSTALL_SETTING_KEY
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


class SettingStorage : PersistentStateComponent<SettingStorage> {
    private val settings = emptyMap<String, Any>().toMutableMap()

    init {
        if (settings[ON_INSTALL_SETTING_KEY] == null) {
            setSetting(ON_INSTALL_SETTING_KEY, true)
        }
    }

    fun getSettings(): Map<String, Any> {
        return settings
    }

    fun setSetting(settingKey: String, settingValue: Any): Any? {
        val previousValue = settings.put(settingKey, settingValue)
        loadState(this)
        return previousValue
    }

    fun removeSetting(settingKey: String): Any? {
        val previousValue = settings.remove(settingKey)
        loadState(this)
        return previousValue
    }

    @Nullable
    override fun getState(): SettingStorage {
        return this
    }

    override fun loadState(@NotNull state: SettingStorage) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

