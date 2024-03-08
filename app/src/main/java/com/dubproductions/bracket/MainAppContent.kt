package com.dubproductions.bracket

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dubproductions.bracket.core.presentation.navigation.Map
import com.dubproductions.bracket.core.presentation.navigation.NavHost
import com.dubproductions.bracket.core.presentation.navigation.Screen
import com.dubproductions.bracket.core.presentation.theme.BracketTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    loggedIn: Boolean,
    setLoggedInStatus: (Boolean) -> Unit
) {
    val navBarItems = listOf(
        Map.Home,
        Map.Hosting,
        Map.Participating,
        Map.Settings
    )

    val screensList = listOf(
        Screen.Login,
        Screen.Registration,
        Screen.Home,
        Screen.Hosting,
        Screen.Participating,
        Screen.Settings,
        Screen.TournamentCreation,
        Screen.EditTournament,
        Screen.Participants,
        Screen.ParticipantMatches,
        Screen.Bracket
    )

    val noBackButtonScreens = listOf(
        Screen.Login,
        Screen.Home,
        Screen.Hosting,
        Screen.Participating,
        Screen.Settings
    )

    BracketTheme {

        val navController = rememberNavController()
        val currentDestination by navController.currentBackStackEntryAsState()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            bottomBar = {
                val selectedIndex: Int = navBarItems.indexOfFirst {
                    currentDestination?.destination?.parent?.route == it.route
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
            },

            topBar = {
                val currentDestinationTitle = currentDestination?.destination?.route
                val currentScreen = screensList.find { currentDestinationTitle == it.route }

                CenterAlignedTopAppBar(
                    title = { Text(text = currentScreen?.label.toString()) },
                    navigationIcon = {
                        if (currentScreen !in noBackButtonScreens) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
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
                    loggedIn = loggedIn,
                    setLoggedInStatus = setLoggedInStatus
                )
            }
        }

    }
}
