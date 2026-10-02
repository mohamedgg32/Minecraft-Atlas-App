package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.MinecraftVersion
import com.example.data.model.SeedArchetype
import com.example.domain.SeedCriteria
import com.example.domain.SeedGeneratorEngine
import com.example.ui.components.CompatibilityBadge
import com.example.ui.components.LoaderBadge
import com.example.ui.components.VersionChip
import com.example.ui.viewmodel.ForgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgeScreen(
    viewModel: ForgeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isShowingWorldMap by viewModel.isShowingWorldMap.collectAsStateWithLifecycle()

    // If World Map mode is active, display the interactive WorldMapScreen directly
    if (isShowingWorldMap) {
        WorldMapScreen(viewModel = viewModel, modifier = modifier)
        return
    }

    val currentSeed by viewModel.currentSeed.collectAsStateWithLifecycle()
    val seedInputText by viewModel.seedInputText.collectAsStateWithLifecycle()
    val selectedVersion by viewModel.selectedVersion.collectAsStateWithLifecycle()
    val selectedWorldGenMods by viewModel.selectedWorldGenMods.collectAsStateWithLifecycle()
    val allMods by viewModel.allModsRaw.collectAsStateWithLifecycle()
    val compatibilityWarnings by viewModel.compatibilityWarnings.collectAsStateWithLifecycle()
    val selectedGeneratorMode by viewModel.selectedGeneratorMode.collectAsStateWithLifecycle()
    val selectedArchetype by viewModel.selectedArchetype.collectAsStateWithLifecycle()
    val latestGeneratedResult by viewModel.latestGeneratedResult.collectAsStateWithLifecycle()
    val seedCriteria by viewModel.seedCriteria.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()

    var versionDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Hero Banner Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF22D3EE))))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_seed_forge),
                        contentDescription = "Minecraft Seed Generate",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xE00D1117))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Seed Generate",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFC084FC), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Fabric Only",
                                    color = Color(0xFF3B0764),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Analyze and map Minecraft Java 26.2 & Fabric seeds",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // ==========================================
        // SECTION 1: SEED GENERATOR & FORGE
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("seed_input_section_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF30363D), Color(0xFF30363D))))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Seed Generator & Forge",
                            color = Color(0xFFF0F6FC),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0F2D1F), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Active Engine",
                                color = Color(0xFF34D399),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Generator Mode Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val modes = listOf(
                            0 to "🏛️ Archetypes",
                            1 to "🔍 Filter",
                            2 to "🎲 64-Bit",
                            3 to "✨ Phrase"
                        )
                        for ((modeId, label) in modes) {
                            val isSelected = selectedGeneratorMode == modeId
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setGeneratorMode(modeId) }
                                    .testTag("generator_tab_$modeId"),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF10B981) else Color(0xFF21262D)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color(0xFF003822) else Color(0xFFC9D1D9),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // MODE 0: ARCHETYPES & BIOMES
                    if (selectedGeneratorMode == 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Select World Archetype:",
                                color = Color(0xFF8B949E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Horizontal Archetype Chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(SeedArchetype.entries) { archetype ->
                                    val isSelected = selectedArchetype == archetype
                                    val emoji = getArchetypeEmoji(archetype)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setSelectedArchetype(archetype) },
                                        label = {
                                            Text(
                                                text = "$emoji ${archetype.title}",
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF065F46),
                                            selectedLabelColor = Color(0xFF6EE7B7),
                                            containerColor = Color(0xFF21262D),
                                            labelColor = Color(0xFFC9D1D9)
                                        )
                                    )
                                }
                            }

                            // Spotlight Card for Selected Archetype
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF10B981)))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = getArchetypeEmoji(selectedArchetype),
                                                fontSize = 20.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = selectedArchetype.title,
                                                color = Color(0xFFF0F6FC),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = selectedArchetype.targetBiome,
                                                color = Color(0xFF38BDF8),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = selectedArchetype.description,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )

                                    // Curated POIs Preview
                                    latestGeneratedResult?.let { genResult ->
                                        if (genResult.coordinates.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Key Landmarks at Spawn:",
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            for (poi in genResult.coordinates.take(3)) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "• ${poi.name}",
                                                        color = Color(0xFFE2E8F0),
                                                        fontSize = 11.sp,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        text = "${poi.x}, ${poi.y}, ${poi.z}",
                                                        color = Color(0xFF34D399),
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { viewModel.generateArchetype(selectedArchetype) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_roll_archetype"),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Roll Seed for ${selectedArchetype.title}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // MODE 1: CUSTOM CRITERIA & FILTERS
                    if (selectedGeneratorMode == 1) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Require Features Near Spawn:",
                                color = Color(0xFF8B949E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Checkbox toggles for spawn features
                            val criteriaFeatures = listOf(
                                "Spawn near Village" to seedCriteria.requireVillage,
                                "Trial Chamber underground" to seedCriteria.requireTrialChamber,
                                "Ancient City beneath spawn" to seedCriteria.requireAncientCity,
                                "Woodland Mansion in 500b" to seedCriteria.requireMansion,
                                "Ocean Monument off coast" to seedCriteria.requireOceanMonument
                            )

                            for ((label, isChecked) in criteriaFeatures) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateCriteria(
                                                when (label) {
                                                    "Spawn near Village" -> seedCriteria.copy(requireVillage = !seedCriteria.requireVillage)
                                                    "Trial Chamber underground" -> seedCriteria.copy(requireTrialChamber = !seedCriteria.requireTrialChamber)
                                                    "Ancient City beneath spawn" -> seedCriteria.copy(requireAncientCity = !seedCriteria.requireAncientCity)
                                                    "Woodland Mansion in 500b" -> seedCriteria.copy(requireMansion = !seedCriteria.requireMansion)
                                                    else -> seedCriteria.copy(requireOceanMonument = !seedCriteria.requireOceanMonument)
                                                }
                                            )
                                        },
                                    colors = CardDefaults.cardColors(containerColor = if (isChecked) Color(0xFF1E293B) else Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                viewModel.updateCriteria(
                                                    when (label) {
                                                        "Spawn near Village" -> seedCriteria.copy(requireVillage = checked)
                                                        "Trial Chamber underground" -> seedCriteria.copy(requireTrialChamber = checked)
                                                        "Ancient City beneath spawn" -> seedCriteria.copy(requireAncientCity = checked)
                                                        "Woodland Mansion in 500b" -> seedCriteria.copy(requireMansion = checked)
                                                        else -> seedCriteria.copy(requireOceanMonument = checked)
                                                    }
                                                )
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = label, color = Color(0xFFF0F6FC), fontSize = 12.sp)
                                    }
                                }
                            }

                            // Preferred Biome selector chips
                            Text(
                                text = "Preferred Biome:",
                                color = Color(0xFF8B949E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            val biomesList = listOf("Any", "Cherry Grove", "Mushroom Fields", "Ice Spikes", "Badlands", "Plains")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(biomesList) { b ->
                                    val isSelected = seedCriteria.preferredBiome == b
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.updateCriteria(seedCriteria.copy(preferredBiome = b)) },
                                        label = { Text(b, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF065F46),
                                            selectedLabelColor = Color(0xFF6EE7B7)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { viewModel.generateWithCriteria() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_generate_with_criteria"),
                                enabled = !isGenerating,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(color = Color(0xFF003822), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Searching Seeds...", color = Color(0xFF003822), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF003822), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Find Matching Seed", color = Color(0xFF003822), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // MODE 2: RANDOM 64-BIT
                    if (selectedGeneratorMode == 2) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Cryptographically Random 64-Bit Java Seed",
                                color = Color(0xFFF0F6FC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Generates a completely unconstrained 64-bit signed integer using pseudo-random cryptographic entropy.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                            Button(
                                onClick = { viewModel.generateRandomSeed() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_generate_random_seed"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF003822), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Roll True 64-Bit Java Seed", color = Color(0xFF003822), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // MODE 3: MNEMONIC PHRASE SEED
                    if (selectedGeneratorMode == 3) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Mnemonic / Word Seed Generator",
                                color = Color(0xFFF0F6FC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Generates memorable thematic words (e.g. 'CHERRY-CALDERA-482') that hash cleanly via Java String.hashCode() to reproducible 64-bit worlds.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                            Button(
                                onClick = { viewModel.generateMnemonicWordSeed() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_generate_phrase_seed"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Roll Thematic Phrase Seed", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ==========================================
                    // ACTIVE SEED PREVIEW & INPUT FIELD
                    // ==========================================
                    Text(
                        text = "Current Active Seed:",
                        color = Color(0xFF8B949E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = seedInputText,
                        onValueChange = { viewModel.setSeedInputText(it) },
                        label = { Text("Active Seed (Numeric or Text)") },
                        placeholder = { Text("e.g. -4270425838048259167 or MyAdventure") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_enter_seed"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF30363D),
                            focusedTextColor = Color(0xFFF0F6FC),
                            unfocusedTextColor = Color(0xFFF0F6FC),
                            focusedLabelColor = Color(0xFF10B981),
                            unfocusedLabelColor = Color(0xFF8B949E)
                        ),
                        trailingIcon = {
                            if (seedInputText.isNotBlank()) {
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Minecraft Seed", seedInputText)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Seed copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Seed",
                                        tint = Color(0xFF22D3EE),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Technical Details Card: Java value, Bedrock parity & Hex
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F141C)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF30363D), Color(0xFF30363D))))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "64-bit Java Seed:",
                                    color = Color(0xFF8B949E),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "$currentSeed",
                                    color = Color(0xFFF0F6FC),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val parity = SeedGeneratorEngine.getBedrockParityInfo(currentSeed)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Bedrock Parity:",
                                    color = Color(0xFF8B949E),
                                    fontSize = 11.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (parity.isDirectParity) Color(0xFF065F46) else Color(0xFF1E293B),
                                                RoundedCornerShape(3.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = if (parity.isDirectParity) "100% Direct Match" else "Terrain Shared (${parity.bedrockSeed})",
                                            color = if (parity.isDirectParity) Color(0xFF6EE7B7) else Color(0xFF93C5FD),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Hexadecimal:",
                                    color = Color(0xFF8B949E),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "0x" + currentSeed.toULong().toString(16).uppercase(),
                                    color = Color(0xFF94A3B8),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 2: WORLD CONFIGURATION
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("world_config_section_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF30363D), Color(0xFF30363D))))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. World Configuration",
                        color = Color(0xFFF0F6FC),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Minecraft Version Selection (Mandatory 26.2 latest)
                    Text(
                        text = "Minecraft Java Version:",
                        color = Color(0xFF8B949E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = versionDropdownExpanded,
                        onExpandedChange = { versionDropdownExpanded = !versionDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = if (selectedVersion == "26.2") "Minecraft Java 26.2 — Latest" else "Minecraft Java $selectedVersion",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = versionDropdownExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_version_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = versionDropdownExpanded,
                            onDismissRequest = { versionDropdownExpanded = false },
                            modifier = Modifier.background(Color(0xFF161B22))
                        ) {
                            for (v in MinecraftVersion.ALL_VERSIONS) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = v.displayLabel,
                                            color = if (v.id == selectedVersion) Color(0xFF10B981) else Color(0xFFF0F6FC),
                                            fontWeight = if (v.id == selectedVersion) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.setSelectedVersion(v.id)
                                        versionDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mod Loader Display (Fabric ONLY)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mod Loader:",
                            color = Color(0xFF8B949E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF3B0764), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Fabric ONLY",
                                color = Color(0xFFE9D5FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // World Generation Mods Checklist
                    Text(
                        text = "Fabric World-Generation Mods (${selectedWorldGenMods.size} active):",
                        color = Color(0xFF8B949E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val worldGenMods = allMods.filter { it.loader.equals("Fabric", ignoreCase = true) }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (mod in worldGenMods) {
                            val isChecked = selectedWorldGenMods.contains(mod.name)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleModSelection(mod.name) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChecked) Color(0xFF1F2937) else Color(0xFF0F141C)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            if (isChecked) Color(0xFF10B981) else Color(0xFF30363D),
                                            if (isChecked) Color(0xFF10B981) else Color(0xFF30363D)
                                        )
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { viewModel.toggleModSelection(mod.name) },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = mod.name,
                                                color = Color(0xFFF0F6FC),
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF21262D), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = mod.category,
                                                    color = Color(0xFF8B949E),
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                        Text(
                                            text = mod.description,
                                            color = Color(0xFF8B949E),
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 3: COMPATIBILITY CHECK & WARNINGS
        // ==========================================
        if (compatibilityWarnings.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF451A03)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFEF4444))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Compatibility Warning",
                                color = Color(0xFFFCA5A5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        for (warning in compatibilityWarnings) {
                            Text(
                                text = "• $warning",
                                color = Color(0xFFFDE8E8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 4: SHOW SEED (WORLD MAP LAUNCH)
        // ==========================================
        item {
            Button(
                onClick = { viewModel.showSeed() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_show_seed"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = Color(0xFF003822),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "SHOW SEED (WORLD MAP)",
                    color = Color(0xFF003822),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun getArchetypeEmoji(archetype: SeedArchetype): String = when (archetype) {
    SeedArchetype.CHERRY_CALDERA -> "🌸"
    SeedArchetype.MUSHROOM_ISLAND -> "🍄"
    SeedArchetype.WOODLAND_MANSION -> "🏰"
    SeedArchetype.TRIAL_CHAMBER_SPAWN -> "⚔️"
    SeedArchetype.ANCIENT_CITY_CALDERA -> "👁️"
    SeedArchetype.SURVIVAL_ISLAND -> "🏝️"
    SeedArchetype.ALPINE_PEAKS -> "🏔️"
    SeedArchetype.ICE_SPIKES_VALLEY -> "❄️"
    SeedArchetype.LUSH_CAVE_MEGA -> "🌿"
    SeedArchetype.BADLANDS_CANYON -> "🏜️"
    SeedArchetype.VILLAGE_CROSSROADS -> "🏘️"
    SeedArchetype.MONUMENT_ARCHIPELAGO -> "🌊"
    SeedArchetype.MANSION_VILLAGE_COMBO -> "🏛️"
    SeedArchetype.PILLAGER_OUTPOST_SIEGE -> "🎯"
    SeedArchetype.JUNGLE_TEMPLE_COAST -> "🌴"
    SeedArchetype.DESERT_PYRAMID_OASIS -> "☀️"
    SeedArchetype.RANDOM_FORGE -> "🎲"
}
