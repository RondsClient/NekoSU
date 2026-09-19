package com.nekosu.ui.customization

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class WallpaperManager(private val context: Context) {

    private val wallpaperDir = File(context.filesDir, "wallpapers")

    init {
        if (!wallpaperDir.exists()) {
            wallpaperDir.mkdirs()
        }
    }

    suspend fun saveWallpaper(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val filename = "wallpaper_${System.currentTimeMillis()}.jpg"
            val file = File(wallpaperDir, filename)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            bitmap.recycle()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadWallpaper(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val connection = java.net.URL(url).openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val filename = "wallpaper_${url.hashCode()}.jpg"
            val file = File(wallpaperDir, filename)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            bitmap.recycle()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteWallpaper(path: String): Boolean {
        return try {
            File(path).delete()
        } catch (e: Exception) {
            false
        }
    }

    fun getAllWallpapers(): List<String> {
        return wallpaperDir.listFiles()?.map { it.absolutePath } ?: emptyList()
    }
}
