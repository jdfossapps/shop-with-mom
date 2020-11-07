package org.jdfossapps.android.shopwithmom.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.util.Date
import androidx.room.TypeConverters

@TypeConverters(DateConverters::class)
@Database(entities = arrayOf(Item::class, Compare::class), version = 1)
abstract class ItemRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun compareItemDao(): CompareItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(
                context: Context,
                scope: CoroutineScope
        ): ItemRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ItemRoomDatabase::class.java,
                        "shop_with_mom.db"
                )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        .fallbackToDestructiveMigration()
                        .addCallback(ItemDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class ItemDatabaseCallback(
                private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.itemDao())
                        populateCompareItems(database.compareItemDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         */
        fun populateDatabase(itemDao: ItemDao) {
        }

        /**
         * Populate the database in a new coroutine.
         */
        fun populateCompareItems(compareItemDao: CompareItemDao) {
        }

    }

}
