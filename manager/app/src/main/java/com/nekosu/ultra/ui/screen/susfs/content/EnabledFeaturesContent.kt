package com.nekosu.ui.screen.susfs.content

import androidx.compose.runtime.Composable
import com.nekosu.ui.LocalUiMode
import com.nekosu.ui.UiMode
import com.nekosu.ui.screen.susfs.content.miuix.EnabledFeaturesContentMiuix
import com.nekosu.ui.screen.susfs.content.material.EnabledFeaturesContentMaterial
import com.nekosu.ui.screen.susfs.util.EnabledFeature

@Composable
fun EnabledFeaturesContent(
    enabledFeatures: List<EnabledFeature>,
    onRefresh: () -> Unit
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> EnabledFeaturesContentMiuix(
            enabledFeatures = enabledFeatures,
            onRefresh = onRefresh
        )
        UiMode.Material -> EnabledFeaturesContentMaterial(
            enabledFeatures = enabledFeatures,
            onRefresh = onRefresh
        )
    }
}
