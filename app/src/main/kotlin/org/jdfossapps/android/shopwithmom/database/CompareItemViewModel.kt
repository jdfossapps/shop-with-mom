package org.jdfossapps.android.shopwithmom.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.lifecycle.MutableLiveData
import android.text.TextUtils
import androidx.lifecycle.Transformations

class CompareItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CompareItemRepository

    val allItems: LiveData<List<Compare>>
    private val searchStringLiveData = MutableLiveData<String>("")

    fun searchItemsChanged(query: String) {
        searchStringLiveData.value = query
    }

    init {
        val itemsDao = ItemRoomDatabase.getDatabase(application, viewModelScope).compareItemDao()
        repository = CompareItemRepository(itemsDao)
        allItems = repository.allCompareItems
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(item: Compare) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(item)
    }

    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun remove(item: Compare) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(item)
    }

    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun removeItems(items: List<Compare>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItems(items)
    }

    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun removeAllItems() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllItems()
    }

    /**
     * Launching a new coroutine to update the data in a non-blocking way
     */
    fun update(item: Compare) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(item)
    }

}