package com.personal.appstore.data

import android.content.Context
import androidx.core.content.edit

class PreferencesRepository(context: Context) {

    private val prefs = context.getSharedPreferences("appstore_prefs", Context.MODE_PRIVATE)

    var catalogUrl: String
        get() = prefs.getString(KEY_CATALOG_URL, DEFAULT_CATALOG_URL) ?: DEFAULT_CATALOG_URL
        set(value) = prefs.edit { putString(KEY_CATALOG_URL, value) }

    companion object {
        private const val KEY_CATALOG_URL = "catalog_url"
        // Replace with your GitHub Pages URL once the repo is created
        const val DEFAULT_CATALOG_URL = "https://YOUR_USERNAME.github.io/personal-app-store/catalog.json"
    }
}
