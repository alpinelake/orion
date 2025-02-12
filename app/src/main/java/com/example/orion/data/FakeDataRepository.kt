package com.example.orion.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

class FakeDataRepository : DataRepository {

    companion object {
        private var itemId = 0
        val owners = listOf(
            Owner(
                ownerId = 1,
                name = "Default"
            ),
            Owner(
                ownerId = 2,
                name = "Jules"
            )
        )
        private val defaultOwner = owners.first()
        val items = listOf(
            Item(
                id = ++itemId,
                itemCreatorId = defaultOwner.ownerId,
                name = "Book title 1 that is long enough to wrap in the item dialog box.",
                note = """
                            Test note for title 1 that is long enough to
                            extend over multiple lines beyond the note displayed
                            by default on the home screen or elsewhere.
                            Continues onward at least 4 lines of text on the screen.""".trimIndent(),
                state = ItemState.Pinned,
                category = ItemCategory.Book,
                created = Clock.System.now().minus(5.days).epochSeconds,
                modified = Clock.System.now().minus(1.days).epochSeconds,
            ),
            Item(
                id = ++itemId,
                itemCreatorId = defaultOwner.ownerId,
                name = "Movie title 1",
                state = ItemState.Default,
                category = ItemCategory.Movie
            ),
            Item(
                id = ++itemId,
                itemCreatorId = defaultOwner.ownerId,
                name = "Movie title 2",
                state = ItemState.Done,
                category = ItemCategory.Movie
            ),
            Item(
                id = ++itemId,
                itemCreatorId = defaultOwner.ownerId,
                name = "Game title 1",
                note = """
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                    """.trimIndent(),
                category = ItemCategory.Game
            ),
            Item(
                id = ++itemId,
                itemCreatorId = defaultOwner.ownerId,
                name = "Uncategorized title 1"
            ),
            Item(
                id = ++itemId,
                itemCreatorId = owners[1].ownerId,
                name = "TV title 1. Expand this card to display alternate item creator id.",
                category = ItemCategory.TV
            ),
        )
    }

    override fun getItems(): Flow<List<Item>> {
        return flow {
            emit(items)
        }
    }

    override fun getOwners(): Flow<List<Owner>> {
        return flow {
            emit(owners)
        }
    }
    override suspend fun insert(item: Item) {}
    override suspend fun update(item: Item) {}
    override suspend fun delete(item: Item) {}
    override suspend fun insert(owner: Owner): Long {
        TODO("Not yet implemented")
    }
    override suspend fun update(owner: Owner) {}
    override suspend fun delete(owner: Owner) {}
    override suspend fun get(id: Long): Owner {
        TODO("Not yet implemented")
    }
    override suspend fun insertReturnResult(owner: Owner): Owner {
        TODO("Not yet implemented")
    }
}
