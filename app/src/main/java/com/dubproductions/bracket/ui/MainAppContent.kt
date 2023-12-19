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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dubproductions.bracket.navigation.MainNavHost
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.ui.theme.BracketTheme
import com.dubproductions.bracket.viewmodel.AppViewModel
import com.dubproductions.bracket.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    val navBarItems: List<Screen> = listOf(
        Screen.Home,
        Screen.Hosting,
        Screen.Participating,
        Screen.Settings
    )
    val userViewModel: UserViewModel = hiltViewModel()
    val appViewModel: AppViewModel = viewModel()

    BracketTheme {
        val mainNavController = rememberNavController()
        val bottomBarNavController = rememberNavController()
        val currentDestination by bottomBarNavController.currentBackStackEntryAsState()
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
                                    bottomBarNavController.navigate(item.route) {
                                        popUpTo(bottomBarNavController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                       (if (index == selectedIndex) {
                                           item.selectedIcon
                                       } else {
                                           item.unselectedIcon
                                       })?.let {
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
                    appViewModel = appViewModel,
                    bottomBarNavController = bottomBarNavController
                )
            }
        }

    }
}
