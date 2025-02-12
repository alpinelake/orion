package com.example.orion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.orion.DataImportState
import com.example.orion.data.FakeDataRepository
import com.example.orion.ui.theme.AppTheme

@Composable
fun ImportDialog(
    dataImportState: DataImportState.Confirm,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        ImportDialogContent(
            dataImportState = dataImportState,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun ImportDialogContent(
    dataImportState: DataImportState.Confirm,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val data = dataImportState.data
    val listState = rememberLazyListState()
    Card {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text("${data.size} items found")
//            }
            if (data.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 75.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("No items found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(data) { item ->
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = item.category.icon,
                                contentDescription = null,
                                modifier = Modifier,
                                tint = colorResource(item.category.colorId())
                            )
                            Text(item.name, modifier = Modifier)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { onDismiss() }) {
                            Text("Cancel")
                        }
                        Button(onClick = { onConfirm() }) {
                            Text("Import")
                        }
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun ImportDialogContentPreview() {
    val dataImportState = DataImportState.Confirm(
        data = FakeDataRepository.items + FakeDataRepository.items
    )
    AppTheme(darkTheme = true) {
        ImportDialogContent(
            dataImportState = dataImportState,
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun ImportDialogContentEmptyPreview() {
    val dataImportState = DataImportState.Confirm(
        data = emptyList()
    )
    AppTheme(darkTheme = true) {
        ImportDialogContent(
            dataImportState = dataImportState,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
