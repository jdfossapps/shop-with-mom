package org.jdfossapps.android.shopwithmom.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: LiveData<List<Item>> = itemDao.getAlphabetizedItems()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Item) {
        itemDao.insert(item)
    }

    fun itemsFromSearchBar(query: String): LiveData<List<Item>> {
        return itemDao.getItemsFromSearchBar(query)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(item: Item) {
        itemDao.delete(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteItems(items: List<Item>) {
        itemDao.deleteItems(items)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(item: Item) {
        itemDao.update(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllItems() {
        itemDao.deleteAll()
    }

}
