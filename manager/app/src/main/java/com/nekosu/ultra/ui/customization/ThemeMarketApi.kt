package com.nekosu.ui.customization

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

class ThemeMarketApi {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // GitHub仓库API地址
    private val baseUrl = "https://api.github.com/repos/RondsClient/NekoSU/contents/Vive"
    private val rawUrl = "https://raw.githubusercontent.com/RondsClient/NekoSU/main/Vive"

    suspend fun fetchThemes(): List<Theme> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(baseUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val files = json.decodeFromString<List<GitHubFile>>(response)

                // 过滤出.json文件
                val themeFiles = files.filter { it.name.endsWith(".json") }

                // 下载并解析每个主题文件
                themeFiles.mapNotNull { file ->
                    try {
                        downloadTheme(file.download_url)
                    } catch (e: Exception) {
                        null
                    }
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun downloadTheme(url: String): Theme? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                json.decodeFromString<Theme>(response)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadThemeFile(theme: Theme): CustomLayout? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(theme.downloadUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                json.decodeFromString<CustomLayout>(response)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @kotlinx.serialization.Serializable
    private data class GitHubFile(
        val name: String,
        val path: String,
        val download_url: String,
        val type: String
    )
}
