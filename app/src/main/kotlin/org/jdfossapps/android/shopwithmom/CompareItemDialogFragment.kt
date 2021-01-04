package org.jdfossapps.android.shopwithmom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.jdfossapps.android.shopwithmom.database.CompareItemViewModel
import org.jdfossapps.android.shopwithmom.database.CompareItemRepository
import org.jdfossapps.android.shopwithmom.database.Compare
import org.jdfossapps.android.shopwithmom.database.ItemRoomDatabase
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.text.TextUtils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class CompareItemDialogFragment : DialogFragment() {

    private lateinit var compareViewModel: CompareItemViewModel
    private lateinit var unit: EditText
    private lateinit var price: EditText
    private lateinit var priceCurrency: TextView
    private var compareToEdit: Compare? = null

    private lateinit var sharedPref: SharedPreferences 

    companion object {

        const val TAG = "CompareItemDialogFragment"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        
        private const val KEY_COMPARE = "KEY_COMPARE"
        

        fun newInstance(editCompare: Compare? = null): CompareItemDialogFragment {
            val args = Bundle()
            val fragment = CompareItemDialogFragment()
            editCompare?.let {
                args.putParcelable(KEY_COMPARE, editCompare)
                fragment.arguments = args
             }

            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compare_items_fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        compareViewModel = ViewModelProvider(this).get(CompareItemViewModel::class.java)
        unit = view.findViewById<EditText>(R.id.compare_items_create_unit_of_messure)
        price = view.findViewById<EditText>(R.id.compare_items_create_price)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext())
        priceCurrency = view.findViewById<EditText>(R.id.compare_items_item_currency)
        priceCurrency.text = sharedPref.getString("defaultCurrencySymbol", view.getContext().getResources().getString(R.string.default_currency_symbol))
        
        compareToEdit = arguments?.getParcelable(KEY_COMPARE)
        compareToEdit?.let { 
            unit.setText(it.unit.toString())
            price.setText(it.price.toString())
         }

        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun userInputHasErrors(view: View) : Boolean {

        var hasErrors: Boolean = false

        if (TextUtils.isEmpty(unit.text)) {
            unit.setError(view.getContext().getResources().getString(R.string.compare_item_dialog_fragment_req_unit))
            hasErrors = true
        } else if (unit.text.toString().toDouble() <= 0) {
            unit.setError(resources.getString(R.string.compare_item_dialog_fragment_unit_gtz))
            hasErrors = true
        }

        if (TextUtils.isEmpty(price.text)) {
            price.setError(view.getContext().getResources().getString(R.string.compare_item_dialog_fragment_req_price))
            hasErrors = true
        } else if (price.text.toString().toDouble() <= 0) {
            price.setError(resources.getString(R.string.compare_item_dialog_fragment_price_gtz))
            hasErrors = true
        }

        return hasErrors

    }

    private fun setupClickListeners(view: View) {
        val saveButton = view.findViewById<Button>(R.id.compare_items_save_button)
        saveButton.setOnClickListener {
            if (!userInputHasErrors(view)) {
                compareToEdit?.let {
                    it.unit = unit.text.toString().toDouble()
                    it.price = price.text.toString().toDouble()
                    it.price_per_unit = it.price / it.unit
                }
                if(compareToEdit != null ) {
                    compareViewModel.update(compareToEdit!!)
                } else {
                    compareViewModel.insert(Compare(0,
                        unit.text.toString().toDouble(),
                        price.text.toString().toDouble(),
                        price.text.toString().toDouble() / unit.text.toString().toDouble()
                    ))
                }
                dismiss()
            }

        }
        val cancelButton = view.findViewById<Button>(R.id.compare_items_cancel_button)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

}