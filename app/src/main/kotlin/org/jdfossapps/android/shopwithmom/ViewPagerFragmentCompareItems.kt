package org.jdfossapps.android.shopwithmom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jdfossapps.android.shopwithmom.R
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.content.Context

import android.view.Menu
import android.view.MenuItem
import android.view.MenuInflater

import android.app.Activity
import android.content.Intent

import org.jdfossapps.android.shopwithmom.database.CompareItemAdapter
import org.jdfossapps.android.shopwithmom.database.CompareItemViewModel
import org.jdfossapps.android.shopwithmom.database.Compare

import androidx.appcompat.app.AlertDialog

class ViewPagerFragmentCompareItems : Fragment() {

    private lateinit var adapter: CompareItemAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var itemViewModel: CompareItemViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentView = inflater.inflate(R.layout.fragment_compare_items_layout, container, false)
        recyclerView = currentView.findViewById<RecyclerView>(R.id.recyclerview_tab_compare_items)
        val adapter = CompareItemAdapter(currentView.getContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        itemViewModel = ViewModelProvider(this).get(CompareItemViewModel::class.java)

        itemViewModel.allItems.observe(viewLifecycleOwner, Observer { items ->
            // Update the cached copy of the items in the adapter.
            items?.let { 
                adapter.setItems(it)
            }
        })

        val fab = currentView.findViewById<FloatingActionButton>(R.id.fab_tab_compare_items)
        fab.setOnClickListener {
            CompareItemDialogFragment.newInstance().show(requireActivity().supportFragmentManager, CompareItemDialogFragment.TAG)
        }

        return currentView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.compare_options_menu, menu)
        return super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {  
            R.id.compareItemsDeleteAll -> {  
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle(requireContext().getResources().getString(R.string.view_pager_fragment_ci_confirm_delete_action_title))
                builder.setMessage(requireContext().getResources().getString(R.string.view_pager_fragment_ci_confirm_delete_action_msg))
                builder.setPositiveButton(requireContext().getResources().getString(R.string.view_pager_fragment_ci_confirm_delete_action_yes)) { _, _ ->
                    itemViewModel.removeAllItems()
                }
                builder.setNegativeButton(requireContext().getResources().getString(R.string.view_pager_fragment_ci_confirm_delete_action_no)) { _, _ ->
                }
                builder.show()
                true  
            }
            R.id.compareOptionsMenuSettings -> {
                val intent = Intent(activity, PreferencesActivity::class.java)
                requireActivity().startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)  
        }  
    }

}