package com.example.orion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orion.data.FakeDataRepository
import com.example.orion.data.HomeItem
import com.example.orion.data.ItemCategory
import com.example.orion.data.ItemState
import com.example.orion.data.Owner
import com.example.orion.data.toHomeItem
import com.example.orion.ui.theme.AppTheme

@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    item: HomeItem,
    owner: Owner,
    isExpanded: Boolean = false,
    onHoldItem: () -> Unit,
    onFilter: (ItemCategory) -> Unit
) {
    val highlight = colorResource(item.category.colorId())
    var expanded by remember { mutableStateOf(isExpanded) }
    Card(
        modifier = modifier
            .alpha(if (item.state == ItemState.Done) .5f else 1f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        expanded = !expanded
                    },
                    onLongClick = {
                        onHoldItem()
                    })
                .padding(10.dp)
                .height(IntrinsicSize.Min),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = item.category.icon,
                    contentDescription = null,
                    tint = highlight,
                    modifier = Modifier.clickable {
                        onFilter(item.category)
                    }
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (item.state == ItemState.Pinned) {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
            }

            if (!item.note.isNullOrEmpty()) {
                Text(
                    text = item.note,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (expanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.alpha(if (owner.isDefault()) 0f else .5f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                        )
                        Text(owner.name, fontSize = 15.sp)
                    }
                    Text(
                        text = item.date(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(.5f)
                    )
                }
            }
        }
    }
}

private class ItemProvider: PreviewParameterProvider<HomeItem> {
    override val values: Sequence<HomeItem> =
        FakeDataRepository.items.map { item ->
            item.toHomeItem()
        }.asSequence()
}

@Preview(showBackground = true)
@Composable
private fun ItemCardPreview(
    @PreviewParameter(ItemProvider::class) item: HomeItem
) {
    val owner = FakeDataRepository.owners.first { it.ownerId == item.itemCreatorId }

    AppTheme(darkTheme = true) {
        ItemCard(
            item = item,
            owner = owner,
            onHoldItem = {},
            isExpanded = item.id == 1 || item.id == 5,
            onFilter = {}
        )
    }
}
