package com.example.orion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.orion.DefaultName
import com.example.orion.UiState
import com.example.orion.data.FakeDataRepository
import com.example.orion.data.HomeItem
import com.example.orion.data.ItemCategory
import com.example.orion.data.ItemState
import com.example.orion.data.Owner
import com.example.orion.data.toHomeItem
import com.example.orion.ui.theme.AppTheme
import kotlinx.coroutines.delay

@Composable
fun ItemDialog(
    uiState: UiState,
    item: HomeItem,
    onDismissRequest: () -> Unit,
    onSaveItem: (HomeItem) -> Unit,
    onDeleteItem: () -> Unit,
    hintCategories: Boolean = false
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        ItemDialogContent(
            item = item,
            owner = uiState.owners.firstOrNull { it.ownerId == item.itemCreatorId },
            onSave = onSaveItem,
            onDismissRequest = onDismissRequest,
            onDelete = onDeleteItem,
            hintCategories = hintCategories
        )
    }
}

@Composable
private fun ItemDialogContent(
    item: HomeItem,
    owner: Owner?,
    onSave: (HomeItem) -> Unit,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    hintCategories: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    var name by remember { mutableStateOf(item.name) }
    var note by remember { mutableStateOf(item.note) }
    var state by remember { mutableStateOf(item.state) }
    var expand by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf(item.category) }
    LaunchedEffect(Unit) {
        if (hintCategories) {
            delay(200)
            expand = true
        }
    }
    val save = {
        onSave(item.copy(
            name = name.trim(),
            note = note?.trim()?.ifEmpty { null },
            state = state,
            category = category
        ))
    }
    val color = colorResource(category.colorId())
    Card {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(enabled = item.id != 0) {
                            onDelete()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Delete",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = if (state == ItemState.Done) color
                        else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            state = if (state == ItemState.Done) ItemState.Default
                            else ItemState.Done
                            save()
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = if (state == ItemState.Pinned) color
                        else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            state = if (state == ItemState.Pinned) ItemState.Default
                            else ItemState.Pinned
                            save()
                        }
                    )
                }
            }
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    label = {
                        Text("Title")
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.clickable { expand = !expand }
                            )
                            DropdownMenu(
                                expanded = expand,
                                onDismissRequest = { expand = false }
                            ) {
                                ItemCategory.entries.sortedBy { it.order }.forEach { option ->
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = option.icon,
                                                contentDescription = null,
                                                tint = colorResource(option.colorId()),
                                                modifier = Modifier
                                            )
                                        },
                                        text = {
                                            Text(option.text)
                                        },
                                        onClick = {
                                            category = option
                                            expand = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    value = name,
                    onValueChange = { name = it.replace("\n", "") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { save() }),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                )
            }
            Row(
                modifier = Modifier
            ) {
                OutlinedTextField(
                    label = { Text("Note") },
                    value = note ?: "",
                    onValueChange = { note = it },
                    maxLines = 7,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(if (owner == null || owner.isDefault()) 0f else .5f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                    )
                    Text(owner?.name ?: DefaultName)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = { onDismissRequest() }) {
                        Text("Cancel")
                    }
                    Button(onClick = { save() }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ItemDialogContentPreview() {
    val item = FakeDataRepository.items[3].toHomeItem()
    val owner = FakeDataRepository.owners.first { it.ownerId == item.itemCreatorId }
    AppTheme(darkTheme = true) {
        ItemDialogContent(
            item = item,
            owner = owner,
            onSave = {},
            onDismissRequest = {},
            onDelete = {}
        )
    }
}

@Preview
@Composable
private fun ItemDialogContentFriendPreview() {
    val item = FakeDataRepository.items.first { it.itemCreatorId == 2 }.toHomeItem()
    val owner = FakeDataRepository.owners.first { it.ownerId == item.itemCreatorId }
    AppTheme(darkTheme = true) {
        ItemDialogContent(
            item = item,
            owner = owner,
            onSave = {},
            onDismissRequest = {},
            onDelete = {}
        )
    }
}
