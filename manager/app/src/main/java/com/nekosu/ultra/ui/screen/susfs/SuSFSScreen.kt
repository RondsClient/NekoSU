package com.nekosu.ui.screen.susfs

import androidx.compose.runtime.Composable
import com.nekosu.ui.LocalUiMode
import com.nekosu.ui.UiMode

@Composable
fun SuSFSScreen() {
    when (LocalUiMode.current) {
        UiMode.Miuix -> SuSFSMiuix()
        UiMode.Material -> SuSFSMaterial()
    }
}
