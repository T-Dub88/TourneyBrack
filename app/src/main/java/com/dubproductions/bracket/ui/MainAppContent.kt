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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dubproductions.bracket.navigation.Map
import com.dubproductions.bracket.navigation.NavHost
import com.dubproductions.bracket.ui.theme.BracketTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(loggedIn: Boolean) {
    val navBarItems: List<Map> = listOf(
        Map.Home,
        Map.Hosting,
        Map.Participating,
        Map.Settings
    )

    BracketTheme {

        val navController = rememberNavController()
        val currentDestination by navController.currentBackStackEntryAsState()

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
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
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
                                label = { Text(text = item.label.toString()) }
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
                NavHost(
                    navController = navController,
                    loggedIn = loggedIn
                )
            }
        }

    }
}
