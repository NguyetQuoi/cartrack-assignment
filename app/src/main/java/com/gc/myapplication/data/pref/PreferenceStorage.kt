package com.gc.myapplication.data.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 * @author n.quoi
 * @date 10.18.2021
 */

interface PreferenceStorage {
    var appIsForeground: Boolean
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
class SharedPreferenceStorage(context: Context, gson: Gson) : PreferenceStorage {

    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val observableAppIsForegroundResult = MutableLiveData<Boolean>()

    override var appIsForeground by BooleanPreference(prefs, PREF_APP_IS_FOREGROUND, false)
    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREF_APP_IS_FOREGROUND -> observableAppIsForegroundResult.value = appIsForeground
        }
    }

    companion object {
        const val PREFS_NAME = "crowdpop"
        const val PREF_APP_IS_FOREGROUND = "pref_app_is_foreground"
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
    }
}

/**
 * BooleanPreference
 * Extend [ReadWriteProperty]
 */
class BooleanPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit { putBoolean(name, value) }
    }
}

/**
 * Preference for String
 */
class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit { putString(name, value) }
    }
}

/**
 * Preference for Int
 */
class IntPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any, Int> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.edit { putInt(name, value) }
    }
}

/**
 * Preference for Map<Object, Object>
 */
class MapPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val gson: Gson
) : ReadWriteProperty<Any, Map<String, String>?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Map<String, String>? {
        val mapString = preferences.getString(name, null)
        return mapString?.let {
            val type = object : TypeToken<HashMap<String, String>>() {}.type
            gson.fromJson(mapString, type)
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Map<String, String>?) {
        val mapString = gson.toJson(value)
        preferences.edit { putString(name, mapString) }
    }
}

/**
 * Preference for Object
 */
class ObjectPreference<T>(
    private val preferences: SharedPreferences,
    private val name: String,
    private val gson: Gson,
    private val clazz: Class<T>
) : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        val objectString = preferences.getString(name, null)
        return objectString?.let {
            gson.fromJson(objectString, clazz)
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        preferences.edit { putString(name, gson.toJson(value)) }
    }
}
