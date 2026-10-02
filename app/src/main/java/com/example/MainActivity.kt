package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AnalyzerScreen
import com.example.ui.screens.ConfigsScreen
import com.example.ui.screens.ForgeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ModsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ForgeViewModel

import androidx.compose.material.icons.filled.Map

class MainActivity : ComponentActivity() {
    private val viewModel: ForgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ForgeViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isShowingWorldMap by viewModel.isShowingWorldMap.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0D1117),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF10B981), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = Color(0xFF003822),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "  Seed Generate",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color(0xFFF0F6FC),
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF161B22)
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(Color(0xFF3B0764), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "26.2 • Fabric",
                            color = Color(0xFFE9D5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF161B22),
                contentColor = Color(0xFFE6EDF3),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.setTab(0)
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.Map, contentDescription = "Seed & Map")
                    },
                    label = { Text("Seed & Map", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003822),
                        selectedTextColor = Color(0xFF10B981),
                        indicatorColor = Color(0xFF10B981),
                        unselectedIconColor = Color(0xFF8B949E),
                        unselectedTextColor = Color(0xFF8B949E)
                    ),
                    modifier = Modifier.testTag("tab_forge")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.setTab(1)
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.ListAlt, contentDescription = "Library")
                    },
                    label = { Text("Library", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003822),
                        selectedTextColor = Color(0xFF10B981),
                        indicatorColor = Color(0xFF10B981),
                        unselectedIconColor = Color(0xFF8B949E),
                        unselectedTextColor = Color(0xFF8B949E)
                    ),
                    modifier = Modifier.testTag("tab_library")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.setTab(2)
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.Extension, contentDescription = "Mods")
                    },
                    label = { Text("Mods", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003822),
                        selectedTextColor = Color(0xFF10B981),
                        indicatorColor = Color(0xFF10B981),
                        unselectedIconColor = Color(0xFF8B949E),
                        unselectedTextColor = Color(0xFF8B949E)
                    ),
                    modifier = Modifier.testTag("tab_mods")
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.setTab(3)
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.Layers, contentDescription = "Presets")
                    },
                    label = { Text("Presets", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003822),
                        selectedTextColor = Color(0xFF10B981),
                        indicatorColor = Color(0xFF10B981),
                        unselectedIconColor = Color(0xFF8B949E),
                        unselectedTextColor = Color(0xFF8B949E)
                    ),
                    modifier = Modifier.testTag("tab_presets")
                )

                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.setTab(4)
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Analyzer")
                    },
                    label = { Text("Analyzer", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF003822),
                        selectedTextColor = Color(0xFF10B981),
                        indicatorColor = Color(0xFF10B981),
                        unselectedIconColor = Color(0xFF8B949E),
                        unselectedTextColor = Color(0xFF8B949E)
                    ),
                    modifier = Modifier.testTag("tab_analyzer")
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TabContentTransition"
            ) { tab ->
                when (tab) {
                    0 -> ForgeScreen(viewModel = viewModel)
                    1 -> LibraryScreen(viewModel = viewModel)
                    2 -> ModsScreen(viewModel = viewModel)
                    3 -> ConfigsScreen(viewModel = viewModel)
                    4 -> AnalyzerScreen(viewModel = viewModel)
                    else -> ForgeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
