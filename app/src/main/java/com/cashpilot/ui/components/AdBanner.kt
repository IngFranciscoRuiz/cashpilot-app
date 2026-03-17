package com.cashpilot.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * ID de unidad de banner. Reemplaza en producción por tu Banner unit ID de AdMob.
 * Este es el ID de prueba de Google para que los anuncios carguen sin error en desarrollo.
 */
private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { ctx -> createAdView(ctx) }
    )
}

private fun createAdView(context: Context): AdView {
    return AdView(context).apply {
        setAdSize(AdSize.BANNER)
        adUnitId = BANNER_AD_UNIT_ID
        loadAd(AdRequest.Builder().build())
    }
}
