package com.nekosu.ui.kernelFlash.component

import androidx.compose.runtime.Composable
import com.nekosu.ui.LocalUiMode
import com.nekosu.ui.UiMode

@Composable
fun SlotSelectionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSlotSelected: (String) -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SlotSelectionDialogMiuix(
            show = show,
            onDismiss = onDismiss,
            onSlotSelected = onSlotSelected
        )
        UiMode.Material -> SlotSelectionDialogMaterial(
            show = show,
            onDismiss = onDismiss,
            onSlotSelected = onSlotSelected
        )
    }
}
