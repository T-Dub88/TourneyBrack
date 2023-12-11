package com.dubproductions.bracket.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.navigation.SetupNavGraph
import com.dubproductions.bracket.ui.theme.BracketTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    navController: NavHostController
) {
    val navBarItems: List<Screen> = listOf(Screen.Home, Screen.Hosting, Screen.Participating, Screen.Settings)
    val currentDestination: String? = navController.currentBackStackEntryAsState().value?.destination?.route

    BracketTheme {
        Scaffold(
            bottomBar = {
                val selectedIndex: Int = navBarItems.indexOfFirst {
                    it.route == currentDestination
                }
                Log.i("Bottom Index", selectedIndex.toString())
                if (selectedIndex != -1) {
                    NavigationBar {
                        navBarItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == selectedIndex,
                                onClick = {
                                    navController.navigate(item.route)
                                },
                                icon = {
                                    item.icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = null
                                        )
                                    }
                                },
                                label = { Text(text = item.label) }
                            )
                        }
                    }
                }
            }
        ) {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background
            ) {
                SetupNavGraph(navHostController = navController)
            }
        }

    }
}
