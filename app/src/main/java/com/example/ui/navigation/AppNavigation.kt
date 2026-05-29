package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.MiniPlayer
import com.example.ui.player.PlayerViewModel
import com.example.ui.screen.LibraryScreen
import com.example.ui.screen.PlayerScreen
import com.example.ui.screen.SearchScreen
import com.example.ui.screen.SettingsScreen
import com.example.ui.search.SearchViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Library : Screen("library", "Library", Icons.Default.LibraryMusic)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val playbackState by playerViewModel.playbackState.collectAsState()
    
    var showPlayerScreen by remember { mutableStateOf(false) }

    val items = listOf(Screen.Search, Screen.Library, Screen.Settings)

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                if (playbackState.currentMediaId != null) {
                    MiniPlayer(
                        playbackState = playbackState,
                        onPlayPauseClick = {
                            if (playbackState.isPlaying) playerViewModel.pause()
                            else playerViewModel.resume()
                        },
                        onNextClick = { playerViewModel.skipToNext() },
                        onExpandClick = { showPlayerScreen = true }
                    )
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            NavHost(navController = navController, startDestination = Screen.Search.route) {
                composable(Screen.Search.route) {
                    val searchViewModel: SearchViewModel = hiltViewModel()
                    val searchState by searchViewModel.uiState.collectAsState()
                    SearchScreen(
                        state = searchState,
                        onQueryChange = searchViewModel::onQueryChanged,
                        onSongClick = { song ->
                            playerViewModel.playSong(song)
                        }
                    )
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onSongClick = { song ->
                            playerViewModel.playSong(song)
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showPlayerScreen,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        PlayerScreen(
            playbackState = playbackState,
            onPlayPauseClick = {
                if (playbackState.isPlaying) playerViewModel.pause()
                else playerViewModel.resume()
            },
            onNextClick = { playerViewModel.skipToNext() },
            onPreviousClick = { playerViewModel.skipToPrevious() },
            onSeekTo = { playerViewModel.seekTo(it) },
            onCloseClick = { showPlayerScreen = false }
        )
    }
}
