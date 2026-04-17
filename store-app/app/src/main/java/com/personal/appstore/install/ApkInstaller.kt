package com.personal.appstore.install

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.FileProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ApkInstaller(private val context: Context) {

    private val client = HttpClient(Android)

    suspend fun downloadAndInstall(
        apkUrl: String,
        appId: String,
        onProgress: (Float) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val outFile = File(context.cacheDir, "$appId.apk")

            val response = client.get(apkUrl)
            val channel = response.bodyAsChannel()
            val contentLength = response.headers["Content-Length"]?.toLongOrNull() ?: -1L
            var bytesRead = 0L

            outFile.outputStream().use { out ->
                val buffer = ByteArray(8192)
                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer)
                    if (read <= 0) break
                    out.write(buffer, 0, read)
                    bytesRead += read
                    if (contentLength > 0) onProgress(bytesRead.toFloat() / contentLength)
                }
            }

            onProgress(1f)
            installApk(outFile)
        }
    }

    private fun installApk(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun getInstalledVersionCode(packageId: String): Int? = try {
        val info = context.packageManager.getPackageInfo(packageId, 0)
        @Suppress("DEPRECATION")
        info.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}
