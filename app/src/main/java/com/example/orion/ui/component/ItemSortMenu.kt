package com.example.orion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.orion.ItemSort
import com.example.orion.ui.theme.AppTheme

@Composable
fun ItemSortMenu(
    expanded: Boolean = false,
    selected: ItemSort,
    onSelect: (ItemSort) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier.width(IntrinsicSize.Min),
        expanded = expanded,
        onDismissRequest = { onDismiss() }
    ) {
        ItemSortContent(
            selected = selected,
            onSelect = onSelect
        )
    }
}

@Composable
fun ItemSortContent(
    selected: ItemSort,
    onSelect: (ItemSort) -> Unit
) {
    Column {
        ItemSort.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Text(option.text)
                },
                onClick = {
                    onSelect(option)
                },
                leadingIcon = {
                    Icon(
                        if (selected == option) Icons.Default.RadioButtonChecked
                        else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null
                    )
                },
                colors = MenuDefaults.itemColors(
                    leadingIconColor = if (selected == option) {
                        MaterialTheme.colorScheme.primary
                    } else Color.Unspecified
                )
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ItemSortContentPreview() {
    AppTheme {
        ItemSortContent(
            selected = ItemSort.Name,
            onSelect = {}
        )
    }
}
