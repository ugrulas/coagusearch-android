package com.example.coagusearch.utils


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.CryptoConfig
import com.google.gson.Gson

object PreferenceHelper {
    fun defaultPrefs(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun customPrefs(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
}

inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
operator fun SharedPreferences.set(key: String, value: Any?) {
    val gson = Gson()
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}


/**
 * puts a encrypted key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
fun SharedPreferences.securelyPutString(context: Context, key: String, value: String) {
    val keyChain = SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)
    val crypto = AndroidConceal.get().createDefaultCrypto(keyChain)

    if (!crypto.isAvailable) {
        edit { it.putString(key, value) }
    } else {
        val encryptedValue = crypto.encrypt(key, value)
        edit { it.putString(key, encryptedValue) }
    }
}

/**
 * finds value on given key and decrypts it.
 */
fun SharedPreferences.securelyGetString(context: Context, key: String): String? {
    val keyChain = SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)
    val crypto = AndroidConceal.get().createDefaultCrypto(keyChain)

    return if (!crypto.isAvailable) {
        getString(key, null)
    } else {
        val encryptedValue = getString(key, null) ?: return null
        crypto.decrypt(key, encryptedValue)
    }
}

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * Clears all of the values
 */
fun SharedPreferences.clear() {
    edit { it.clear() }
}