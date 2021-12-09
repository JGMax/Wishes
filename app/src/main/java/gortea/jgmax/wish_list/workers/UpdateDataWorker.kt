package gortea.jgmax.wish_list.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gortea.jgmax.wish_list.app.data.local.room.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.local.room.entity.Wish
import kotlinx.coroutines.flow.map

@HiltWorker
class UpdateDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val wishesDAO: WishesDAO
) : CoroutineWorker(appContext, workerParams) {

    init {
        Log.e("update", "init")
    }

    override suspend fun doWork(): Result {
        val requests = wishesDAO.getWishes().map { createWorkerRequest(it) }
        if (requests.isNotEmpty()) {
            WorkManager
                .getInstance(applicationContext)
                .enqueue(requests)
        }
        return Result.success()
    }

    private fun createWorkerRequest(wish: Wish): WorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val data = Data.Builder()
            .putString(DownloadWorker.URL_KEY, wish.url)
            .putInt(DownloadWorker.LEFT_CROP_KEY, wish.priceLeft)
            .putInt(DownloadWorker.TOP_CROP_KEY, wish.priceTop)
            .putInt(DownloadWorker.RIGHT_CROP_KEY, wish.priceRight)
            .putInt(DownloadWorker.BOTTOM_CROP_KEY, wish.priceBottom)
            .build()
        return OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()
    }
}