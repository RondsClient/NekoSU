package com.nekosu.ultra.ui.customization

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomizationUiState(
    val currentLayout: CustomLayout = CustomLayout(),
    val presets: List<CustomLayout> = emptyList(),
    val wallpapers: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ThemeMarketUiState(
    val themes: List<Theme> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTheme: Theme? = null
)

class CustomizationViewModel(private val context: Context) : ViewModel() {

    private val wallpaperManager = WallpaperManager(context)
    private val layoutManager = LayoutManager(context)

    private val _uiState = MutableStateFlow(CustomizationUiState())
    val uiState: StateFlow<CustomizationUiState> = _uiState.asStateFlow()

    private val _marketState = MutableStateFlow(ThemeMarketUiState())
    val marketState: StateFlow<ThemeMarketUiState> = _marketState.asStateFlow()

    private val themeMarketApi = ThemeMarketApi()

    init {
        loadLayout()
        loadWallpapers()
        loadPresets()
    }

    fun loadLayout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val layout = layoutManager.loadLayout()
                _uiState.update { it.copy(currentLayout = layout, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun saveLayout(layout: CustomLayout) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val success = layoutManager.saveLayout(layout)
                if (success) {
                    _uiState.update { it.copy(currentLayout = layout, isLoading = false, error = null) }
                } else {
                    _uiState.update { it.copy(error = "Failed to save layout", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun setWallpaper(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val path = wallpaperManager.saveWallpaper(uri)
                if (path != null) {
                    val newLayout = _uiState.value.currentLayout.copy(wallpaperPath = path)
                    saveLayout(newLayout)
                    loadWallpapers()
                } else {
                    _uiState.update { it.copy(error = "Failed to save wallpaper", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun setWallpaperAlpha(alpha: Float) {
        val newLayout = _uiState.value.currentLayout.copy(wallpaperAlpha = alpha)
        saveLayout(newLayout)
    }

    fun removeWallpaper() {
        val currentPath = _uiState.value.currentLayout.wallpaperPath
        if (currentPath != null) {
            wallpaperManager.deleteWallpaper(currentPath)
        }
        val newLayout = _uiState.value.currentLayout.copy(wallpaperPath = null)
        saveLayout(newLayout)
        loadWallpapers()
    }

    private fun loadWallpapers() {
        viewModelScope.launch {
            try {
                val wallpapers = wallpaperManager.getAllWallpapers()
                _uiState.update { it.copy(wallpapers = wallpapers) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun moveModule(from: Int, to: Int) {
        val currentModules = _uiState.value.currentLayout.modules
        val newModules = layoutManager.moveModule(from, to, currentModules)
        val newLayout = _uiState.value.currentLayout.copy(modules = newModules)
        saveLayout(newLayout)
    }

    fun toggleModule(moduleId: String) {
        val currentModules = _uiState.value.currentLayout.modules
        val newModules = currentModules.map { module ->
            if (module.id == moduleId) {
                module.copy(enabled = !module.enabled)
            } else {
                module
            }
        }
        val newLayout = _uiState.value.currentLayout.copy(modules = newModules)
        saveLayout(newLayout)
    }

    fun addModule(type: ModuleType) {
        val currentModules = _uiState.value.currentLayout.modules
        val newModule = ModuleConfig(
            id = "module_${System.currentTimeMillis()}",
            type = type,
            position = currentModules.size,
            enabled = true
        )
        val newModules = currentModules + newModule
        val newLayout = _uiState.value.currentLayout.copy(modules = newModules)
        saveLayout(newLayout)
    }

    fun removeModule(moduleId: String) {
        val currentModules = _uiState.value.currentLayout.modules
        val newModules = currentModules.filter { it.id != moduleId }
            .mapIndexed { index, module -> module.copy(position = index) }
        val newLayout = _uiState.value.currentLayout.copy(modules = newModules)
        saveLayout(newLayout)
    }

    fun savePreset(name: String) {
        viewModelScope.launch {
            try {
                val success = layoutManager.savePreset(name, _uiState.value.currentLayout)
                if (success) {
                    loadPresets()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadPreset(name: String) {
        viewModelScope.launch {
            try {
                val preset = layoutManager.loadPreset(name)
                if (preset != null) {
                    saveLayout(preset)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun loadPresets() {
        viewModelScope.launch {
            try {
                val presets = layoutManager.getAllPresets()
                _uiState.update { it.copy(presets = presets) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deletePreset(name: String) {
        viewModelScope.launch {
            try {
                layoutManager.deletePreset(name)
                loadPresets()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // Theme Market functions
    fun loadThemes() {
        viewModelScope.launch {
            _marketState.update { it.copy(isLoading = true) }
            try {
                val themes = themeMarketApi.fetchThemes()
                _marketState.update { it.copy(themes = themes, isLoading = false, error = null) }
            } catch (e: Exception) {
                _marketState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun selectTheme(theme: Theme) {
        _marketState.update { it.copy(selectedTheme = theme) }
    }

    fun applyTheme(theme: Theme) {
        viewModelScope.launch {
            _marketState.update { it.copy(isLoading = true) }
            try {
                val layout = themeMarketApi.downloadThemeFile(theme)
                if (layout != null) {
                    // Download wallpaper if exists
                    if (theme.wallpaperUrl != null) {
                        val wallpaperPath = wallpaperManager.downloadWallpaper(theme.wallpaperUrl)
                        val newLayout = layout.copy(wallpaperPath = wallpaperPath)
                        saveLayout(newLayout)
                    } else {
                        saveLayout(layout)
                    }
                    _marketState.update { it.copy(isLoading = false, error = null) }
                } else {
                    _marketState.update { it.copy(error = "Failed to download theme", isLoading = false) }
                }
            } catch (e: Exception) {
                _marketState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun resetToDefault() {
        saveLayout(CustomLayout())
    }
}
