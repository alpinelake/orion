package com.example.orion.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.orion.data.ItemCategory
import com.example.orion.ui.theme.AppTheme

@Composable
fun CategoryFilter(
    categories: Set<ItemCategory>,
    initialExpanded: Boolean = false,
    onSelect: (ItemCategory?) -> Unit
) {
    var expanded by remember {
        mutableStateOf(initialExpanded)
    }
    DropdownMenu(
        modifier = Modifier.width(IntrinsicSize.Min),
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        CategoryFilterContent(
            categories = categories,
            onSelect = {
                expanded = false
                onSelect(it)
            }
        )
    }
}

@Composable
fun CategoryFilterContent(
    categories: Set<ItemCategory>,
    onSelect: (ItemCategory?) -> Unit
) {
    Column {
        DropdownMenuItem(
            enabled = categories.isNotEmpty(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.NotInterested,
                    contentDescription = null,
                    modifier = Modifier
                )
            },
            text = {
                Row(Modifier.fillMaxWidth()) {
                    Text("Clear filter")
                }
            },
            onClick = {
                onSelect(null)
            }
        )
        HorizontalDivider()
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(option.text)
                        Spacer(Modifier.padding(horizontal = 10.dp))
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .alpha(if (categories.contains(option)) 1f else 0f)
                        )
                    }
                },
                onClick = {
                    onSelect(option)
                }
            )
        }
    }
}

@Preview
@Composable
fun CategoryFilterContentPreview() {
    AppTheme(darkTheme = true) {
        CategoryFilterContent(
            categories = ItemCategory.entries.toSet(),
            onSelect = {}
        )
    }
}
