package com.nekosu.ui.customization

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import coil.compose.rememberAsyncImagePainter
import com.nekosu.R
import com.nekosu.ui.theme.LocalEnableBlur
import com.nekosu.ui.util.BlurredBar
import com.nekosu.ui.util.rememberBlurBackdrop
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import java.io.File

@Composable
fun CustomizationScreen(
    onBack: () -> Unit,
    onOpenThemeMarket: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { CustomizationViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val backdrop = rememberBlurBackdrop(enableBlur)
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else colorScheme.surface

    val wallpaperPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setWallpaper(it) }
    }

    var showAddModuleDialog by remember { mutableStateOf(false) }
    var showPresetDialog by remember { mutableStateOf(false) }
    var showAlphaSlider by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BlurredBar(backdrop) {
                TopAppBar(
                    color = barColor,
                    title = stringResource(R.string.customization_title),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenThemeMarket) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingCart,
                                contentDescription = "Theme Market"
                            )
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars
            .add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        Box(
            modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier
        ) {
            // Background wallpaper
            uiState.currentLayout.wallpaperPath?.let { path ->
                Image(
                    painter = rememberAsyncImagePainter(File(path)),
                    contentDescription = "Wallpaper",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(uiState.currentLayout.wallpaperAlpha),
                    contentScale = ContentScale.Crop
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = padding,
                overscrollEffect = null
            ) {
                // Wallpaper settings
                item {
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                    ) {
                        ArrowPreference(
                            title = stringResource(R.string.customization_wallpaper),
                            summary = if (uiState.currentLayout.wallpaperPath != null)
                                stringResource(R.string.customization_wallpaper_set)
                            else
                                stringResource(R.string.customization_wallpaper_none),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Image,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = null,
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = { wallpaperPicker.launch("image/*") }
                        )

                        if (uiState.currentLayout.wallpaperPath != null) {
                            ArrowPreference(
                                title = stringResource(R.string.customization_wallpaper_alpha),
                                summary = "${(uiState.currentLayout.wallpaperAlpha * 100).toInt()}%",
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Opacity,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = null,
                                        tint = colorScheme.onBackground
                                    )
                                },
                                onClick = { showAlphaSlider = true }
                            )

                            ArrowPreference(
                                title = stringResource(R.string.customization_wallpaper_remove),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = null,
                                        tint = colorScheme.error
                                    )
                                },
                                onClick = { viewModel.removeWallpaper() }
                            )
                        }
                    }
                }

                // Layout presets
                item {
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                    ) {
                        ArrowPreference(
                            title = stringResource(R.string.customization_save_preset),
                            summary = stringResource(R.string.customization_save_preset_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Save,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = null,
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = { showPresetDialog = true }
                        )

                        ArrowPreference(
                            title = stringResource(R.string.customization_reset_default),
                            summary = stringResource(R.string.customization_reset_default_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.RestartAlt,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = null,
                                    tint = colorScheme.error
                                )
                            },
                            onClick = { viewModel.resetToDefault() }
                        )
                    }
                }

                // Modules header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.customization_modules),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        IconButton(
                            onClick = { showAddModuleDialog = true }
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = "Add Module",
                                tint = colorScheme.primary
                            )
                        }
                    }
                }

                // Draggable module list
                itemsIndexed(
                    items = uiState.currentLayout.modules.sortedBy { it.position },
                    key = { _, module -> module.id }
                ) { index, module ->
                    var isDragging by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth()
                            .alpha(if (isDragging) 0.5f else 1f)
                            .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { isDragging = true },
                                    onDragEnd = { isDragging = false },
                                    onDragCancel = { isDragging = false }
                                ) { change, dragAmount ->
                                    change.consume()
                                    // Calculate new position based on drag
                                    val draggedY = dragAmount.y
                                    if (draggedY > 100 && index < uiState.currentLayout.modules.size - 1) {
                                        viewModel.moveModule(index, index + 1)
                                    } else if (draggedY < -100 && index > 0) {
                                        viewModel.moveModule(index, index - 1)
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Rounded.DragHandle,
                                    contentDescription = "Drag",
                                    modifier = Modifier.padding(end = 12.dp)
                                )

                                Column {
                                    Text(
                                        text = getModuleName(module.type),
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = "Position: ${module.position}",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = colorScheme.onSurfaceVariantSummary
                                        )
                                    )
                                }
                            }

                            Row {
                                Switch(
                                    checked = module.enabled,
                                    onCheckedChange = { viewModel.toggleModule(module.id) }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { viewModel.removeModule(module.id) }
                                ) {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        contentDescription = "Delete",
                                        tint = colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add Module Dialog
    if (showAddModuleDialog) {
        Dialog(
            title = stringResource(R.string.customization_add_module),
            onDismissRequest = { showAddModuleDialog = false }
        ) {
            LazyColumn {
                items(ModuleType.values().size) { index ->
                    val type = ModuleType.values()[index]
                    ArrowPreference(
                        title = getModuleName(type),
                        onClick = {
                            viewModel.addModule(type)
                            showAddModuleDialog = false
                        }
                    )
                }
            }
        }
    }

    // Save Preset Dialog
    if (showPresetDialog) {
        var presetName by remember { mutableStateOf("") }
        Dialog(
            title = stringResource(R.string.customization_save_preset),
            onDismissRequest = { showPresetDialog = false }
        ) {
            Column {
                TextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = stringResource(R.string.customization_preset_name),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    text = stringResource(R.string.confirm),
                    onClick = {
                        if (presetName.isNotBlank()) {
                            viewModel.savePreset(presetName)
                            showPresetDialog = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Alpha Slider Dialog
    if (showAlphaSlider) {
        var alpha by remember { mutableStateOf(uiState.currentLayout.wallpaperAlpha) }
        Dialog(
            title = stringResource(R.string.customization_wallpaper_alpha),
            onDismissRequest = { showAlphaSlider = false }
        ) {
            Column {
                Slider(
                    value = alpha,
                    onValueChange = { alpha = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${(alpha * 100).toInt()}%")
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    text = stringResource(R.string.confirm),
                    onClick = {
                        viewModel.setWallpaperAlpha(alpha)
                        showAlphaSlider = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun getModuleName(type: ModuleType): String {
    return when (type) {
        ModuleType.STATUS_CARD -> stringResource(R.string.module_status_card)
        ModuleType.UPDATE_CARD -> stringResource(R.string.module_update_card)
        ModuleType.INFO_CARD -> stringResource(R.string.module_info_card)
        ModuleType.QUICK_ACTIONS -> stringResource(R.string.module_quick_actions)
        ModuleType.DEVICE_INFO -> stringResource(R.string.module_device_info)
        ModuleType.KERNEL_INFO -> stringResource(R.string.module_kernel_info)
        ModuleType.CUSTOM_TEXT -> stringResource(R.string.module_custom_text)
        ModuleType.CUSTOM_IMAGE -> stringResource(R.string.module_custom_image)
        ModuleType.SEPARATOR -> stringResource(R.string.module_separator)
    }
}
