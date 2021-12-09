package gortea.jgmax.wish_list.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.di.ForegroundLoader
import gortea.jgmax.wish_list.workers.UpdateDataWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ForegroundLoader private val loader: PageLoader
) : ViewModel() {
    fun startWorker(context: Context, frequency: Long, keep: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()
        val request =
            PeriodicWorkRequestBuilder<UpdateDataWorker>(1440 / frequency, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(
            WORKER_NAME,
            if (keep) ExistingPeriodicWorkPolicy.KEEP else ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    fun attachLoader(context: Context) {
        // Workaround to fix memory leak
        loader.attach(context)
    }

    override fun onCleared() {
        // Workaround to fix memory leak
        loader.detach()
        super.onCleared()
    }

    private companion object {
        private const val WORKER_NAME = "PRICE_TRACKER_WORK"
    }
}
