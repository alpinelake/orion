package com.example.orion

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orion.data.DataRepository
import com.example.orion.data.HomeItem
import com.example.orion.data.Item
import com.example.orion.data.ItemCategory
import com.example.orion.data.ItemState
import com.example.orion.data.Owner
import com.example.orion.data.SettingsDataStoreManager
import com.example.orion.data.toHomeItem
import com.example.orion.network.DataImporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.io.IOException
import javax.inject.Inject

data class UiState(
    val items: List<HomeItem>,
    val owners: Set<Owner>,
    val dataImportState: DataImportState
)

sealed interface DataImportState {
    data object None : DataImportState
    data class InputError(val exception: Throwable) : DataImportState
    data class Confirm(val data: List<Item>) : DataImportState
    data class DataExtractError(val exception: Throwable) : DataImportState
    data class DataLoadError(val data: List<Item>, val exception: Throwable) : DataImportState
}

data class FilterState(
    val categories: Set<ItemCategory> = emptySet(),
    val text: String? = null,
    val sort: ItemSort = ItemSort.Date,
    val hideArchived: Boolean = false
)

enum class ItemSort(val text: String) { Date("Recent"), Name("Title") }

const val DefaultName = "Default"

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: DataRepository,
    private val importer: DataImporter,
    private val settingsDataStoreManager: SettingsDataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(
        items = emptyList(),
        owners = emptySet(),
        dataImportState = DataImportState.None
    ))
    val uiState = _uiState.asStateFlow()
    private val _uiFilterState = MutableStateFlow(FilterState())
    val uiFilterState = _uiFilterState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStoreManager.defaultSort.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ItemSort.Date.name
            ).collect { sort ->
                _uiFilterState.update {
                    it.copy(sort = ItemSort.valueOf(sort))
                }
            }
        }
        viewModelScope.launch {
            settingsDataStoreManager.hideArchived.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = false
            ).collect { hide ->
                _uiFilterState.update {
                    it.copy(hideArchived = hide)
                }
            }
        }
        viewModelScope.launch {
            combine(uiFilterState, repository.getOwners(), repository.getItems()) { filter, owners, items ->
                if (owners.isEmpty()) {
                    repository.insert(Owner(ownerId = 0, name = DefaultName))
                }
                val filteredItems = if (filter.categories.isNotEmpty()) {
                    items.filter { filter.categories.contains(it.category) }
                } else if (!filter.text.isNullOrBlank()) {
                    items.filter {
                        val tokens = filter.text.split(" ")
                        tokens.all { token ->
                            it.name.contains(token, ignoreCase = true) ||
                                    it.note?.contains(token, ignoreCase = true) == true
                        }
                    }
                } else {
                    items
                }.let {
                    if (filter.hideArchived) {
                        items.filterNot { it.state == ItemState.Done }
                    } else items
                }
                Pair(
                    owners,
                    when (filter.sort) {
                        ItemSort.Date -> filteredItems.sortedBy { it.modified }.reversed()
                        ItemSort.Name -> filteredItems.sortedBy { it.name }
                    }
                )
            }.collect { data ->
                val owners = data.first
                val items = data.second
                _uiState.update {
                    it.copy(
                        owners = owners.toSet(),
                        items = items.map { item ->
                            item.toHomeItem()
                        },
                    )
                }
            }
        }
    }

    fun setSortPreference(sort: ItemSort) {
        viewModelScope.launch {
            settingsDataStoreManager.setDefaultSort(sort.name)
        }
    }

    fun setHideArchived(hideArchived: Boolean) {
        viewModelScope.launch {
            settingsDataStoreManager.setHideArchived(hideArchived)
        }
    }

    fun setFilterText(text: String?) {
        _uiFilterState.update {
            it.copy(text = text, categories = emptySet())
        }
    }

    fun toggleFilterCategory(category: ItemCategory?) {
        _uiFilterState.update {
            val cats = it.categories.toMutableSet()
            it.copy(
                categories = if (category == null) emptySet()
                else if (cats.contains(category)) cats.minus(category)
                else cats.plus(category),
                text = null
            )
        }
    }

    fun insertOrUpdate(item: HomeItem, modified: Boolean = true) {
        viewModelScope.launch {
//            if (owner != null) {
//                if (owner.ownerId == 0) {
//                    repository.insert(owner)
//                } else {
//                    repository.update(owner)
//                }
//            }
            if (item.id == 0) {
                repository.insert(item.toItem().copy(
                    created = Clock.System.now().epochSeconds
                ))
            } else {
                repository.update(item.toItem().let {
                    if (modified) it.copy(modified = Clock.System.now().epochSeconds)
                    else it
                })
            }
        }
    }

    fun delete(item: HomeItem) {
        viewModelScope.launch {
            repository.delete(item.toItem())
        }
    }

    fun delete(owner: Owner) {
        viewModelScope.launch {
            repository.delete(owner)
        }
    }

    fun extractImport(uri: Uri) {
        viewModelScope.launch {
            val result = try {
                val data = importer.import(uri)
                DataImportState.Confirm(data = data)
            } catch (e: IOException) {
                Log.e("TAG", "extract import error", e)
                DataImportState.InputError(e)
            } catch (e: Exception) {
                Log.e("TAG", "extract import error", e)
                DataImportState.DataExtractError(e)
            }
            _uiState.update {
                it.copy(
                    dataImportState = result
                )
            }
        }
    }

    fun loadImport(mergeOwnerId: Int?, mergeOwner: String?) {
        viewModelScope.launch {
            val dataImportState = _uiState.value.dataImportState
            if (dataImportState is DataImportState.Confirm) {
                val result = try {
                    val owner = if (mergeOwner != null) {
                        // use the provided merge owner if desired
                        repository.insertReturnResult(Owner(ownerId = 0, name = mergeOwner))
                    } else {
                        // merge using provided id or default owner if none
                        uiState.value.owners.first {
                            it.ownerId == (mergeOwnerId ?: 1)
                        }
                    }
                    dataImportState.data.forEach { item ->
                        repository.insert(
                            item.copy(id = 0, itemCreatorId = owner.ownerId)
                        )
                    }
                    DataImportState.None
                } catch (e: Exception) {
                    Log.e("TAG", "load import error", e)
                    DataImportState.DataLoadError(dataImportState.data, e)
                }
                _uiState.update {
                    it.copy(dataImportState = result)
                }
            }
        }
    }

    fun doneImport() {
        _uiState.update {
            it.copy(dataImportState = DataImportState.None)
        }
    }
}
