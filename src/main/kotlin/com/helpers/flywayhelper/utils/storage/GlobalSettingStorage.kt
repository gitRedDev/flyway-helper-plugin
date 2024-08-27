package com.helpers.flywayhelper.utils.storage

import com.helpers.flywayhelper.Constants.ON_INSTALL_SETTING_KEY
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


class GlobalSettingStorage : PersistentStateComponent<GlobalSettingStorage>, SettingStorage<Any> {
    private val settings = emptyMap<String, Any>().toMutableMap()

    init {
        if (settings[ON_INSTALL_SETTING_KEY] == null) {
            this.putSetting(ON_INSTALL_SETTING_KEY, true)
        }
    }

    override fun getSetting(key: String): Any? {
        return settings[key]
    }

    fun getSettings(): Map<String, Any> {
        return settings
    }

    override fun putSetting(key: String, value: Any): Any? {
        val previousValue = settings.put(key, value)
        loadState(this)
        return previousValue
    }

    @Nullable
    override fun getState(): GlobalSettingStorage {
        return this
    }

    override fun loadState(@NotNull state: GlobalSettingStorage) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

