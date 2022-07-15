package mo.egy.timermvvm.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import mo.egy.timermvvm.R

class SettingsView : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

    }
}