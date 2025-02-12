package com.example.orion.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

private fun formatDateTime(time: LocalDateTime): String {
    val format = LocalDateTime.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
    }
    return format.format(time)
}

data class HomeItem(
    val id: Int,
    val itemCreatorId: Int,
    val name: String,
    val note: String? = null,
    val state: ItemState = ItemState.Default,
    val category: ItemCategory = ItemCategory.None,
    val created: Instant = Clock.System.now(),
    val modified: Instant = Clock.System.now()
) {
    fun toItem(): Item {
        return Item(
            id = id,
            itemCreatorId = itemCreatorId,
            name = name,
            note = note,
            state = state,
            category = category,
            created = created.epochSeconds,
            modified = modified.epochSeconds
        )
    }

    fun date(): String {
        return formatDateTime(modified.toLocalDateTime(TimeZone.UTC))
    }
}

fun Item.toHomeItem(): HomeItem {
    return HomeItem(
        id = id,
        itemCreatorId = itemCreatorId,
        name = name,
        note = note,
        state = state,
        category = category,
        created = created.let { Instant.fromEpochSeconds(it) },
        modified = modified.let { Instant.fromEpochSeconds(it) }
    )
}
