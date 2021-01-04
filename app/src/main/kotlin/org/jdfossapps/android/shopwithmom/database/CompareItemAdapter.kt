package org.jdfossapps.android.shopwithmom.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.CheckBox
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

import org.jdfossapps.android.shopwithmom.R
import java.text.SimpleDateFormat
import android.widget.Toast

import androidx.appcompat.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import org.jdfossapps.android.shopwithmom.database.CompareItemViewModel
import androidx.lifecycle.ViewModelProvider

import androidx.appcompat.app.AlertDialog

import android.content.Intent
import org.jdfossapps.android.shopwithmom.ShoppingItemActivity
import org.jdfossapps.android.shopwithmom.CompareItemDialogFragment

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

import java.text.NumberFormat

class CompareItemAdapter internal constructor(
        context: Context
) : RecyclerView.Adapter<CompareItemAdapter.ItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Compare>() // Cached copy of items
    private var cabSelectedItems = mutableListOf<Compare>()
    private var actionMode: ActionMode? = null
    private var multiSelectOn: Boolean = false
    private var itemViewModel: CompareItemViewModel = ViewModelProvider((context as AppCompatActivity)).get(CompareItemViewModel::class.java)
    private val shoppingItemActivityRequestCode = 3

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val compareItemViewUnit: TextView = itemView.findViewById(R.id.compareItemUnit)
        val compareItemViewPrice: TextView = itemView.findViewById(R.id.mainCompareItemPrice)
        val compareItemViewPricePerUnit: TextView = itemView.findViewById(R.id.mainCompareItemPricePerUnit)

        val compareItemViewMainCompareItemCurrency: TextView = itemView.findViewById(R.id.mainCompareItemCurrency)
        val compareItemViewMainCompareItemPPUCurrency: TextView = itemView.findViewById(R.id.mainCompareItemPPUCurrency)

        init {
            itemView.setOnLongClickListener { v: View ->
                CompareItemDialogFragment.newInstance(items[bindingAdapterPosition]).show((v.getContext() as AppCompatActivity).supportFragmentManager, CompareItemDialogFragment.TAG)
                true
            }

            val addShoppingCartButton = itemView.findViewById<Button>(R.id.add_shopping_cart_button_compare_item)
            addShoppingCartButton.setOnClickListener {
                val intent = Intent( (itemView.getContext() as AppCompatActivity) , ShoppingItemActivity::class.java)
                intent.putExtra(ShoppingItemActivity.EXTRA_REPLY, items[bindingAdapterPosition] )
                (itemView.getContext() as AppCompatActivity).startActivityForResult(intent, shoppingItemActivityRequestCode)
            }

            val deleteButton = itemView.findViewById<Button>(R.id.delete_button_compare_item)
            deleteButton.setOnClickListener {

                val builder = AlertDialog.Builder(itemView.getContext())
                builder.setTitle(itemView.getContext().getResources().getString(R.string.compare_item_adapter_confirm_delete_action_title))
                builder.setMessage(itemView.getContext().getResources().getString(R.string.compare_item_adapter_confirm_delete_action_msg))
                builder.setPositiveButton(itemView.getContext().getResources().getString(R.string.compare_item_adapter_confirm_delete_action_yes)) { _, _ ->
                    itemViewModel.remove(items[bindingAdapterPosition])
                }
                builder.setNegativeButton(itemView.getContext().getResources().getString(R.string.compare_item_adapter_confirm_delete_action_no)) { _, _ ->
                }
                builder.show()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_compare_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = items[position]

        // holder.compareItemViewUnit.text = "%.2f".format(item.unit).toDouble().toString()
        // holder.compareItemViewPrice.text = "%.2f".format(item.price).toDouble().toString()
        // holder.compareItemViewPricePerUnit.text = "%.4f".format(item.price_per_unit).toDouble().toString()
        
        val mNumberFormat : NumberFormat = NumberFormat.getInstance()
        holder.compareItemViewUnit.text = mNumberFormat.format(item.unit).toString()
        holder.compareItemViewPrice.text = mNumberFormat.format(1.2).toString()
        holder.compareItemViewPricePerUnit.text = "2"

        holder.compareItemViewMainCompareItemCurrency.text =  sharedPref.getString("defaultCurrencySymbol", holder.itemView.getContext().getResources().getString(R.string.default_currency_symbol))
        holder.compareItemViewMainCompareItemPPUCurrency.text = sharedPref.getString("defaultCurrencySymbol", holder.itemView.getContext().getString(R.string.default_currency_symbol))

        if(position == 0) {
            holder.compareItemViewPricePerUnit.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check_icon,0)
        } else {
            holder.compareItemViewPricePerUnit.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        }
        
    }

    internal fun setItems(items: List<Compare>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

}