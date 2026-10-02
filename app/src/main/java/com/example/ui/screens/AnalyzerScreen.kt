package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.LoaderBadge
import com.example.ui.components.VersionChip
import com.example.ui.viewmodel.ForgeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyzerScreen(
    viewModel: ForgeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val seedInput by viewModel.analyzerSeedInput.collectAsStateWithLifecycle()
    val selectedVersion by viewModel.analyzerVersion.collectAsStateWithLifecycle()
    val selectedLoader by viewModel.analyzerLoader.collectAsStateWithLifecycle()
    val result by viewModel.analyzerResult.collectAsStateWithLifecycle()

    val versions = remember { listOf("26.2", "1.21.x", "1.20.x", "1.19.x", "1.18.x", "Older") }
    val loaders = remember { listOf("Fabric") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SEED & COMPATIBILITY ANALYZER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF0F6FC),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Cross-version compatibility matrix & multi-noise analysis",
                        fontSize = 12.sp,
                        color = Color(0xFF8B949E)
                    )
                }
            }
        }

        // Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF30363D))
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ANALYSIS PARAMETERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B949E),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = seedInput,
                        onValueChange = { viewModel.setAnalyzerSeedInput(it) },
                        label = { Text("Java Seed or String", fontSize = 12.sp) },
                        placeholder = { Text("e.g. -4270425838048259167 or 'MySeed'") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analyzer_seed_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF22D3EE),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color(0xFFF0F6FC),
                            unfocusedTextColor = Color(0xFFF0F6FC)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Target Minecraft Java Version:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8B949E)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        versions.forEach { ver ->
                            val isSelected = selectedVersion == ver
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFF10B981) else Color(0xFF21262D))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFFA7F3D0) else Color(0xFF30363D),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setAnalyzerVersion(ver) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = ver,
                                    color = if (isSelected) Color(0xFF003822) else Color(0xFFF0F6FC),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3B0764), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mod Loader",
                            color = Color(0xFFE9D5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Fabric ONLY",
                            color = Color(0xFFE9D5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (selectedVersion == "26.2") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF3B2304), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Status: Estimated. Exact Minecraft Java 26.2 generation is estimated via 64-bit multi-noise sampling.",
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Command Copy Formats
        result?.let { res ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF30363D))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "COPY FORMATS & COMMANDS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B949E),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Raw Seed", res.seedCopyFormats.rawSeed))
                                    Toast.makeText(context, "Copied Seed: ${res.seedCopyFormats.rawSeed}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D))
                            ) {
                                Text("Raw Seed", fontSize = 11.sp, color = Color(0xFF22D3EE))
                            }

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Server Properties", res.seedCopyFormats.serverProperties))
                                    Toast.makeText(context, "Copied: ${res.seedCopyFormats.serverProperties}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D))
                            ) {
                                Text("Server Prop", fontSize = 11.sp, color = Color(0xFFE6EDF3))
                            }

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("TP Command", res.seedCopyFormats.tpSpawnCommand))
                                    Toast.makeText(context, "Copied TP Command", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D))
                            ) {
                                Text("TP Spawn", fontSize = 11.sp, color = Color(0xFF10B981))
                            }
                        }
                    }
                }
            }

            // Version Compatibility Matrix
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF30363D))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CROSS-VERSION COMPATIBILITY MATRIX",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B949E),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Target: ${res.targetVersion}",
                                fontSize = 11.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            res.compatibilityReports.forEach { report ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0D1117), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF21262D), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "vs. Minecraft ${report.evaluatedVersion}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFFF0F6FC)
                                        )
                                        CompatibilityBadge(level = report.level)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = report.explanation,
                                        fontSize = 12.sp,
                                        color = Color(0xFFC9D1D9),
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Biome Match: ${report.biomeSimilarityPercent}%",
                                            fontSize = 11.sp,
                                            color = Color(0xFF8B949E)
                                        )
                                        Text(
                                            text = "Structures: ${report.structureConsistency}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF8B949E)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Multi-noise Climate & Terrain Analysis
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF30363D))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Color(0xFF22D3EE),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MULTI-NOISE & CLIMATE PROFILE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B949E),
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow(label = "Climate Zone", value = res.climateZone)
                            InfoRow(label = "Estimated Spawn Biome", value = res.estimatedSpawnBiome)
                            InfoRow(label = "Terrain Elevation", value = res.terrainElevationEstimate)
                            InfoRow(label = "Underground & Caves", value = res.undergroundFeatures)
                        }
                    }
                }
            }

            // Calculated Points of Interest
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF30363D))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CALCULATED POI COORDINATES & TELEPORTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B949E),
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            res.keyPoiEstimates.forEach { poi ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0D1117), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = poi.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFF0F6FC)
                                        )
                                        Text(
                                            text = "${poi.toCoordinatesString()} (${poi.dimension})",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = Color(0xFF22D3EE)
                                        )
                                        if (poi.description.isNotBlank()) {
                                            Text(
                                                text = poi.description,
                                                fontSize = 11.sp,
                                                color = Color(0xFF8B949E)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("TP Command", poi.toTpCommand()))
                                            Toast.makeText(context, "Copied: ${poi.toTpCommand()}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy command",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Modded Impact
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF30363D))
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AltRoute,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WORLDGEN MOD IMPACT ANALYSIS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B949E),
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = res.moddedImpactSummary,
                            fontSize = 12.sp,
                            color = Color(0xFFC9D1D9),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1117), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B949E),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color(0xFFE6EDF3),
            lineHeight = 16.sp
        )
    }
}
