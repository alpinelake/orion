package com.example.orion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import com.example.orion.ui.theme.AppTheme

@Composable
fun NavDrawerSheet(
    onExport: () -> Unit,
    onImport: () -> Unit,
    hideArchived: Boolean,
    onToggleHideArchived: () -> Unit
) {
    ModalDrawerSheet {
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Download, contentDescription = null) },
            label = {
                Text(text = "Export database")
            },
            selected = false,
            onClick = {
                onExport()
            }
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.FileOpen, contentDescription = null) },
            label = {
                Text(text = "Import database")
            },
            selected = false,
            onClick = {
                onImport()
            }
        )
        NavigationDrawerItem(
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Show archived items")
                    Switch(
                        checked = !hideArchived,
                        onCheckedChange = { onToggleHideArchived() },
                        modifier = Modifier.scale(.75f)
                    )
                }
            },
            selected = false,
            onClick = {
                onToggleHideArchived()
            }
        )
    }
}

@Preview
@Composable
fun NavDrawerSheetPreview() {
    AppTheme(darkTheme = true) {
        NavDrawerSheet(
            onExport = {},
            onImport = {},
            hideArchived = false,
            onToggleHideArchived = {}
        )
    }
}
