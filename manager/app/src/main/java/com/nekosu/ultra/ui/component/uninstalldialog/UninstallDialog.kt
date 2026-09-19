package com.nekosu.ui.component.uninstalldialog

import androidx.compose.runtime.Composable
import com.nekosu.ui.LocalUiMode
import com.nekosu.ui.UiMode

@Composable
fun UninstallDialog(
    show: Boolean,
    onDismissRequest: () -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> UninstallDialogMiuix(show, onDismissRequest)
        UiMode.Material -> UninstallDialogMaterial(show, onDismissRequest)
    }
}
