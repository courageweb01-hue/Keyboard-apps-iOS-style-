package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.KeyboardTheme
import com.example.ui.screens.CustomizerScreen
import com.example.ui.screens.PlaygroundScreen
import com.example.ui.screens.QuickPhrasesScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.StatsScreen

enum class MainTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PLAYGROUND("Playground", Icons.Default.Keyboard),
    CUSTOMIZER("Studio", Icons.Default.ColorLens),
    QUICK_PHRASES("Phrases", Icons.Default.FlashOn),
    SETUP("Setup", Icons.Default.Settings),
    STATS("Stats", Icons.Default.BarChart)
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(MainTab.PLAYGROUND) }

    val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
    val allThemes by viewModel.allThemes.collectAsStateWithLifecycle()
    val allPhrases by viewModel.allPhrases.collectAsStateWithLifecycle()
    val recentStats by viewModel.recentStats.collectAsStateWithLifecycle()
    val totalKeystrokes by viewModel.totalKeystrokes.collectAsStateWithLifecycle()
    val averageWpm by viewModel.averageWpm.collectAsStateWithLifecycle()

    val accentColor = remember(activeTheme) {
        try {
            Color(android.graphics.Color.parseColor(activeTheme.accentColorHex))
        } catch (_: Exception) {
            Color(0xFF818CF8)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        containerColor = Color(0xFF0F172A),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_navigation_bar")
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) accentColor else Color.White.copy(alpha = 0.6f)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = accentColor.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (selectedTab) {
                MainTab.PLAYGROUND -> {
                    PlaygroundScreen(
                        activeTheme = activeTheme,
                        quickPhrases = allPhrases,
                        onLogSession = { wpm, count, secs, key ->
                            viewModel.logTypingSession(wpm, count, secs, key)
                        }
                    )
                }
                MainTab.CUSTOMIZER -> {
                    CustomizerScreen(
                        activeTheme = activeTheme,
                        allThemes = allThemes,
                        onSelectTheme = { viewModel.selectTheme(it) },
                        onUpdateTheme = { viewModel.updateActiveTheme(it) },
                        onSaveTheme = { viewModel.saveCustomTheme(it) },
                        onDeleteTheme = { viewModel.deleteTheme(it) }
                    )
                }
                MainTab.QUICK_PHRASES -> {
                    QuickPhrasesScreen(
                        phrases = allPhrases,
                        activeTheme = activeTheme,
                        onAddPhrase = { title, text, category ->
                            viewModel.addQuickPhrase(title, text, category)
                        },
                        onDeletePhrase = { viewModel.deleteQuickPhrase(it) }
                    )
                }
                MainTab.SETUP -> {
                    SetupScreen(activeTheme = activeTheme)
                }
                MainTab.STATS -> {
                    StatsScreen(
                        stats = recentStats,
                        totalKeystrokes = totalKeystrokes ?: 0,
                        averageWpm = averageWpm ?: 0f,
                        activeTheme = activeTheme
                    )
                }
            }
        }
    }
}
