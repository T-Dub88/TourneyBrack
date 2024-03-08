package com.dubproductions.bracket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

        setContent {
            val userLoggedIn by loadingViewModel.isLoggedIn.collectAsStateWithLifecycle()
            MainAppContent(
                userLoggedIn,
                setLoggedInStatus = {
                    loadingViewModel.updatedLogInStatus(it)
                }
            )
        }
    }
}
