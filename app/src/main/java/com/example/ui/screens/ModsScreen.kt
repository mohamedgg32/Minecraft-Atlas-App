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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.ModCategory
import com.example.data.model.ModLoader
import com.example.ui.components.ModCard
import com.example.ui.theme.FabricLoaderColor
import com.example.ui.viewmodel.ForgeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ModsScreen(
    viewModel: ForgeViewModel,
    modifier: Modifier = Modifier
) {
    val mods by viewModel.filteredMods.collectAsStateWithLifecycle()
    val modCategoryFilter by viewModel.modCategoryFilter.collectAsStateWithLifecycle()
    val modSearchQuery by viewModel.modSearchQuery.collectAsStateWithLifecycle()

    var showAddModDialog by remember { mutableStateOf(false) }

    // Dialog form state
    var newModName by remember { mutableStateOf("") }
    var newModVersion by remember { mutableStateOf("26.2") }
    var newModLoader by remember { mutableStateOf(ModLoader.FABRIC) }
    var newModCategory by remember { mutableStateOf(ModCategory.BIOME_MODS) }
    var newModDescription by remember { mutableStateOf("") }
    var newModFeatures by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val categories = remember {
        listOf("All") + ModCategory.entries.map { it.displayName }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0D1117),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    newModName = ""
                    newModVersion = "1.21.1"
                    newModLoader = ModLoader.FABRIC
                    newModCategory = ModCategory.BIOME_MODS
                    newModDescription = ""
                    newModFeatures = ""
                    showAddModDialog = true
                },
                containerColor = Color(0xFF10B981),
                contentColor = Color(0xFF003822),
                modifier = Modifier.testTag("add_mod_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add World Gen Mod")
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
                        text = "WORLDGEN MODS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF0F6FC),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${mods.size} terrain & biome mods (Fabric & Forge)",
                        fontSize = 12.sp,
                        color = Color(0xFF8B949E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = modSearchQuery,
                onValueChange = { viewModel.setModSearchQuery(it) },
                placeholder = { Text("Search mods by name, description, features...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF8B949E))
                },
                trailingIcon = {
                    if (modSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setModSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF8B949E))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mod_search_input"),
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

            // Fabric Only Badge indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B22), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF30363D), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target Loader: Fabric ONLY",
                    color = Color(0xFFC084FC),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Minecraft 26.2 Compatible",
                    color = Color(0xFF10B981),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Horizontal Scroll
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = modCategoryFilter == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF238636) else Color(0xFF161B22))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF3FB950) else Color(0xFF30363D),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { viewModel.setModCategoryFilter(cat) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else Color(0xFFC9D1D9),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mod List
            if (mods.isEmpty()) {
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
                            imageVector = Icons.Default.Extension,
                            contentDescription = null,
                            tint = Color(0xFF8B949E),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No mods found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF0F6FC)
                        )
                        Text(
                            text = "Tap the '+' button below to register a new world generation mod.",
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
                    items(mods, key = { it.id }) { mod ->
                        ModCard(
                            mod = mod,
                            onDelete = { viewModel.deleteMod(it) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add Mod Dialog (Requires Mod Name, Minecraft Version, Loader: Fabric or Forge, Category, Description, World-gen features)
    if (showAddModDialog) {
        AlertDialog(
            onDismissRequest = { showAddModDialog = false },
            title = {
                Text(
                    text = "Add World Generation Mod",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Mod Name
                    item {
                        OutlinedTextField(
                            value = newModName,
                            onValueChange = { newModName = it },
                            label = { Text("Mod Name *") },
                            placeholder = { Text("e.g. Terralith, Tectonic, Yung's...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_mod_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color(0xFFF0F6FC),
                                unfocusedTextColor = Color(0xFFF0F6FC)
                            )
                        )
                    }

                    // 2. Minecraft Version
                    item {
                        OutlinedTextField(
                            value = newModVersion,
                            onValueChange = { newModVersion = it },
                            label = { Text("Minecraft Version *") },
                            placeholder = { Text("e.g. 1.21.1, 1.20.1, 1.19.2") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_mod_version_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color(0xFFF0F6FC),
                                unfocusedTextColor = Color(0xFFF0F6FC)
                            )
                        )
                    }

                    // 3. Mod Loader: Fabric ONLY
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

                    // 4. Mod Category Dropdown
                    item {
                        ExposedDropdownMenuBox(
                            expanded = categoryDropdownExpanded,
                            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = newModCategory.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Mod Category *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("add_mod_category_selector"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF10B981),
                                    unfocusedBorderColor = Color(0xFF30363D),
                                    focusedTextColor = Color(0xFFF0F6FC),
                                    unfocusedTextColor = Color(0xFFF0F6FC)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = categoryDropdownExpanded,
                                onDismissRequest = { categoryDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF161B22))
                            ) {
                                ModCategory.entries.forEach { cat ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = cat.displayName,
                                                color = Color(0xFFF0F6FC),
                                                fontSize = 13.sp
                                            )
                                        },
                                        onClick = {
                                            newModCategory = cat
                                            categoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 5. Description
                    item {
                        OutlinedTextField(
                            value = newModDescription,
                            onValueChange = { newModDescription = it },
                            label = { Text("Description *") },
                            placeholder = { Text("What does this mod do?") },
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_mod_description_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color(0xFFF0F6FC),
                                unfocusedTextColor = Color(0xFFF0F6FC)
                            )
                        )
                    }

                    // 6. World-generation features
                    item {
                        OutlinedTextField(
                            value = newModFeatures,
                            onValueChange = { newModFeatures = it },
                            label = { Text("World-generation Features *") },
                            placeholder = { Text("e.g. 85+ biomes, yellowstone springs, custom noise, deep ravines") },
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_mod_features_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF30363D),
                                focusedTextColor = Color(0xFFF0F6FC),
                                unfocusedTextColor = Color(0xFFF0F6FC)
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newModName.isNotBlank() && newModDescription.isNotBlank()) {
                            viewModel.addMod(
                                name = newModName,
                                minecraftVersion = newModVersion,
                                category = newModCategory,
                                description = newModDescription,
                                worldGenFeatures = newModFeatures.ifBlank { "Procedural world-generation changes" }
                            )
                            showAddModDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    enabled = newModName.isNotBlank() && newModDescription.isNotBlank(),
                    modifier = Modifier.testTag("confirm_add_mod_btn")
                ) {
                    Text("Add Mod", color = Color(0xFF003822), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddModDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF161B22),
            titleContentColor = Color(0xFFF0F6FC),
            textContentColor = Color(0xFFC9D1D9)
        )
    }
}
