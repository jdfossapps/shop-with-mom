package org.jdfossapps.android.shopwithmom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jdfossapps.android.shopwithmom.R


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView

import org.jdfossapps.android.shopwithmom.database.ItemViewModel
import org.jdfossapps.android.shopwithmom.database.ItemListAdapter
import org.jdfossapps.android.shopwithmom.database.Item

import org.jdfossapps.android.shopwithmom.database.ItemRoomDatabase
import androidx.lifecycle.viewModelScope
import org.jdfossapps.android.shopwithmom.database.ItemRepository
import android.content.Context
import android.view.MenuInflater

import android.widget.Toast

import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class ViewPagerFragmentItemList : Fragment() {
    private val newItemActivityRequestCode = 1
    private val editItemActivityRequestCode = 2
    private val shoppingItemActivityRequestCode = 3
    private val editShoppingItemActivityRequestCode = 4

    private lateinit var itemViewModel: ItemViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var shoppingListTotal: TextView

    private lateinit var sharedPref: SharedPreferences 
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main_list_layout, container, false)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview_tab)
        val adapter = ItemListAdapter(rootView.getContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(rootView.getContext())
        val totalCurrencySymbol = sharedPref.getString("defaultCurrencySymbol", rootView.getContext().getResources().getString(R.string.default_currency_symbol))
        shoppingListTotal = rootView.findViewById(R.id.main_list_shopping_list_total)

        itemViewModel.allItems.observe(viewLifecycleOwner, Observer { items ->
            items?.let { 
                adapter.setItems(it)
                var updatedItemsTotal = "%.2f".format(adapter.itemsTotal).toDouble().toString()
                shoppingListTotal.setText("${rootView.getContext().getResources().getString(R.string.view_pager_fragment_il_total)} ${totalCurrencySymbol}${updatedItemsTotal}")
            }
        })

        val fab = rootView.findViewById<FloatingActionButton>(R.id.fab_tab)
        fab.setOnClickListener {
            val intent = Intent(activity, ShoppingItemActivity::class.java)
            requireActivity().startActivityForResult(intent, shoppingItemActivityRequestCode)
        }

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.options_menu, menu)
        
        val searchItem = menu.findItem(R.id.mainSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                itemViewModel.searchItemsChanged(newText)
                return true
             }
            override fun onQueryTextSubmit(query: String): Boolean {
                itemViewModel.searchItemsChanged(query)
                return true
             }
        })

        return super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {  
            R.id.itemListDeleteShoppingCart -> {  
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle(requireContext().getResources().getString(R.string.view_pager_fragment_il_confirm_delete_action_title))
                builder.setMessage(requireContext().getResources().getString(R.string.view_pager_fragment_il_confirm_delete_action_msg))
                builder.setPositiveButton(requireContext().getResources().getString(R.string.view_pager_fragment_il_confirm_delete_action_yes)) { _, _ ->
                    itemViewModel.removeAllItems()
                }
                builder.setNegativeButton(requireContext().getResources().getString(R.string.view_pager_fragment_il_confirm_delete_action_no)) { _, _ ->
                }
                builder.show()
                true  
            }
            R.id.optionsMenuSettings -> {
                val intent = Intent(activity, PreferencesActivity::class.java)
                requireActivity().startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)  
        }  
    }

}