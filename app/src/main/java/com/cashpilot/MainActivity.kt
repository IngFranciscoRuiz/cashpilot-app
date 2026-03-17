package com.cashpilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.cashpilot.ui.navigation.CashPilotNavHost
import com.cashpilot.ui.theme.CashPilotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CashPilotTheme {
                SetSystemBars()
                CashPilotNavHost()
            }
        }
    }
}

@Composable
private fun SetSystemBars() {
    // Intentionally simplified; could be replaced with Accompanist System UI Controller
    SideEffect {
        // Keep default system bar styling for now for reliability across OEMs
    }
}

