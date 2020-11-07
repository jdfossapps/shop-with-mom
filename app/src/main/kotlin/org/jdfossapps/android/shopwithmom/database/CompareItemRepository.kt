package org.jdfossapps.android.shopwithmom.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class CompareItemRepository(private val compareItemDao: CompareItemDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allCompareItems: LiveData<List<Compare>> = compareItemDao.getAlphabetizedItems()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Compare) {
        compareItemDao.insert(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(item: Compare) {
        compareItemDao.delete(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteItems(items: List<Compare>) {
        compareItemDao.deleteItems(items)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllItems() {
        compareItemDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(item: Compare) {
        compareItemDao.update(item)
    }

}
