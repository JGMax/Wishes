package gortea.jgmax.wish_list.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.databinding.ActivityMainBinding
import gortea.jgmax.wish_list.navigation.NavStorage
import gortea.jgmax.wish_list.workers.DownloadWorker
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var navStorage: NavStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupNavController()
        viewModel.attachLoader(applicationContext)
        viewModel.startWorker(applicationContext)
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
