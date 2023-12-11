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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.dubproductions.bracket.navigation.Screen
import com.dubproductions.bracket.navigation.SetupNavGraph
import com.dubproductions.bracket.ui.theme.BracketTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    navController: NavHostController
) {
    var selectedNavBarItem by rememberSaveable { mutableStateOf(0) }
    var bottomBarVisible by rememberSaveable { mutableStateOf(false) }

    val navBarItems: List<Screen> = listOf(Screen.Home, Screen.Hosting, Screen.Participating, Screen.Settings)

    BracketTheme {
        Scaffold(
            bottomBar = {
                if (bottomBarVisible) {
                    NavigationBar {
                        navBarItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedNavBarItem == index,
                                onClick = {
                                    selectedNavBarItem = index
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
                SetupNavGraph(navHostController = navController) { visible ->
                    bottomBarVisible = visible
                }
            }
        }
        
    }
}
