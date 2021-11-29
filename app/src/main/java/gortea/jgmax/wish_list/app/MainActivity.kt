package gortea.jgmax.wish_list.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.navigation.NavStorage
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var pageLoader: PageLoader

    @Inject
    lateinit var navStorage: NavStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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