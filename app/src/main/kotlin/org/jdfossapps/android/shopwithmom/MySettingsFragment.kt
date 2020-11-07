package org.jdfossapps.android.shopwithmom

import androidx.preference.PreferenceFragmentCompat
import android.os.Bundle

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}