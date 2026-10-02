package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.worldmap.BiomeCategory
import com.example.domain.worldmap.MapBiome
import com.example.domain.worldmap.MapStructure
import com.example.domain.worldmap.StructureCategory
import com.example.domain.worldmap.WorldGenerationEngine
import com.example.domain.worldmap.WorldGenerationState
import com.example.ui.viewmodel.ForgeViewModel
import com.example.ui.viewmodel.MapLayer
import com.example.ui.viewmodel.SeedMapState
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Interactive Minecraft Seed Map screen powered by SeedMapState ViewModel.
 *
 * Implements smooth panning, zooming, and layer toggles that survive configuration changes.
 */
@Composable
fun WorldMapScreen(
    viewModel: ForgeViewModel,
    mapState: SeedMapState = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val worldState by viewModel.worldGenState.collectAsStateWithLifecycle()

    // Handle physical back button
    BackHandler {
        focusManager.clearFocus()
        viewModel.closeWorldMap()
    }

    // State from SeedMapState ViewModel (persisted across configuration changes)
    val centerX by mapState.centerX.collectAsStateWithLifecycle()
    val centerZ by mapState.centerZ.collectAsStateWithLifecycle()
    val zoom by mapState.zoom.collectAsStateWithLifecycle()

    val layerBiomes by mapState.layerBiomes.collectAsStateWithLifecycle()
    val layerVillages by mapState.layerVillages.collectAsStateWithLifecycle()
    val layerStructures by mapState.layerStructures.collectAsStateWithLifecycle()
    val layerSpawn by mapState.layerSpawn.collectAsStateWithLifecycle()
    val layerTerrain by mapState.layerTerrain.collectAsStateWithLifecycle()
    val layerOceans by mapState.layerOceans.collectAsStateWithLifecycle()
    val layerCoordinates by mapState.layerCoordinates.collectAsStateWithLifecycle()

    val selectedStructure by mapState.selectedStructure.collectAsStateWithLifecycle()
    val inspectedLocation by mapState.inspectedLocation.collectAsStateWithLifecycle()
    val structureSearchQuery by mapState.structureSearchQuery.collectAsStateWithLifecycle()
    val biomeSearchQuery by mapState.biomeSearchQuery.collectAsStateWithLifecycle()

    val biomeSearchResult by viewModel.biomeSearchResult.collectAsStateWithLifecycle()

    // Local dialog / sheet states
    var showSearchSheet by remember { mutableStateOf(false) }
    var showLayersSheet by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitleInput by remember { mutableStateOf("") }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpXInput by remember { mutableStateOf("0") }
    var jumpZInput by remember { mutableStateOf("0") }

    val state = worldState
    if (state == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0D1117)),
            contentAlignment = Alignment.Center
        ) {
            Text("Calculating Java 26.2 World Generation...", color = Color(0xFF8B949E))
        }
        return
    }

    // Initialize map position to spawn if center coordinates are at default origin (0, 0)
    LaunchedEffect(state.seedValue, state.minecraftVersion) {
        if (centerX == 0f && centerZ == 0f) {
            mapState.centerOnSpawn(state.spawnX, state.spawnZ)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A0E))
    ) {
        // ==========================================
        // 1. MAIN INTERACTIVE MAP COMPOSABLE (Drag-to-Pan & Pinch-to-Zoom)
        // ==========================================
        MainMap(
            worldState = state,
            mapState = mapState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_map_composable"),
            onMapTapped = { focusManager.clearFocus() }
        )

        // ==========================================
        // 2. COMPACT TOP SEED INFORMATION BAR
        // ==========================================
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = Color(0xF2161B22),
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.closeWorldMap()
                        },
                        modifier = Modifier.testTag("map_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Generator",
                            tint = Color(0xFFF0F6FC)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Seed: ",
                                color = Color(0xFF8B949E),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${state.seedValue}",
                                color = Color(0xFF10B981),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Minecraft Seed", state.seedValue.toString())
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Seed copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Seed",
                                    tint = Color(0xFF22D3EE),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Version & Fabric loader chips
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "Minecraft Java ${state.minecraftVersion}${if (state.minecraftVersion == "26.2") " — Latest" else ""}",
                                color = Color(0xFFF0F6FC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = " • Fabric",
                                color = Color(0xFFC084FC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (state.activeMods.isNotEmpty()) {
                                Text(
                                    text = " • ${state.activeMods.size} Mods Active",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Save Seed button
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            saveTitleInput = "${state.spawnBiome.name} (${state.minecraftVersion})"
                            showSaveDialog = true
                        },
                        modifier = Modifier.testTag("map_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Save Seed",
                            tint = Color(0xFF10B981)
                        )
                    }
                }

                // 26.2 Estimated Notice Banner
                if (state.minecraftVersion == "26.2") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Java 26.2 generation is Estimated via multi-noise sampling & Java LCG algorithms.",
                            color = Color(0xFFBAE6FD),
                            fontSize = 11.sp
                        )
                    }
                }

                // Unsupported Mod Notice Banner
                if (state.unsupportedModNotices.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3B2304))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = state.unsupportedModNotices.first(),
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. TARGET COORDINATES HUD (Top-Left)
        // ==========================================
        if (layerCoordinates) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 88.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xD0161B22)),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF30363D), Color(0xFF30363D))))
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text(
                        text = "CENTER TARGET",
                        color = Color(0xFF8B949E),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "X: ${centerX.roundToInt()}   Z: ${centerZ.roundToInt()}",
                        color = Color(0xFFF0F6FC),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    val distSpawn = sqrt(((centerX - state.spawnX) * (centerX - state.spawnX) + (centerZ - state.spawnZ) * (centerZ - state.spawnZ)).toDouble()).roundToInt()
                    Text(
                        text = "Dist: ${distSpawn}m from Spawn",
                        color = Color(0xFF22D3EE),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // ==========================================
        // 4. BIOME SEARCH RESULT BANNER
        // ==========================================
        if (biomeSearchResult != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 150.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xF01E293B)),
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF38BDF8))))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(biomeSearchResult!!.biome.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Found: ${biomeSearchResult!!.biome.name}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        IconButton(
                            onClick = { viewModel.searchBiome("") },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                        }
                    }
                    Text(
                        text = "Coordinates: X: ${biomeSearchResult!!.foundX}, Z: ${biomeSearchResult!!.foundZ} (${biomeSearchResult!!.distanceFromSpawn}m away)",
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // ==========================================
        // 5. INSPECTED BIOME AREA DETAIL CARD
        // ==========================================
        if (inspectedLocation != null) {
            val ins = inspectedLocation!!
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 74.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xF8161B22)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(ins.biome.color, ins.biome.color)))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(ins.biome.color, RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = ins.biome.name,
                                    color = Color(0xFFF0F6FC),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = ins.biome.category.name,
                                    color = Color(0xFF8B949E),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = { mapState.clearInspection() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8B949E))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Coordinates: X: ${ins.x}, Z: ${ins.z}  (${ins.distanceFromSpawn}m from Spawn)",
                        color = Color(0xFF22D3EE),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    if (ins.biome.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = ins.biome.description,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val coordText = "${ins.x}, ${ins.z}"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Coordinates", coordText))
                                Toast.makeText(context, "Coordinates copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Coords", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { mapState.jumpTo(ins.x, ins.z) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Center Here", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 6. SELECTED STRUCTURE DETAIL CARD
        // ==========================================
        if (selectedStructure != null) {
            val struct = selectedStructure!!
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 74.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xF8161B22)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(struct.badgeColor, struct.badgeColor)))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(struct.badgeColor, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = struct.iconVector,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = struct.name,
                                    color = Color(0xFFF0F6FC),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = struct.category.label,
                                    color = struct.badgeColor,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = { mapState.clearSelectedStructure() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8B949E))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Coordinates: X: ${struct.x}, Y: ${struct.y}, Z: ${struct.z}  (${struct.distanceFrom(state.spawnX, state.spawnZ)}m from spawn)",
                        color = Color(0xFF22D3EE),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    if (struct.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = struct.description,
                            color = Color(0xFFC9D1D9),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val tpCmd = "/tp @s ${struct.x} ${struct.y} ${struct.z}"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Minecraft Teleport", tpCmd))
                                Toast.makeText(context, "Copied: $tpCmd", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("/tp Command", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                mapState.jumpTo(struct.x, struct.z)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Center Here", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 7. FLOATING MAP CONTROLS (Right Side)
        // ==========================================
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { mapState.zoomIn() },
                containerColor = Color(0xFF21262D),
                contentColor = Color(0xFFF0F6FC),
                modifier = Modifier.testTag("map_zoom_in")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            SmallFloatingActionButton(
                onClick = { mapState.zoomOut() },
                containerColor = Color(0xFF21262D),
                contentColor = Color(0xFFF0F6FC),
                modifier = Modifier.testTag("map_zoom_out")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            SmallFloatingActionButton(
                onClick = { mapState.centerOnSpawn(state.spawnX, state.spawnZ) },
                containerColor = Color(0xFF10B981),
                contentColor = Color(0xFF003822),
                modifier = Modifier.testTag("map_center_spawn")
            ) {
                Icon(Icons.Default.Explore, contentDescription = "Center on Spawn")
            }

            SmallFloatingActionButton(
                onClick = {
                    jumpXInput = centerX.roundToInt().toString()
                    jumpZInput = centerZ.roundToInt().toString()
                    showJumpDialog = true
                },
                containerColor = Color(0xFF21262D),
                contentColor = Color(0xFF22D3EE),
                modifier = Modifier.testTag("map_jump_coords")
            ) {
                Icon(Icons.Default.LocationSearching, contentDescription = "Jump to Coordinates")
            }

            SmallFloatingActionButton(
                onClick = { mapState.resetZoom() },
                containerColor = Color(0xFF21262D),
                contentColor = Color(0xFF94A3B8),
                modifier = Modifier.testTag("map_reset_zoom")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Zoom")
            }
        }

        // ==========================================
        // 8. FLOATING BOTTOM BAR (Spawn, Layers, Search)
        // ==========================================
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color(0xFA161B22),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spawn Info Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { mapState.centerOnSpawn(state.spawnX, state.spawnZ) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFFFD700), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Spawn: ${state.spawnX}, ${state.spawnZ} (${state.spawnBiome.name})",
                        color = Color(0xFFC9D1D9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()
                            showLayersSheet = !showLayersSheet
                            showSearchSheet = false
                        },
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Layers", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            showSearchSheet = !showSearchSheet
                            showLayersSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF003822), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Search", color = Color(0xFF003822), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ==========================================
        // 9. LAYERS CONTROL SHEET
        // ==========================================
        if (showLayersSheet) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp, start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xF8161B22)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF30363D), Color(0xFF30363D))))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Map Layers (7 Available)",
                            color = Color(0xFFF0F6FC),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        IconButton(onClick = { showLayersSheet = false }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF8B949E))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = layerBiomes,
                            onClick = { mapState.toggleLayerBiomes() },
                            label = { Text("Biomes", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF10B981))
                        )
                        FilterChip(
                            selected = layerVillages,
                            onClick = { mapState.toggleLayerVillages() },
                            label = { Text("Villages", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF66BB6A))
                        )
                        FilterChip(
                            selected = layerStructures,
                            onClick = { mapState.toggleLayerStructures() },
                            label = { Text("Structures", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF00E5FF))
                        )
                        FilterChip(
                            selected = layerSpawn,
                            onClick = { mapState.toggleLayerSpawn() },
                            label = { Text("Spawn", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFD700))
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = layerTerrain,
                            onClick = { mapState.toggleLayerTerrain() },
                            label = { Text("Terrain", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFF59E0B))
                        )
                        FilterChip(
                            selected = layerOceans,
                            onClick = { mapState.toggleLayerOceans() },
                            label = { Text("Oceans", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF184E9E))
                        )
                        FilterChip(
                            selected = layerCoordinates,
                            onClick = { mapState.toggleLayerCoordinates() },
                            label = { Text("Coordinates", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF22D3EE))
                        )
                    }
                }
            }
        }

        // ==========================================
        // 10. SEARCH SHEET (Structures & Biomes)
        // ==========================================
        if (showSearchSheet) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp, start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xF8161B22)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF30363D), Color(0xFF30363D))))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Search World Features",
                            color = Color(0xFFF0F6FC),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                showSearchSheet = false
                            },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF8B949E))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = structureSearchQuery,
                        onValueChange = { mapState.setStructureSearchQuery(it) },
                        placeholder = { Text("Search Village, Ancient City, Stronghold, Trial...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Quick Structure Filter Chips
                    val quickPillList = listOf("Village", "Ancient City", "Trial Chamber", "Stronghold", "Mansion", "Witch Hut", "Mineshaft")
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickPillList) { pill ->
                            FilterChip(
                                selected = structureSearchQuery.contains(pill, ignoreCase = true),
                                onClick = {
                                    if (structureSearchQuery == pill) {
                                        mapState.setStructureSearchQuery("")
                                    } else {
                                        mapState.setStructureSearchQuery(pill)
                                    }
                                },
                                label = { Text(pill, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Matching structures list (up to 4)
                    val matchingStructs = WorldGenerationEngine.searchStructures(
                        query = structureSearchQuery,
                        structures = state.structures,
                        spawnX = state.spawnX,
                        spawnZ = state.spawnZ
                    ).take(4)

                    if (matchingStructs.isNotEmpty()) {
                        Text(
                            text = "Found ${matchingStructs.size} structures near spawn:",
                            color = Color(0xFF8B949E),
                            fontSize = 10.sp
                        )
                        for (s in matchingStructs) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        focusManager.clearFocus()
                                        mapState.selectStructure(s)
                                        showSearchSheet = false
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = s.name, color = Color(0xFFF0F6FC), fontSize = 12.sp)
                                Text(
                                    text = "X: ${s.x}, Z: ${s.z} (${s.distanceFrom(state.spawnX, state.spawnZ)}m)",
                                    color = Color(0xFF22D3EE),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Biome Finder (Estimates coordinates)",
                        color = Color(0xFF8B949E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        var biomeInput by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = biomeInput,
                            onValueChange = { biomeInput = it },
                            placeholder = { Text("Cherry Grove, Badlands, Desert...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.searchBiome(biomeInput)
                                showSearchSheet = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                        ) {
                            Text("Find", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 11. SAVE SEED DIALOG
        // ==========================================
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = {
                    focusManager.clearFocus()
                    showSaveDialog = false
                },
                title = { Text("Save Seed to Library", color = Color(0xFFF0F6FC)) },
                text = {
                    Column {
                        Text("Give this seed configuration a title:", color = Color(0xFF8B949E), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = saveTitleInput,
                            onValueChange = { saveTitleInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val activeModsDisplay = if (state.activeMods.isEmpty()) "None" else state.activeMods.joinToString(", ")
                        Text(
                            text = "Saved Configuration:\nSeed: ${state.seedValue}\nVersion: Minecraft Java ${state.minecraftVersion}\nLoader: Fabric\nSelected Mods: $activeModsDisplay",
                            color = Color(0xFF8B949E),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.saveCurrentSeed(saveTitleInput)
                            showSaveDialog = false
                            Toast.makeText(context, "Saved seed to library!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Save", color = Color(0xFF003822), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        showSaveDialog = false
                    }) {
                        Text("Cancel", color = Color(0xFF8B949E))
                    }
                },
                containerColor = Color(0xFF161B22)
            )
        }

        // ==========================================
        // 12. JUMP TO COORDINATES DIALOG
        // ==========================================
        if (showJumpDialog) {
            AlertDialog(
                onDismissRequest = {
                    focusManager.clearFocus()
                    showJumpDialog = false
                },
                title = { Text("Jump to Coordinates", color = Color(0xFFF0F6FC)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = jumpXInput,
                            onValueChange = { jumpXInput = it },
                            label = { Text("X Coordinate") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = jumpZInput,
                            onValueChange = { jumpZInput = it },
                            label = { Text("Z Coordinate") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val x = jumpXInput.toIntOrNull() ?: 0
                            val z = jumpZInput.toIntOrNull() ?: 0
                            mapState.jumpTo(x, z)
                            showJumpDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Go", color = Color(0xFF003822), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        showJumpDialog = false
                    }) {
                        Text("Cancel", color = Color(0xFF8B949E))
                    }
                },
                containerColor = Color(0xFF161B22)
            )
        }
    }
}

/**
 * Main Map composable providing interactive rendering and gesture controls.
 * Applies basic pointer input modifiers to support fluid drag-to-pan and pinch-to-zoom
 * functionality, seamlessly integrating with SeedMapState.
 */
@Composable
fun MainMap(
    worldState: WorldGenerationState,
    mapState: SeedMapState,
    modifier: Modifier = Modifier,
    onMapTapped: () -> Unit = {}
) {
    val centerX by mapState.centerX.collectAsStateWithLifecycle()
    val centerZ by mapState.centerZ.collectAsStateWithLifecycle()
    val zoom by mapState.zoom.collectAsStateWithLifecycle()

    val layerBiomes by mapState.layerBiomes.collectAsStateWithLifecycle()
    val layerVillages by mapState.layerVillages.collectAsStateWithLifecycle()
    val layerStructures by mapState.layerStructures.collectAsStateWithLifecycle()
    val layerSpawn by mapState.layerSpawn.collectAsStateWithLifecycle()
    val layerTerrain by mapState.layerTerrain.collectAsStateWithLifecycle()
    val layerOceans by mapState.layerOceans.collectAsStateWithLifecycle()
    val layerCoordinates by mapState.layerCoordinates.collectAsStateWithLifecycle()

    val selectedStructure by mapState.selectedStructure.collectAsStateWithLifecycle()
    val inspectedLocation by mapState.inspectedLocation.collectAsStateWithLifecycle()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("world_map_canvas")
            .pointerInput(mapState) {
                // Drag-to-pan and pinch-to-zoom pointer input modifiers integrated with SeedMapState
                detectTransformGestures(panZoomLock = false) { _, pan, gestureZoom, _ ->
                    // 1. Pinch-to-zoom integration: adjusts zoom smoothly
                    if (gestureZoom != 1.0f) {
                        mapState.zoomBy(gestureZoom)
                    }

                    // 2. Drag-to-pan integration: translates screen pixels to world blocks (1:1 tracking)
                    val currentZoom = mapState.zoom.value
                    val tileSize = (24f * currentZoom).coerceAtLeast(8f)
                    val worldStep = 48f
                    val scaleRatio = tileSize / worldStep
                    val deltaX = -pan.x / scaleRatio
                    val deltaZ = -pan.y / scaleRatio
                    mapState.pan(deltaX, deltaZ)
                }
            }
            .pointerInput(mapState) {
                detectTapGestures(
                    onDoubleTap = {
                        // Double-tap zoom step
                        mapState.zoomIn()
                    },
                    onTap = { tapOffset ->
                        onMapTapped()
                        val canvasW = size.width
                        val canvasH = size.height
                        val tileSize = (24f * zoom).coerceAtLeast(8f)
                        val worldStep = 48f
                        val scaleRatio = tileSize / worldStep

                        // 1. Check if clicked near any visible structure marker
                        var tappedStruct: MapStructure? = null
                        for (s in worldState.structures) {
                            val isVillage = s.category == StructureCategory.VILLAGE
                            if (isVillage && !layerVillages) continue
                            if (!isVillage && !layerStructures) continue

                            val screenX = canvasW / 2f + (s.x - centerX) * scaleRatio
                            val screenY = canvasH / 2f + (s.z - centerZ) * scaleRatio

                            val distToTap = sqrt(
                                ((tapOffset.x - screenX) * (tapOffset.x - screenX) +
                                    (tapOffset.y - screenY) * (tapOffset.y - screenY)).toDouble()
                            )
                            if (distToTap < 30.0) {
                                tappedStruct = s
                                break
                            }
                        }

                        if (tappedStruct != null) {
                            mapState.selectStructure(tappedStruct)
                        } else {
                            // 2. Clicked on map area -> inspect Biome at exact coordinates
                            val clickedWorldX = (centerX + (tapOffset.x - canvasW / 2f) / scaleRatio).roundToInt()
                            val clickedWorldZ = (centerZ + (tapOffset.y - canvasH / 2f) / scaleRatio).roundToInt()

                            val sampledBiome = WorldGenerationEngine.sampleBiomeAt(
                                seed = worldState.seedValue,
                                x = clickedWorldX,
                                z = clickedWorldZ,
                                version = worldState.minecraftVersion,
                                activeMods = worldState.activeMods
                            )

                            mapState.inspectLocation(
                                biome = sampledBiome,
                                x = clickedWorldX,
                                z = clickedWorldZ,
                                spawnX = worldState.spawnX,
                                spawnZ = worldState.spawnZ
                            )
                        }
                    }
                )
            }
    ) {
        val canvasW = size.width
        val canvasH = size.height
        val tileSize = (24f * zoom).coerceAtLeast(8f)
        val worldStep = 48 // block step per sample tile

        val cols = (canvasW / tileSize).toInt() + 4
        val rows = (canvasH / tileSize).toInt() + 4

        val startCol = -cols / 2
        val endCol = cols / 2
        val startRow = -rows / 2
        val endRow = rows / 2

        // --- Render Biome & Terrain Layer ---
        if (layerBiomes) {
            for (c in startCol..endCol) {
                for (r in startRow..endRow) {
                    val sampleX = (centerX + c * worldStep).roundToInt()
                    val sampleZ = (centerZ + r * worldStep).roundToInt()

                    val biome = WorldGenerationEngine.sampleBiomeAt(
                        seed = worldState.seedValue,
                        x = sampleX,
                        z = sampleZ,
                        version = worldState.minecraftVersion,
                        activeMods = worldState.activeMods
                    )

                    var tileColor = biome.color

                    // Ocean layer check: if ocean layer disabled, show muted water
                    val isWater = biome.category == BiomeCategory.OCEAN || biome.category == BiomeCategory.DEEP_OCEAN
                    if (isWater && !layerOceans) {
                        tileColor = Color(0xFF1E293B)
                    }

                    // Terrain elevation shading
                    if (layerTerrain) {
                        tileColor = when (biome.category) {
                            BiomeCategory.MOUNTAINS -> tileColor.copy(alpha = 0.95f)
                            BiomeCategory.CHERRY_GROVE -> tileColor.copy(alpha = 0.9f)
                            BiomeCategory.DEEP_OCEAN -> if (layerOceans) Color(0xFF081C44) else Color(0xFF151C28)
                            BiomeCategory.OCEAN -> if (layerOceans) Color(0xFF103B7B) else Color(0xFF1E293B)
                            else -> tileColor
                        }
                    }

                    val rectX = canvasW / 2f + c * tileSize
                    val rectY = canvasH / 2f + r * tileSize

                    drawRect(
                        color = tileColor,
                        topLeft = Offset(rectX, rectY),
                        size = Size(tileSize + 1f, tileSize + 1f)
                    )
                }
            }
        } else {
            // Background void grid when biomes layer is toggled off
            drawRect(color = Color(0xFF0D1117))
        }

        val scaleRatio = tileSize / worldStep.toFloat()

        // --- Coordinate Grid Overlay (512-block chunks) ---
        if (layerCoordinates) {
            val gridSpacing = 512f * scaleRatio
            if (gridSpacing > 30f) {
                val gridColor = Color(0x28FFFFFF)
                val offsetX = (canvasW / 2f - (centerX % 512) * scaleRatio) % gridSpacing
                val offsetZ = (canvasH / 2f - (centerZ % 512) * scaleRatio) % gridSpacing

                var curX = offsetX
                while (curX < canvasW) {
                    drawLine(gridColor, Offset(curX, 0f), Offset(curX, canvasH), strokeWidth = 1f)
                    curX += gridSpacing
                }

                var curY = offsetZ
                while (curY < canvasH) {
                    drawLine(gridColor, Offset(0f, curY), Offset(canvasW, curY), strokeWidth = 1f)
                    curY += gridSpacing
                }
            }
        }

        // --- Draw Spawn Point Beacon & Ring ---
        if (layerSpawn) {
            val spawnScreenX = canvasW / 2f + (worldState.spawnX - centerX) * scaleRatio
            val spawnScreenY = canvasH / 2f + (worldState.spawnZ - centerZ) * scaleRatio

            // Outer pulsating beacon ring
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = 0.35f),
                radius = 24f * zoom.coerceIn(0.8f, 2.0f),
                center = Offset(spawnScreenX, spawnScreenY)
            )
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 8f,
                center = Offset(spawnScreenX, spawnScreenY)
            )
            drawCircle(
                color = Color(0xFF003822),
                radius = 4f,
                center = Offset(spawnScreenX, spawnScreenY)
            )
        }

        // --- Draw Structures & Villages Pins ---
        for (struct in worldState.structures) {
            val isVillage = struct.category == StructureCategory.VILLAGE
            if (isVillage && !layerVillages) continue
            if (!isVillage && !layerStructures) continue

            val sx = canvasW / 2f + (struct.x - centerX) * scaleRatio
            val sy = canvasH / 2f + (struct.z - centerZ) * scaleRatio

            // Skip drawing pins outside canvas viewport
            if (sx < -40 || sx > canvasW + 40 || sy < -40 || sy > canvasH + 40) continue

            val isSelected = selectedStructure?.id == struct.id
            val pinRadius = if (isSelected) 14f else 8f

            if (isSelected) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = pinRadius + 6f,
                    center = Offset(sx, sy)
                )
            }

            drawCircle(
                color = struct.badgeColor,
                radius = pinRadius,
                center = Offset(sx, sy)
            )
            drawCircle(
                color = Color.Black,
                radius = pinRadius,
                center = Offset(sx, sy),
                style = Stroke(width = 2f)
            )
        }

        // --- Inspected location crosshair marker ---
        inspectedLocation?.let { ins ->
            val ix = canvasW / 2f + (ins.x - centerX) * scaleRatio
            val iy = canvasH / 2f + (ins.z - centerZ) * scaleRatio
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = 10f,
                center = Offset(ix, iy),
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                radius = 18f,
                center = Offset(ix, iy)
            )
        }

        // --- Center Screen Crosshair ---
        val crosshairSize = 12f
        val crossColor = Color(0x66FFFFFF)
        drawLine(
            crossColor,
            Offset(canvasW / 2f - crosshairSize, canvasH / 2f),
            Offset(canvasW / 2f + crosshairSize, canvasH / 2f),
            strokeWidth = 1.5f
        )
        drawLine(
            crossColor,
            Offset(canvasW / 2f, canvasH / 2f - crosshairSize),
            Offset(canvasW / 2f, canvasH / 2f + crosshairSize),
            strokeWidth = 1.5f
        )
    }
}

/**
 * Convenience alias for MainMap.
 */
@Composable
fun SeedMap(
    worldState: WorldGenerationState,
    mapState: SeedMapState,
    modifier: Modifier = Modifier,
    onMapTapped: () -> Unit = {}
) {
    MainMap(
        worldState = worldState,
        mapState = mapState,
        modifier = modifier,
        onMapTapped = onMapTapped
    )
}
