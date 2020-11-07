package org.jdfossapps.android.shopwithmom.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update


@Dao
interface ItemDao {

    @Query("SELECT * from item_table ORDER BY id ASC")
    fun getAlphabetizedItems(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Item)

    @Query("DELETE FROM item_table")
    fun deleteAll()

    // SQLite is case insensitive
    @Query("SELECT * from item_table where name like '%'||:query||'%' " +
        "or description like '%'||:query||'%' ORDER BY id ASC")
    fun getItemsFromSearchBar(query: String?): LiveData<List<Item>>

    @Delete
    fun delete(item: Item)

    @Delete
    fun deleteItems(items: List<Item>)

    @Update
    fun update(item: Item)
}
