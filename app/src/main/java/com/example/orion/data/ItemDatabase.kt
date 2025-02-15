package com.example.orion.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import com.example.orion.R
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

enum class ItemState(val text: String) {
    Default("Default"),
    Pinned("Pinned"),
    Done("Done")
}

enum class ItemCategory(val text: String, val icon: ImageVector, val order: Int) {
    None("Other", Icons.Outlined.Circle, 5),
    TV("TV", Icons.Default.Tv, 1),
    Movie("Movie", Icons.Outlined.Movie, 2),
    Book("Book", Icons.Outlined.BookmarkBorder, 3),
    Game("Game", Icons.Default.Games, 4);

    fun colorId(): Int {
        return when (this) {
            None -> R.color.pastel_orange
            TV -> R.color.pastel_blue
            Movie -> R.color.pastel_red
            Game -> R.color.pastel_yellow
            Book -> R.color.pastel_green
        }
    }
}

@Entity(tableName = "owner")
data class Owner(
    @PrimaryKey(autoGenerate = true) val ownerId: Int,
    val name: String
) {
    fun isDefault() = ownerId == 1
}

@Entity(
    tableName = "item",
    foreignKeys = [
        ForeignKey(
            entity = Owner::class,
            parentColumns = ["ownerId"],
            childColumns = ["itemCreatorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("itemCreatorId")
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val itemCreatorId: Int,
    val name: String,
    val note: String? = null,
    val state: ItemState = ItemState.Default,
    val category: ItemCategory = ItemCategory.None,
    val created: Long = Clock.System.now().epochSeconds,
    val modified: Long = Clock.System.now().epochSeconds
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getItems(): Flow<List<Item>>
    @Query("SELECT * FROM item")
    fun getItemsFlat(): List<Item>
    @Query("SELECT * FROM owner")
    fun getOwners(): Flow<List<Owner>>
    @Insert
    suspend fun insert(item: Item): Long
    @Update
    suspend fun update(item: Item)
    @Delete
    suspend fun delete(item: Item)
    @Insert
    suspend fun insert(owner: Owner): Long
    @Update
    suspend fun update(owner: Owner)
    @Delete
    suspend fun delete(owner: Owner)

    @Query("SELECT * FROM owner WHERE ownerId = :id")
    suspend fun get(id: Long): Owner

    @Transaction
    suspend fun insertReturnResult(owner: Owner) : Owner {
        val id = insert(owner)
        return get(id)
    }
}

@Database(
    entities = [Owner::class, Item::class],
    version = 1
)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
