package com.nekosu.ultra.ui.customization

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class LayoutManager(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val layoutFile = File(context.filesDir, "custom_layout.json")
    private val presetsDir = File(context.filesDir, "layout_presets")

    init {
        if (!presetsDir.exists()) {
            presetsDir.mkdirs()
        }
    }

    suspend fun saveLayout(layout: CustomLayout) = withContext(Dispatchers.IO) {
        try {
            val jsonString = json.encodeToString(layout)
            layoutFile.writeText(jsonString)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadLayout(): CustomLayout = withContext(Dispatchers.IO) {
        try {
            if (layoutFile.exists()) {
                val jsonString = layoutFile.readText()
                json.decodeFromString<CustomLayout>(jsonString)
            } else {
                CustomLayout()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CustomLayout()
        }
    }

    suspend fun savePreset(name: String, layout: CustomLayout): Boolean = withContext(Dispatchers.IO) {
        try {
            val presetFile = File(presetsDir, "$name.json")
            val jsonString = json.encodeToString(layout.copy(id = name, name = name))
            presetFile.writeText(jsonString)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadPreset(name: String): CustomLayout? = withContext(Dispatchers.IO) {
        try {
            val presetFile = File(presetsDir, "$name.json")
            if (presetFile.exists()) {
                val jsonString = presetFile.readText()
                json.decodeFromString<CustomLayout>(jsonString)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllPresets(): List<CustomLayout> = withContext(Dispatchers.IO) {
        presetsDir.listFiles()?.mapNotNull { file ->
            try {
                json.decodeFromString<CustomLayout>(file.readText())
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()
    }

    suspend fun deletePreset(name: String): Boolean = withContext(Dispatchers.IO) {
        try {
            File(presetsDir, "$name.json").delete()
        } catch (e: Exception) {
            false
        }
    }

    fun moveModule(from: Int, to: Int, modules: List<ModuleConfig>): List<ModuleConfig> {
        val mutable = modules.toMutableList()
        if (from in mutable.indices && to in mutable.indices) {
            val item = mutable.removeAt(from)
            mutable.add(to, item)
            return mutable.mapIndexed { index, module ->
                module.copy(position = index)
            }
        }
        return modules
    }
}
