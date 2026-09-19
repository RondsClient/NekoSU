package com.nekosu.ui.screen.susfs.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nekosu.ui.LocalUiMode
import com.nekosu.ui.UiMode
import com.nekosu.ui.screen.susfs.component.miuix.FeatureStatusCardMiuix
import com.nekosu.ui.screen.susfs.component.material.FeatureStatusCardMaterial
import com.nekosu.ui.screen.susfs.util.EnabledFeature

@Composable
fun FeatureStatusCard(
    feature: EnabledFeature,
    onRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> FeatureStatusCardMiuix(
            feature = feature,
            onRefresh = onRefresh,
            modifier = modifier
        )
        UiMode.Material -> FeatureStatusCardMaterial(
            feature = feature,
            onRefresh = onRefresh,
            modifier = modifier
        )
    }
}
