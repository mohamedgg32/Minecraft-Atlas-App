package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ModEntity
import com.example.ui.components.ConfigCard
import com.example.ui.theme.FabricLoaderColor
import com.example.ui.viewmodel.ForgeViewModel

@Composable
fun ConfigsScreen(
    viewModel: ForgeViewModel,
    modifier: Modifier = Modifier
) {
    val configs by viewModel.allConfigs.collectAsStateWithLifecycle()
    val allMods by viewModel.allModsRaw.collectAsStateWithLifecycle()
    val selectedConfig by viewModel.selectedConfig.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }

    // Dialog state
    var newConfigName by remember { mutableStateOf("") }
    var newConfigVersion by remember { mutableStateOf("26.2") }
    val newConfigLoader = "Fabric"
    var newConfigDesc by remember { mutableStateOf("") }
    val selectedModIds = remember { mutableStateListOf<Long>() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0D1117),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    newConfigName = ""
                    newConfigVersion = "26.2"
                    newConfigDesc = ""
                    selectedModIds.clear()
                    showCreateDialog = true
                },
                containerColor = Color(0xFF10B981),
                contentColor = Color(0xFF003822),
                modifier = Modifier.testTag("new_config_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Worldgen Preset")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WORLDGEN PRESETS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF0F6FC),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Bundle Fabric & Forge mods into generation configurations",
                        fontSize = 12.sp,
                        color = Color(0xFF8B949E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (configs.isEmpty()) {
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
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = Color(0xFF8B949E),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No configurations found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF0F6FC)
                        )
                        Text(
                            text = "Create a preset by grouping your favorite world generation mods.",
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
                    items(configs, key = { it.id }) { config ->
                        ConfigCard(
                            config = config,
                            allMods = allMods,
                            isSelected = selectedConfig?.id == config.id,
                            onSelectForForge = {
                                if (selectedConfig?.id == config.id) {
                                    viewModel.selectConfig(null)
                                } else {
                                    viewModel.selectConfig(config)
                                    viewModel.setTab(0) // Switch to Forge
                                }
                            },
                            onDelete = { viewModel.deleteWorldConfig(it) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Create Worldgen Configuration Dialog
    if (showCreateDialog) {
        val availableModsForLoader = allMods.filter { it.loader.equals(newConfigLoader, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Create Worldgen Preset",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedTextField(
                            value = newConfigName,
                            onValueChange = { newConfigName = it },
                            label = { Text("Preset Name *") },
                            placeholder = { Text("e.g. Ultra Fantasy Overworld") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("config_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color(0xFFF0F6FC),
                                unfocusedTextColor = Color(0xFFF0F6FC)
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newConfigVersion,
                            onValueChange = { newConfigVersion = it },
                            label = { Text("Minecraft Version *") },
                            placeholder = { Text("e.g. 1.21.1, 1.20.1") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color(0xFFF0F6FC),
                                unfocusedTextColor = Color(0xFFF0F6FC)
                            )
                        )
                    }

                    item {
                        Text(
                            text = "Mod Loader:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8B949E)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3B0764))
                                .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Fabric ONLY",
                                color = Color(0xFFE9D5FF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = newConfigDesc,
                            onValueChange = { newConfigDesc = it },
                            label = { Text("Description") },
                            placeholder = { Text("What generation style does this preset achieve?") },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color(0xFFF0F6FC),
                                unfocusedTextColor = Color(0xFFF0F6FC)
                            )
                        )
                    }

                    item {
                        Text(
                            text = "Select Installed $newConfigLoader Mods (${selectedModIds.size} selected):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }

                    if (availableModsForLoader.isEmpty()) {
                        item {
                            Text(
                                text = "No installed $newConfigLoader mods found. You can add them in the Mods tab.",
                                fontSize = 12.sp,
                                color = Color(0xFF8B949E)
                            )
                        }
                    } else {
                        items(availableModsForLoader, key = { it.id }) { mod ->
                            val isChecked = selectedModIds.contains(mod.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChecked) Color(0xFF161B22) else Color(0xFF0D1117))
                                    .clickable {
                                        if (isChecked) selectedModIds.remove(mod.id) else selectedModIds.add(mod.id)
                                    }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        if (it) selectedModIds.add(mod.id) else selectedModIds.remove(mod.id)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF10B981)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = mod.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFF0F6FC)
                                    )
                                    Text(
                                        text = "${mod.category} • ${mod.minecraftVersion}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF8B949E)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newConfigName.isNotBlank()) {
                            viewModel.createWorldConfig(
                                name = newConfigName,
                                minecraftVersion = newConfigVersion,
                                selectedModIds = selectedModIds.toList(),
                                description = newConfigDesc.ifBlank { "Custom Fabric worldgen bundle" }
                            )
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    enabled = newConfigName.isNotBlank(),
                    modifier = Modifier.testTag("confirm_create_config_btn")
                ) {
                    Text("Create Preset", color = Color(0xFF003822), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF161B22),
            titleContentColor = Color(0xFFF0F6FC),
            textContentColor = Color(0xFFC9D1D9)
        )
    }
}
