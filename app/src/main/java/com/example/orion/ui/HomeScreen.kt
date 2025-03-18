package com.example.orion.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.orion.AppViewModel
import com.example.orion.DataImportState
import com.example.orion.R
import com.example.orion.UiState
import com.example.orion.data.FakeDataRepository
import com.example.orion.data.HomeItem
import com.example.orion.data.Item
import com.example.orion.data.ItemCategory
import com.example.orion.data.ItemState
import com.example.orion.data.SettingsDataStoreManager
import com.example.orion.network.DataImporter
import com.example.orion.ui.component.ItemSortMenu
import com.example.orion.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    exportData: () -> Unit,
    importData: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiFilterState by viewModel.uiFilterState.collectAsStateWithLifecycle()
    val query = uiFilterState.text
    val categories = uiFilterState.categories
    var itemForEdit: HomeItem? by remember { mutableStateOf(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        focusManager.clearFocus()
        if (drawerState.isOpen) {
            coroutineScope.launch {
                drawerState.close()
            }
        } else if (categories.isNotEmpty()) {
            viewModel.toggleFilterCategory(null)
        } else if (query != null) {
            viewModel.setFilterText(null)
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawerSheet(
                onExport = {
                    exportData()
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                onImport = {
                    importData()
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                hideArchived = uiFilterState.hideArchived,
                onToggleHideArchived = {
                    viewModel.setHideArchived(!uiFilterState.hideArchived)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                val placeholder = if (categories.isEmpty()) "Search"
                else categories.joinToString(", ") { it.text }
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    colors = SearchBarDefaults.colors(
                        //containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(15.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            onSearch = {
                                focusManager.clearFocus()
                            },
                            expanded = false,
                            onExpandedChange = {},
                            onQueryChange = {
                                viewModel.setFilterText(it)
                            },
                            query = query ?: "",
                            placeholder = {
                                Text(placeholder)
                            },
                            leadingIcon = {
//                                Icon(Icons.Default.Search, contentDescription = null)
//                                Icon(
//                                    painter = painterResource(R.drawable.icon_grid),
//                                    contentDescription = null,
//                                    tint = Color.Unspecified,
//                                    modifier = Modifier.clickable {
//                                        coroutineScope.launch {
//                                            if (drawerState.isOpen) drawerState.close()
//                                            else drawerState.open()
//                                        }
//                                    }
//                                )
                                Icon(
                                    painter = painterResource(R.drawable.bow_arrow),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable {
                                            coroutineScope.launch {
                                                if (drawerState.isOpen) drawerState.close()
                                                else drawerState.open()
                                            }
                                        }
                                )
                            },
                            trailingIcon = {
                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Sort,
                                        contentDescription = null,
                                        modifier = Modifier.clickable {
                                            expanded = !expanded
                                        }
                                    )
                                    ItemSortMenu(
                                        expanded = expanded,
                                        selected = uiFilterState.sort,
                                        onSelect = {
                                            viewModel.setSortPreference(it)
                                            expanded = !expanded
                                        },
                                        onDismiss = {
                                            expanded = !expanded
                                        }
                                    )
                                }
                            },
                            colors = SearchBarDefaults.inputFieldColors()
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    content = {}
                )
            },
            floatingActionButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            itemForEdit = HomeItem(
                                id = 0,
                                itemCreatorId = 1,
                                name = "",
                                state = ItemState.Default,
                                category = ItemCategory.TV
                            )
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                HomeScreenContent(
                    uiState = uiState,
                    onHoldItem = { itemForEdit = it },
                    onFilter = viewModel::toggleFilterCategory,
                    resetAutoScroll = viewModel::resetLastId
                )
                itemForEdit?.let { item ->
                    ItemDialog(
                        uiState = uiState,
                        item = item,
                        onDismissRequest = { itemForEdit = null },
                        onSaveItem = { updated ->
                            if (updated.name.isNotBlank()) {
                                viewModel.insertOrUpdate(updated, modified = true)
                                itemForEdit = null
                            }
                        },
                        onDeleteItem = {
                            itemForEdit = null
                            viewModel.delete(item)
                        },
                        hintCategories = uiState.items.isEmpty()
                    )
                }
                when (uiState.dataImportState) {
                    DataImportState.None -> {}
                    is DataImportState.Confirm -> {
                        ImportDialog(
                            dataImportState = uiState.dataImportState as DataImportState.Confirm,
                            onConfirm = {
                                viewModel.loadImport(null, null)
                            },
                            onDismiss = {
                                viewModel.doneImport()
                            }
                        )
                    }
                    is DataImportState.DataExtractError,
                    is DataImportState.DataLoadError,
                    is DataImportState.InputError -> {
                        AlertDialog(
                            onDismissRequest = { viewModel.doneImport() },
                            text = { Text(uiState.dataImportState.javaClass.simpleName) },
                            confirmButton = {
                                TextButton(onClick = { viewModel.doneImport() }) {
                                    Text("Confirm")
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onHoldItem: (HomeItem) -> Unit,
    onFilter: (ItemCategory) -> Unit,
    resetAutoScroll: () -> Unit
) {
    val sorted = uiState.items.sortedByDescending { it.state == ItemState.Pinned }
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        state = listState
    ) {
        items(
            items = sorted,
            key = { item ->
                "${item.id}${item.modified}${item.state}"
            }
        ) { item ->
            val owner = uiState.owners.first { it.ownerId == item.itemCreatorId }
            ItemCard(
                item = item,
                owner = owner,
                isExpanded = false,
                onHoldItem = { onHoldItem(item) },
                onFilter = onFilter
            )
        }
    }

    LaunchedEffect(key1 = uiState.lastId, key2 = uiState.items, block = {
        if (uiState.lastId != 0L) {
            val i = sorted.indexOfFirst { it.id.toLong() == uiState.lastId }
            if (i >= 0) {
                listState.animateScrollToItem(index = i)
            }
            delay(1000)
            resetAutoScroll()
        }
    })
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val repository = FakeDataRepository()
    AppTheme(darkTheme = true) {
        HomeScreen(
            viewModel = AppViewModel(
                repository = repository,
                importer = object : DataImporter {
                    override suspend fun import(uri: Uri): List<Item> {
                        TODO("Not yet implemented")
                    }
                },
                settingsDataStoreManager = SettingsDataStoreManager(LocalContext.current)
            ),
            exportData = {},
            importData = {}
        )
    }
}
