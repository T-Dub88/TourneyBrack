package com.dubproductions.bracket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dubproductions.bracket.ui.MainAppContent
import com.dubproductions.bracket.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loadingViewModel: LoadingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !loadingViewModel.appReady.value
            }
        }

        val userLoggedIn = loadingViewModel.isLoggedIn.value

        setContent {
            MainAppContent(userLoggedIn)
        }
    }
}
