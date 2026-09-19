package com.nekosu.ui.screen.susfs.component

import androidx.compose.runtime.Composable
import com.nekosu.ui.LocalUiMode
import com.nekosu.ui.UiMode
import com.nekosu.ui.screen.susfs.component.miuix.SlotInfoDialogMiuix
import com.nekosu.ui.screen.susfs.component.material.SlotInfoDialogMaterial
import com.nekosu.ui.screen.susfs.util.SlotInfo

@Composable
fun SlotInfoDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    slotInfoList: List<SlotInfo>,
    currentActiveSlot: String,
    isLoadingSlotInfo: Boolean,
    onRefresh: () -> Unit,
    onUseUname: (String) -> Unit,
    onUseBuildTime: (String) -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SlotInfoDialogMiuix(
            showDialog = showDialog,
            onDismiss = onDismiss,
            slotInfoList = slotInfoList,
            currentActiveSlot = currentActiveSlot,
            isLoadingSlotInfo = isLoadingSlotInfo,
            onRefresh = onRefresh,
            onUseUname = onUseUname,
            onUseBuildTime = onUseBuildTime
        )
        UiMode.Material -> SlotInfoDialogMaterial(
            showDialog = showDialog,
            onDismiss = onDismiss,
            slotInfoList = slotInfoList,
            currentActiveSlot = currentActiveSlot,
            isLoadingSlotInfo = isLoadingSlotInfo,
            onRefresh = onRefresh,
            onUseUname = onUseUname,
            onUseBuildTime = onUseBuildTime
        )
    }
}
