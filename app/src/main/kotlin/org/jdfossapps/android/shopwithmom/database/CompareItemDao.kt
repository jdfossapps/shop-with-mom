package org.jdfossapps.android.shopwithmom.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update


@Dao
interface CompareItemDao {

    @Query("SELECT * from compare_item_table ORDER BY price_per_unit ASC")
    fun getAlphabetizedItems(): LiveData<List<Compare>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Compare)

    @Query("DELETE FROM compare_item_table")
    fun deleteAll()

    // SQLite is case insensitive
    @Delete
    fun delete(item: Compare)

    @Delete
    fun deleteItems(items: List<Compare>)

    @Update
    fun update(item: Compare)
}
