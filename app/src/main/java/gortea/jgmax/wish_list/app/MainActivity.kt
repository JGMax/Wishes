package gortea.jgmax.wish_list.app

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.databinding.ActivityMainBinding
import gortea.jgmax.wish_list.navigation.NavStorage
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel: MainActivityViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val trackingFrequencyKey: String by lazy { getString(R.string.tracking_frequency_key) }
    private val defaultFrequency: Long by lazy {
        resources.getInteger(R.integer.default_tracking_frequency).toLong()
    }

    private val nightModeKey: String by lazy { getString(R.string.night_mode_key) }
    private val defaultNightModeEnabled: Boolean by lazy {
        resources.getBoolean(R.bool.default_night_mode_enabled)
    }

    @Inject
    lateinit var navStorage: NavStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupNavController()
        viewModel.attachLoader(applicationContext)
        viewModel.startWorker(this, defaultFrequency, true)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    private fun setupNavController() {
        val host = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navStorage.navController = host.navController
    }

    override fun onDestroy() {
        navStorage.navController = null
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        preferences?.let {
            when (key) {
                trackingFrequencyKey -> viewModel.startWorker(
                    context = this,
                    frequency = preferences.getInt(key, defaultFrequency.toInt()).toLong(),
                    keep = false
                )
                nightModeKey -> {
                    val enabled = preferences.getBoolean(key, defaultNightModeEnabled)
                    if (enabled) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }
}
