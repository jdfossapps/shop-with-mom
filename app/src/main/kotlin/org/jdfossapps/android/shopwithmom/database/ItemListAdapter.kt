package org.jdfossapps.android.shopwithmom.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

import org.jdfossapps.android.shopwithmom.R

import java.text.SimpleDateFormat

import android.widget.Toast

import androidx.appcompat.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import org.jdfossapps.android.shopwithmom.ShoppingItemActivity
import org.jdfossapps.android.shopwithmom.database.ItemViewModel
import androidx.lifecycle.ViewModelProvider

import androidx.appcompat.app.AlertDialog

import android.content.Intent
import java.text.DateFormat
import java.util.Locale
import java.text.NumberFormat

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class ItemListAdapter internal constructor(
        context: Context
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Item>() // Cached copy of items
    private var currentContext: Context = context
    private var cabSelectedItems = mutableListOf<Item>()
    private var actionMode: ActionMode? = null
    private var multiSelectOn: Boolean = false
    private var itemViewModel: ItemViewModel = ViewModelProvider((context as AppCompatActivity)).get(ItemViewModel::class.java)
    private val shoppingItemActivityRequestCode = 3
    private val editShoppingItemActivityRequestCode = 4
    var itemsTotal: Double = 0.0
    private var mNumberFormat : NumberFormat = NumberFormat.getInstance()
    

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        private val actionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.main_contextual_action_bar_menu, menu)
                return true
            }
        
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }
        
            // Called when the user selects a contextual menu item
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.delete_cab -> {
                        val builder = AlertDialog.Builder(itemView.getContext() as AppCompatActivity)
                        builder.setTitle(itemView.getContext().getResources().getString(R.string.item_adapter_confirm_delete_action_title))
                        builder.setMessage(itemView.getContext().getResources().getString(R.string.item_adapter_confirm_delete_action_msg))
                        builder.setPositiveButton(itemView.getContext().getResources().getString(R.string.item_adapter_confirm_delete_action_yes)) { _, _ ->
                            itemViewModel.removeItems(cabSelectedItems.toList())
                            cabSelectedItems.clear()
                            notifyDataSetChanged()
                            mode.finish() // Action picked, so close the CAB
                        }
                        builder.setNegativeButton(itemView.getContext().getResources().getString(R.string.item_adapter_confirm_delete_action_no)) { _, _ ->
                        }
                        builder.show()
                        
                        true
                    }
                    R.id.share_cab -> {
                        mNumberFormat.setMaximumFractionDigits(2)
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            var textToSend = ""
                            val currencySymbol = sharedPref.getString("defaultCurrencySymbol", itemView.getContext().getResources().getString(R.string.default_currency_symbol))
                            val localFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
                            cabSelectedItems.forEach { 
                                textToSend += "${it.name} "
                                textToSend += "${it.unit_description.capitalize()}: ${mNumberFormat.format(it.unit).toString()}\n"
                                if(it.description.length > 0) {
                                    textToSend += "${it.description}\n"
                                }
                                textToSend += "${itemView.getContext().getResources().getString(R.string.item_adapter_share_cab_price)} "
                                textToSend += "${currencySymbol}${mNumberFormat.format(it.price).toString()}\n"

                                textToSend += "${itemView.getContext().getResources().getString(R.string.item_adapter_share_cab_quantity)} "
                                textToSend += "${mNumberFormat.format(it.quantity).toString()} "
                                textToSend += "${itemView.getContext().getResources().getString(R.string.item_adapter_share_cab_item_total)} "
                                textToSend += "${currencySymbol}${mNumberFormat.format(it.item_total).toString()}\n"

                                mNumberFormat.setMaximumFractionDigits(4)
                                textToSend += "${itemView.getContext().getResources().getString(R.string.item_adapter_share_cab_ppu)} "
                                textToSend += "${currencySymbol}${"%.4f".format(it.price_per_unit).toDouble().toString()}\n"
                                mNumberFormat.setMaximumFractionDigits(2)
                                textToSend += "${itemView.getContext().getResources().getString(R.string.item_adapter_share_cab_date)} "
                                textToSend += "${localFormat.format(it.created_at!!)}\n\n"
                            }
                            putExtra(Intent.EXTRA_TEXT, textToSend.dropLast(2))
                            type = "text/plain"
                        }
                        
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        (itemView.getContext() as AppCompatActivity).startActivity(shareIntent)
                        mode.finish()
                        true
                    }
                    else -> false
                }
            }
        
            // Called when the user exits the action mode
            override fun onDestroyActionMode(mode: ActionMode) {
                multiSelectOn = false
                cabSelectedItems.clear()
                notifyDataSetChanged()
                actionMode = null
            }
        }

        val itemViewItemNameWithUnit: TextView = itemView.findViewById(R.id.mainItemNameWithUnit)

        val itemViewPrice: TextView = itemView.findViewById(R.id.mainItemPrice)
        val itemViewCreatedAt: TextView = itemView.findViewById(R.id.mainItemCreatedAt)
        val itemViewItemTotal: TextView = itemView.findViewById(R.id.mainItemTotal)
        val itemViewQuantity: TextView = itemView.findViewById(R.id.mainQuantity)
        val itemSelectedItem: CheckBox = itemView.findViewById(R.id.selectedItemCAB)

        val itemViewMainItemPriceCurrencyLabel: TextView = itemView.findViewById(R.id.mainItemPriceCurrencyLabel)
        val itemViewMmainItemTotalCurrencyLabel: TextView = itemView.findViewById(R.id.mainItemTotalCurrencyLabel)


        init {

            itemView.setOnClickListener { v: View ->
                if (!multiSelectOn) {
                    val position: Int = bindingAdapterPosition
                    val intent = Intent( (v.getContext() as AppCompatActivity) , ShoppingItemActivity::class.java)
                    intent.putExtra(ShoppingItemActivity.EXTRA_EDIT_ITEM, items[position] )
                    (v.getContext() as AppCompatActivity).startActivityForResult(intent, editShoppingItemActivityRequestCode)
                } else {
                    if (itemSelectedItem.isChecked) {
                        itemSelectedItem.setChecked(false)
                        cabSelectedItems.remove(items[position])
                        
                    } else {
                        itemSelectedItem.setChecked(true)
                        cabSelectedItems.add(items[position])
                    }
                    actionMode?.title = "${cabSelectedItems.size} ${v.getContext().getResources().getString(R.string.item_adapter_share_cab_selected)}"
                    notifyDataSetChanged()
                }
                
            }

            itemView.setOnLongClickListener { v: View ->
                val position: Int = bindingAdapterPosition
                if (!multiSelectOn) {
                    if (actionMode == null) actionMode = (v.getContext() as AppCompatActivity).startSupportActionMode(actionModeCallback)
                    itemSelectedItem.setChecked(true)
                    cabSelectedItems.add(items[position])
                    actionMode?.title = "${cabSelectedItems.size} ${v.getContext().getResources().getString(R.string.item_adapter_share_cab_selected)}"
                    notifyDataSetChanged()
                }
                multiSelectOn = true
                true
            }

            itemSelectedItem.setOnClickListener { v: View ->
                val position: Int = bindingAdapterPosition
                
                if (multiSelectOn) {
                    if (!itemSelectedItem.isChecked) {
                        cabSelectedItems.remove(items[position])
                    }
                    else {
                        cabSelectedItems.add(items[position])
                    }
                    actionMode?.title = "${cabSelectedItems.size} ${v.getContext().getResources().getString(R.string.item_adapter_share_cab_selected)}"
                    notifyDataSetChanged()
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = items[position]
        
        val itemWithUnit: String = 
            if (current.unit_description.isNullOrBlank()) 
            "${holder.itemView.getContext().getResources().getString(R.string.item_adapter_share_cab_unit2)}" else current.unit_description.capitalize()
        holder.itemViewItemNameWithUnit.text = "${current.name}  ${itemWithUnit}: " +            
            "%.2f".format(current.unit).toDouble().toString()

        holder.itemViewPrice.text = "%.2f".format(current.price).toDouble().toString()
        holder.itemViewQuantity.text = "%.2f".format(current.quantity).toDouble().toString()
        holder.itemViewItemTotal.text = "%.2f".format(current.item_total).toDouble().toString()

        holder.itemViewMainItemPriceCurrencyLabel.text =  sharedPref.getString("defaultCurrencySymbol", holder.itemView.getContext().getResources().getString(R.string.default_currency_symbol))
        holder.itemViewMmainItemTotalCurrencyLabel.text = sharedPref.getString("defaultCurrencySymbol", holder.itemView.getContext().getResources().getString(R.string.default_currency_symbol))
        
        val localFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        val tmpRequestedAtDate = localFormat.format(current.created_at!!)

        holder.itemViewCreatedAt.text = tmpRequestedAtDate

        if (multiSelectOn) {
            holder.itemSelectedItem.setVisibility(View.VISIBLE)
            if (cabSelectedItems.contains(current))
                holder.itemSelectedItem.setChecked(true)
            else
                holder.itemSelectedItem.setChecked(false)
        } else {
            holder.itemSelectedItem.setChecked(false)
            holder.itemSelectedItem.setVisibility(View.GONE)
        }

    }

    internal fun setItems(items: List<Item>) {
        this.items = items
        itemsTotal = 0.0
        this.items.forEach {
            itemsTotal += it.item_total
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

}


