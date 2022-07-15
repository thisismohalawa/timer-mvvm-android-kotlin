package mo.egy.timermvvm.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import mo.egy.timermvvm.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        setSupportActionBar(toolbar)
        supportActionBar?.subtitle = getString(R.string.settings)
        supportActionBar?.title = getString(R.string.settings)

    }
}