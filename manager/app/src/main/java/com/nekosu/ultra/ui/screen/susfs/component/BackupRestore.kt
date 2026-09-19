package com.nekosu.ui.screen.susfs.component

import androidx.compose.runtime.Composable
import com.nekosu.ui.LocalUiMode
import com.nekosu.ui.UiMode
import com.nekosu.ui.screen.susfs.component.miuix.BackupRestoreComponentMiuix
import com.nekosu.ui.screen.susfs.component.material.BackupRestoreComponentMaterial

@Composable
fun BackupRestoreComponent(
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit,
    onConfigReload: () -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> BackupRestoreComponentMiuix(
            isLoading = isLoading,
            onLoadingChange = onLoadingChange,
            onConfigReload = onConfigReload
        )
        UiMode.Material -> BackupRestoreComponentMaterial(
            isLoading = isLoading,
            onLoadingChange = onLoadingChange,
            onConfigReload = onConfigReload
        )
    }
}
