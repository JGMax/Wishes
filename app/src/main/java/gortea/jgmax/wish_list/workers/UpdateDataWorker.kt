package gortea.jgmax.wish_list.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.features.factory.FeatureFactory
import gortea.jgmax.wish_list.features.wish_list.action.WishListAction
import gortea.jgmax.wish_list.features.wish_list.event.WishListEvent
import gortea.jgmax.wish_list.features.wish_list.state.WishListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

@HiltWorker
class UpdateDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    featureFactory: FeatureFactory
) : CoroutineWorker(appContext, workerParams) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val feature = featureFactory
        .createFeature<WishListState, WishListEvent, WishListAction>(coroutineScope)
        ?: throw IllegalStateException("Unknown feature")
    private val state = WishListState.Default

    init {
        Log.e("update", "init")
    }

    override suspend fun doWork(): Result {
        val countDownLatch = CountDownLatch(1)
        var result = Result.failure()
        feature.handleEvent(WishListEvent.GetList, state)
        val job = CoroutineScope(Dispatchers.Default).launch {
            val workManager = WorkManager
                .getInstance(applicationContext)
            feature.stateFlow
                .filter { state -> !state.isLoading }
                .map { state -> state.list }
                .onEach {
                    if (it.isEmpty()) {
                        result = Result.failure()
                        countDownLatch.countDown()
                    }
                }
                .filter { it.isNotEmpty() }
                .map { list -> list.map { createWorkerRequest(it) } }
                .onEach { list ->
                    var continuation = workManager.beginWith(list.first())
                    list.forEachIndexed { i, it ->
                        if (i > 0) {
                            continuation = continuation.then(it)
                        }
                    }
                    continuation.enqueue()
                    result = Result.success()
                    countDownLatch.countDown()
                }
                .collect()
        }
        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        job.cancel()
        coroutineScope.cancel()
        return result
    }

    private fun createWorkerRequest(wish: WishModel): OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val data = Data.Builder()
            .putString(DownloadWorker.URL_KEY, wish.url)
            .putInt(DownloadWorker.LEFT_CROP_KEY, wish.params.position?.left ?: -1)
            .putInt(DownloadWorker.TOP_CROP_KEY, wish.params.position?.top ?: -1)
            .putInt(DownloadWorker.RIGHT_CROP_KEY, wish.params.position?.right ?: -1)
            .putInt(DownloadWorker.BOTTOM_CROP_KEY, wish.params.position?.bottom ?: -1)
            .build()
        return OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()
    }
}
