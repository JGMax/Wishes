package gortea.jgmax.wish_list.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.databinding.ActivityMainBinding
import gortea.jgmax.wish_list.navigation.NavStorage
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var navStorage: NavStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.attachLoader(applicationContext)
        setContentView(binding.root)
        setupNavController()
    }

    private fun setupNavController() {
        val host = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navStorage.navController = host.navController
    }

    override fun onDestroy() {
        navStorage.navController = null
        super.onDestroy()
    }
}
