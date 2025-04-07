package com.example.orion.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.orion.ItemAction
import com.example.orion.data.FakeDataRepository
import com.example.orion.data.HomeItem
import com.example.orion.data.ItemState
import com.example.orion.data.toHomeItem
import com.example.orion.ui.theme.AppTheme

@Composable
fun ItemActionMenu(
    item: HomeItem,
    expanded: Boolean,
    onSelect: (ItemAction) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier.width(IntrinsicSize.Min),
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        ItemActionContent(
            item = item,
            onSelect = onSelect
        )
    }
}

@Composable
fun ItemActionContent(
    item: HomeItem,
    onSelect: (ItemAction) -> Unit
) {
    Column {
        ItemAction.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    val text = when (option) {
                        ItemAction.Archive -> {
                            if (item.state == ItemState.Done) "Unarchive" else "Archive"
                        }
                        ItemAction.Pin -> {
                            if (item.state == ItemState.Pinned) "Unpin" else "Pin"
                        }
                        ItemAction.Delete -> {
                            "Delete"
                        }
                    }
                    Text(text)
                },
                leadingIcon = {
                    when (option) {
                        ItemAction.Pin -> {
                            Icon(Icons.Default.PushPin, contentDescription = null)
                        }
                        ItemAction.Archive -> {
                            Icon(Icons.Default.TaskAlt, contentDescription = null)
                        }
                        ItemAction.Delete -> {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null)
                        }
                    }
                },
                onClick = {
                    onSelect(option)
                }
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ItemActionContentPreview() {
    AppTheme {
        ItemActionContent(
            item = FakeDataRepository.items.first().toHomeItem(),
            onSelect = {}
        )
    }
}
