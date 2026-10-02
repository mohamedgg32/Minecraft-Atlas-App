package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SeedCard
import com.example.ui.viewmodel.ForgeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LibraryScreen(
    viewModel: ForgeViewModel,
    modifier: Modifier = Modifier
) {
    val seeds by viewModel.filteredSeeds.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val versionFilter by viewModel.versionFilter.collectAsStateWithLifecycle()
    val onlyFavorites by viewModel.onlyFavorites.collectAsStateWithLifecycle()

    val versions = remember { listOf("All", "26.2", "1.21.x", "1.20.x", "1.19.x", "1.18.x", "Older") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title and count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SEED LIBRARY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFF0F6FC),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${seeds.size} saved Java seeds",
                    fontSize = 12.sp,
                    color = Color(0xFF8B949E)
                )
            }

            // Favorites Toggle Button
            IconButton(
                onClick = { viewModel.toggleFavoritesFilter() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (onlyFavorites) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF161B22))
                    .border(
                        1.dp,
                        if (onlyFavorites) Color(0xFFEF4444) else Color(0xFF30363D),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = if (onlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Filter favorites",
                    tint = if (onlyFavorites) Color(0xFFEF4444) else Color(0xFF8B949E)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by title, seed number, biome, or tags...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF8B949E))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search", tint = Color(0xFF8B949E))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("library_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFF30363D),
                focusedTextColor = Color(0xFFF0F6FC),
                unfocusedTextColor = Color(0xFFF0F6FC),
                focusedContainerColor = Color(0xFF161B22),
                unfocusedContainerColor = Color(0xFF161B22)
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Rows: Minecraft Version & Loader
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            versions.forEach { ver ->
                val isSelected = versionFilter == ver
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) Color(0xFF10B981) else Color(0xFF161B22))
                        .border(1.dp, if (isSelected) Color(0xFFA7F3D0) else Color(0xFF30363D), RoundedCornerShape(6.dp))
                        .clickable { viewModel.setVersionFilter(ver) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = ver,
                        color = if (isSelected) Color(0xFF003822) else Color(0xFFC9D1D9),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Seed Cards List
        if (seeds.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        tint = Color(0xFF8B949E),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No seeds found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF0F6FC)
                    )
                    Text(
                        text = "Try clearing filters or forging a new seed in the Forge tab.",
                        fontSize = 12.sp,
                        color = Color(0xFF8B949E)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(seeds, key = { it.id }) { seed ->
                    SeedCard(
                        seed = seed,
                        onOpenMap = { viewModel.loadSeedConfiguration(it) },
                        onInspect = { seedVal, ver, _ ->
                            viewModel.inspectInAnalyzer(seedVal, ver)
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onDelete = { viewModel.deleteSeed(it) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
