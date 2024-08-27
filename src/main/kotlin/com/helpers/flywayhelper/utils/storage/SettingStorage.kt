package com.helpers.flywayhelper.utils.storage

interface SettingStorage<T> {

    fun getSetting(key: String): T?
    fun putSetting(key: String, value: T): T?
}