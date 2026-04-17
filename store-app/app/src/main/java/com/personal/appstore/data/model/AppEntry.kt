package com.personal.appstore.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppCatalog(
    @SerialName("store_version") val storeVersion: Int = 1,
    val updated: String = "",
    val apps: List<AppEntry> = emptyList()
)

@Parcelize
@Serializable
data class AppEntry(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("version_name") val versionName: String,
    @SerialName("version_code") val versionCode: Int,
    @SerialName("min_sdk") val minSdk: Int = 26,
    @SerialName("apk_url") val apkUrl: String,
    @SerialName("icon_url") val iconUrl: String? = null,
    val category: String = "General",
    val changelog: String? = null,
    @SerialName("size_bytes") val sizeBytes: Long? = null,
    val screenshots: List<String> = emptyList()
) : Parcelable
