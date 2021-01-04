package org.jdfossapps.android.shopwithmom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.CheckBox

import org.jdfossapps.android.shopwithmom.database.Item
import org.jdfossapps.android.shopwithmom.database.Compare

import android.view.Menu
import android.view.MenuItem

import java.util.Date
import java.text.NumberFormat

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class ShoppingItemActivity : AppCompatActivity() {

    private lateinit var textItemName: EditText
    private lateinit var textItemDescription: EditText
    private lateinit var textItemUnit: EditText
    private lateinit var textItemPrice: EditText
    private lateinit var textItemUnitDescription: EditText
    private lateinit var textItemQuantity: EditText
    private lateinit var textItemPriceCurrency: TextView
    
    
    private var editCompare: Compare? = null
    private var editItem: Item? = null

    private lateinit var sharedPref: SharedPreferences

    private var mNumberFormat : NumberFormat = NumberFormat.getInstance()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_item)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        mNumberFormat.setMaximumFractionDigits(2)
        textItemName = findViewById<EditText>(R.id.create_edit_item_name)
        textItemDescription = findViewById<EditText>(R.id.create_edit_item_description)
        textItemUnit = findViewById<EditText>(R.id.create_edit_item_unit)
        textItemPrice = findViewById<EditText>(R.id.create_edit_item_price)
        textItemUnitDescription = findViewById<EditText>(R.id.create_edit_item_unit_description)
        textItemQuantity = findViewById<EditText>(R.id.create_edit_item_quantity)
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        textItemPriceCurrency = findViewById<TextView>(R.id.create_edit_item_price_currency_label)
        textItemPriceCurrency.setText(sharedPref.getString("defaultCurrencySymbol", resources.getString(R.string.default_currency_symbol)))

        editCompare = intent.getParcelableExtra(ShoppingItemActivity.EXTRA_REPLY)
        editCompare?.let {
            textItemUnit.setText(mNumberFormat.format(editCompare?.unit).toString())
            textItemPrice.setText(mNumberFormat.format(editCompare?.price).toString())
        }
        if(editCompare == null) {
            editItem = intent.getParcelableExtra(ShoppingItemActivity.EXTRA_EDIT_ITEM)
            editItem?.let {
                textItemName.setText(editItem?.name)
                textItemDescription.setText(editItem?.description)
                textItemUnit.setText(mNumberFormat.format(editItem?.unit).toString())
                textItemPrice.setText(mNumberFormat.format(editItem?.price).toString())
                textItemUnitDescription.setText(editItem?.unit_description)
                textItemQuantity.setText(mNumberFormat.format(editItem?.quantity).toString())

            }
        }

        val saveButton = findViewById<Button>(R.id.create_edit_save_button)

        saveButton.setOnClickListener {
            
            if (!userInputHasErrors()) {
                val replyIntent = Intent()

                var item: Item = Item(
                    0,
                    textItemName.text.toString(),
                    textItemDescription.text.toString(),
                    textItemUnitDescription.text.toString(),
                    textItemUnit.text.toString().toDouble(),
                    textItemPrice.text.toString().toDouble(),
                    textItemPrice.text.toString().toDouble() / 
                        textItemUnit.text.toString().toDouble(),
                    textItemQuantity.text.toString().toDouble(),
                    textItemPrice.text.toString().toDouble() * 
                        textItemQuantity.text.toString().toDouble(),
                    Date()
                )

                if(editItem == null) {
                    replyIntent.putExtra(EXTRA_REPLY, item)
                } else {
                    editItem?.name = textItemName.text.toString()
                    editItem?.description = textItemDescription.text.toString()
                    editItem?.unit_description = textItemUnitDescription.text.toString()
                    editItem?.unit = textItemUnit.text.toString().toDouble()
                    editItem?.price = textItemPrice.text.toString().toDouble()
                    editItem?.price_per_unit = textItemPrice.text.toString().toDouble() / 
                        textItemUnit.text.toString().toDouble()
                        editItem?.quantity = textItemQuantity.text.toString().toDouble()
                    editItem?.item_total = textItemPrice.text.toString().toDouble() * 
                        textItemQuantity.text.toString().toDouble()
                    replyIntent.putExtra(EXTRA_EDIT_ITEM, editItem)
                }
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }

        }

    }

    private fun userInputHasErrors() : Boolean {

        var hasErrors: Boolean = false

        if (TextUtils.isEmpty(textItemName.text.toString())) {
            textItemName.setError(resources.getString(R.string.shopping_item_activity_in_req))
            hasErrors = true
        }
        if (TextUtils.isEmpty(textItemUnit.text.toString())) {
            textItemUnit.setError(resources.getString(R.string.shopping_item_activity_uom_req))
            hasErrors = true
        } else if (textItemUnit.text.toString().toDouble() <= 0) {
            textItemUnit.setError(resources.getString(R.string.shopping_item_activity_uom_gtz))
            hasErrors = true
        }

        if (TextUtils.isEmpty(textItemPrice.text.toString())) {
            textItemPrice.setError(resources.getString(R.string.shopping_item_activity_price_req))
            hasErrors = true
        } else if (textItemPrice.text.toString().toDouble() <= 0) {
            textItemPrice.setError(resources.getString(R.string.shopping_item_activity_price_gtz))
            hasErrors = true
        }

        if (TextUtils.isEmpty(textItemUnitDescription.text.toString())) {
            textItemUnitDescription.setError(resources.getString(R.string.shopping_item_activity_ud_req))
            hasErrors = true
        }

        if (TextUtils.isEmpty(textItemQuantity.text.toString())) {
            textItemQuantity.setError(resources.getString(R.string.shopping_item_activity_quantity_req))
            hasErrors = true
        } else if (textItemQuantity.text.toString().toDouble() <= 0) {
            textItemQuantity.setError(resources.getString(R.string.shopping_item_activity_quantity_gtz))
            hasErrors = true
        }

        return hasErrors

    }

    companion object {
        const val EXTRA_REPLY = "org.jdfossapps.android.shopwithmom.CREATE_ITEM_FROM_COMPARE_REPLY"
        const val EXTRA_EDIT_ITEM = "org.jdfossapps.android.shopwithmom.CREATE_EDIT_ITEM_REPLY"
    }
}

