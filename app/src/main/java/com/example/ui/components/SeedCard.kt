package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompatibilityLevel
import com.example.data.model.PoiCoordinate
import com.example.data.model.SeedEntity
import org.json.JSONArray

import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SeedCard(
    seed: SeedEntity,
    onOpenMap: (SeedEntity) -> Unit = {},
    onInspect: (Long, String, String) -> Unit,
    onToggleFavorite: (SeedEntity) -> Unit,
    onDelete: (SeedEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expandedPois by remember { mutableStateOf(false) }

    val pois = remember(seed.poiCoordinatesJson) {
        try {
            val array = JSONArray(seed.poiCoordinatesJson)
            val list = mutableListOf<PoiCoordinate>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PoiCoordinate(
                        name = obj.optString("name", "POI"),
                        x = obj.optInt("x", 0),
                        y = obj.optInt("y", 64),
                        z = obj.optInt("z", 0),
                        dimension = obj.optString("dimension", "Overworld"),
                        description = obj.optString("description", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("seed_card_${seed.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF161B22)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF30363D))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title, Category & Favorite Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = seed.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF0F6FC)
                    )
                    Text(
                        text = seed.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8B949E)
                    )
                }

                IconButton(
                    onClick = { onToggleFavorite(seed) },
                    modifier = Modifier.testTag("favorite_btn_${seed.id}")
                ) {
                    Icon(
                        imageVector = if (seed.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (seed.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (seed.isFavorite) Color(0xFFEF4444) else Color(0xFF8B949E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seed Value Box with Monospace font and one-tap copy
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1117), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF21262D), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "JAVA SEED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B949E),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = seed.seedValue.toString(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF22D3EE)
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Minecraft Seed", seed.seedValue.toString()))
                            Toast.makeText(context, "Copied seed: ${seed.seedValue}", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy raw seed",
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Version, Loader, Biome
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                VersionChip(version = seed.targetVersion)
                LoaderBadge(loader = seed.loader)
                if (seed.configName.isNotBlank() && seed.configName != "Vanilla") {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF21262D), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Preset: " + seed.configName,
                            color = Color(0xFFA855F7),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF064E3B).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = seed.primaryBiome,
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Spawn coordinates
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Spawn: ${seed.spawnCoordinates}",
                    fontSize = 12.sp,
                    color = Color(0xFF8B949E),
                    fontFamily = FontFamily.Monospace
                )
            }

            // Expandable POIs section
            if (pois.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Points of Interest (${pois.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF58A6FF)
                    )
                    IconButton(
                        onClick = { expandedPois = !expandedPois },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (expandedPois) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expandedPois) "Collapse POIs" else "Expand POIs",
                            tint = Color(0xFF58A6FF)
                        )
                    }
                }

                AnimatedVisibility(visible = expandedPois) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        pois.forEach { poi ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0D1117), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = poi.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE6EDF3)
                                    )
                                    Text(
                                        text = poi.toCoordinatesString() + " (${poi.dimension})",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF8B949E)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("TP Command", poi.toTpCommand()))
                                        Toast.makeText(context, "Copied: ${poi.toTpCommand()}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Navigation,
                                        contentDescription = "Copy teleport command",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (seed.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = seed.notes,
                    fontSize = 12.sp,
                    color = Color(0xFF8B949E),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: World Map, Analyze, Properties, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onOpenMap(seed) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("map_btn_${seed.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = Color(0xFF003822),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("World Map", color = Color(0xFF003822), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = { onInspect(seed.seedValue, seed.targetVersion, seed.loader) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("analyze_btn_${seed.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Analyze", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Server Properties", "level-seed=${seed.seedValue}"))
                        Toast.makeText(context, "Copied level-seed=${seed.seedValue}", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Props", fontSize = 11.sp)
                }

                IconButton(
                    onClick = { onDelete(seed) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete seed",
                        tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
