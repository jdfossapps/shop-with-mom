package org.jdfossapps.android.shopwithmom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import org.jdfossapps.android.shopwithmom.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView

import org.jdfossapps.android.shopwithmom.database.ItemViewModel
import org.jdfossapps.android.shopwithmom.database.ItemListAdapter
import org.jdfossapps.android.shopwithmom.database.Item

import org.jdfossapps.android.shopwithmom.database.ItemRoomDatabase
import androidx.lifecycle.viewModelScope
import org.jdfossapps.android.shopwithmom.database.ItemRepository

import android.content.Intent
import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider

import android.widget.Toast

class TabbedActivity : AppCompatActivity() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager2? = null

    private val newItemActivityRequestCode = 1
    private val editItemActivityRequestCode = 2
    private val shoppingItemActivityRequestCode = 3
    private val editShoppingItemActivityRequestCode = 4

    private lateinit var itemViewModel: ItemViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_main)
        tabLayout = findViewById(R.id.tabs) as TabLayout
        viewPager = findViewById(R.id.viewpager) as ViewPager2
        viewPager!!.setAdapter(MyAdapter(supportFragmentManager, lifecycle))
        TabLayoutMediator(tabLayout!!, viewPager!!, TabLayoutMediator.TabConfigurationStrategy{tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.tabbed_activity_item_comparisons)
                1 -> tab.text = resources.getString(R.string.tabbed_activity_shopping_list)
            }
        }).attach()

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

    }

    private inner class MyAdapter(fm: FragmentManager?, lifecycle: Lifecycle) : FragmentStateAdapter(fm!!, lifecycle) {
        private val int_items = 2

        override fun createFragment(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = ViewPagerFragmentCompareItems()
                1 -> fragment = ViewPagerFragmentItemList()
            }
            return fragment!!
        }

        override fun getItemCount(): Int {
            return int_items
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == shoppingItemActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                itemViewModel.insert(data.getParcelableExtra(ShoppingItemActivity.EXTRA_REPLY)!!)
                Unit
            }
        } else if (requestCode == editShoppingItemActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                itemViewModel.update(data.getParcelableExtra(ShoppingItemActivity.EXTRA_EDIT_ITEM)!!)
                Unit
            }
        }
    }

}