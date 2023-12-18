package com.dubproductions.bracket.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dubproductions.bracket.navigation.MainNavHost
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.ui.theme.BracketTheme
import com.dubproductions.bracket.viewmodel.AppViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    mainNavController: NavHostController,
) {
    val navBarItems: List<Screen> = listOf(Screen.Home, Screen.Hosting, Screen.Participating, Screen.Settings)
    val currentDestination by mainNavController.currentBackStackEntryAsState()
    val userViewModel: UserViewModel = hiltViewModel()
    val appViewModel: AppViewModel = viewModel()

    BracketTheme {
        Scaffold(
            bottomBar = {
                val selectedIndex: Int = navBarItems.indexOfFirst {
                    currentDestination?.destination?.route == it.route
                }
                if (selectedIndex != -1) {
                    NavigationBar {
                        navBarItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == selectedIndex,
                                onClick = {
                                    mainNavController.navigate(item.route) {
                                        popUpTo(mainNavController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
                MainNavHost(
                    mainNavController = mainNavController,
                    userViewModel = userViewModel,
                    appViewModel = appViewModel
                )
            }
        }

    }
}
