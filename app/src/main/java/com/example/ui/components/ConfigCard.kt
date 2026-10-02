package com.example.ui.components

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ModEntity
import com.example.data.model.WorldConfigEntity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfigCard(
    config: WorldConfigEntity,
    allMods: List<ModEntity>,
    isSelected: Boolean,
    onSelectForForge: (WorldConfigEntity) -> Unit,
    onDelete: (WorldConfigEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val modIdsList = config.modIds.split(",").mapNotNull { it.trim().toLongOrNull() }
    val includedMods = allMods.filter { it.id in modIdsList }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("config_card_${config.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isSelected) Color(0xFF10B981) else Color(0xFF30363D)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = config.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF0F6FC)
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF064E3B), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Active Preset",
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else if (!config.isDefault) {
                    IconButton(
                        onClick = { onDelete(config) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete configuration",
                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges row: Loader, Version, Mod count
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoaderBadge(loader = config.loader)
                VersionChip(version = config.minecraftVersion)
                Box(
                    modifier = Modifier
                        .background(Color(0xFF21262D), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (includedMods.isEmpty()) "Vanilla Generation" else "${includedMods.size} Worldgen Mods",
                        color = Color(0xFF8B949E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = config.description,
                fontSize = 13.sp,
                color = Color(0xFFC9D1D9),
                lineHeight = 18.sp
            )

            // Included mods pills
            if (includedMods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "INCLUDED WORLDGEN MODS:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B949E),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    includedMods.forEach { mod ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0D1117), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = mod.name,
                                    color = Color(0xFFE6EDF3),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${mod.category})",
                                    color = Color(0xFF8B949E),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isSelected) {
                OutlinedButton(
                    onClick = { onSelectForForge(config) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Use for Seed Forging", fontSize = 12.sp)
                }
            } else {
                Button(
                    onClick = { onSelectForForge(config) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF064E3B)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFFA7F3D0),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Active in Forge", color = Color(0xFFA7F3D0), fontSize = 12.sp)
                }
            }
        }
    }
}
