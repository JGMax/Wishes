package gortea.jgmax.wish_list.workers

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.di.BackgroundLoader
import gortea.jgmax.wish_list.features.factory.FeatureFactory
import gortea.jgmax.wish_list.features.select_data_zone.action.SelectDataZoneAction
import gortea.jgmax.wish_list.features.select_data_zone.event.SelectDataZoneEvent
import gortea.jgmax.wish_list.features.select_data_zone.state.SelectDataZoneState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.CountDownLatch

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @BackgroundLoader private val pageLoader: PageLoader,
    featureFactory: FeatureFactory
) : CoroutineWorker(appContext, workerParams) {

    init {
        featureFactory.store = featureFactory.store.copy(pageLoader = pageLoader)
        Log.e("download", "init")
    }

    private var failureCount = 0

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val feature = featureFactory
        .createFeature<SelectDataZoneState, SelectDataZoneEvent, SelectDataZoneAction>(
            coroutineScope
        )
        ?: throw IllegalAccessException("Unknown feature")

    private val digitsRegex = Regex("[^\\d]+")
    private fun onlyDigits(str: String): String = digitsRegex.replace(str, "")

    override suspend fun doWork(): Result {
        val url = inputData.getString(URL_KEY) ?: return Result.failure()
        val leftCrop = inputData.getInt(LEFT_CROP_KEY, -1)
        val topCrop = inputData.getInt(TOP_CROP_KEY, -1)
        val rightCrop = inputData.getInt(RIGHT_CROP_KEY, -1)
        val bottomCrop = inputData.getInt(BOTTOM_CROP_KEY, -1)
        if (leftCrop == -1 || topCrop == -1 || rightCrop == -1 || bottomCrop == -1) {
            return Result.success()
        }

        val countDownLatch = CountDownLatch(1)
        withContext(Dispatchers.Main) {
            pageLoader.attach(applicationContext)
        }

        feature.handleEvent(SelectDataZoneEvent.LoadUrl(url), null)
        feature.handleEvent(SelectDataZoneEvent.GetWish(url), null)

        var wishModel: WishModel? = null

        val job = CoroutineScope(Dispatchers.Default).launch {
            feature.actionFlow
                .filter {
                    it is SelectDataZoneAction.RenderBitmap ||
                            it is SelectDataZoneAction.RecognitionResult ||
                            it is SelectDataZoneAction.ReturnWish ||
                            it is SelectDataZoneAction.LoadingFailed ||
                            it is SelectDataZoneAction.RecognitionFailed ||
                            it is SelectDataZoneAction.UnknownWish ||
                            it is SelectDataZoneAction.WishUpdated
                }
                .onEach { action ->
                    when (action) {
                        is SelectDataZoneAction.ReturnWish -> {
                            wishModel = action.wishModel
                        }
                        is SelectDataZoneAction.UnknownWish -> {
                            wishModel?.let { openNotificationWorkerFailure(it) }
                            countDownLatch.countDown()
                        }
                        is SelectDataZoneAction.LoadingFailed -> {
                            onFailure(url, wishModel, countDownLatch)
                        }
                        is SelectDataZoneAction.RenderBitmap -> {
                            val cropped =
                                cropBitmap(action.bitmap, leftCrop, topCrop, rightCrop, bottomCrop)
                            if (cropped == null) {
                                onFailure(url, wishModel, countDownLatch)
                            } else {
                                feature.handleEvent(
                                    SelectDataZoneEvent.RecognizeText(cropped),
                                    null
                                )
                            }
                        }
                        is SelectDataZoneAction.RecognitionFailed -> {
                            onFailure(url, wishModel, countDownLatch)
                        }
                        is SelectDataZoneAction.RecognitionResult -> {
                            val newPrice = onlyDigits(action.result).toLongOrNull()
                            if (newPrice == null || wishModel == null) {
                                onFailure(url, wishModel, countDownLatch)
                            } else {
                                wishModel?.let {
                                    onRecognitionSucceeded(it, newPrice)
                                    openNotificationWorkerSuccess(it, newPrice)
                                }
                            }
                        }
                        is SelectDataZoneAction.WishUpdated -> {
                            countDownLatch.countDown()
                        }
                    }
                }
                .collect()
        }
        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        withContext(Dispatchers.Main) {
            pageLoader.detach()
        }
        job.cancel()
        coroutineScope.cancel()

        return Result.success()
    }

    private fun onFailure(url: String, wishModel: WishModel?, countDownLatch: CountDownLatch) {
        failureCount++
        if (failureCount >= MAX_FAILURE_COUNT) {
            wishModel?.let { openNotificationWorkerFailure(it) }
            countDownLatch.countDown()
        } else {
            Log.e("retry", url)
            feature.handleEvent(SelectDataZoneEvent.ReloadUrl(url), null)
        }
    }

    private fun openNotificationWorkerFailure(wish: WishModel) {
        val title = applicationContext.getString(R.string.notification_title_failure)
        val data = Data.Builder()
            .putInt(NotificationWorker.ID_KEY, wish.title.hashCode())
            .putString(NotificationWorker.TITLE_KEY, title)
            .putString(NotificationWorker.SUBTITLE_KEY, wish.title)
            .build()
        openNotificationWorker(data)
    }

    private fun openNotificationWorkerSuccess(wish: WishModel, value: Long) {
        if (value <= requireNotNull(wish.params.targetPrice)) {
            val title = applicationContext.getString(R.string.notification_title)
            val data = Data.Builder()
                .putInt(NotificationWorker.ID_KEY, wish.title.hashCode())
                .putString(NotificationWorker.TITLE_KEY, title)
                .putString(NotificationWorker.SUBTITLE_KEY, "${wish.title}: $value")
                .build()
            openNotificationWorker(data)
        }
    }

    private fun openNotificationWorker(data: Data) {
        val notificationWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(data)
                .build()
        WorkManager
            .getInstance(applicationContext)
            .enqueue(notificationWorkRequest)
    }

    private fun onRecognitionSucceeded(wish: WishModel, value: Long) {
        feature.handleEvent(
            SelectDataZoneEvent.UpdateWish(wishModel = wish.copy(currentPrice = value)),
            null
        )
    }

    private fun cropBitmap(
        bitmap: Bitmap,
        leftCrop: Int,
        topCrop: Int,
        rightCrop: Int,
        bottomCrop: Int
    ): Bitmap? {
        if (rightCrop > bitmap.width || bottomCrop > bitmap.height) {
            return null
        }
        return Bitmap.createBitmap(
            bitmap,
            leftCrop,
            topCrop,
            (rightCrop - leftCrop),
            (bottomCrop - topCrop)
        )
    }

    companion object {
        const val URL_KEY = "URL_DATA"
        const val LEFT_CROP_KEY = "LEFT_CROP_KEY"
        const val TOP_CROP_KEY = "TOP_CROP_KEY"
        const val RIGHT_CROP_KEY = "RIGHT_CROP_KEY"
        const val BOTTOM_CROP_KEY = "BOTTOM_CROP_KEY"
        private const val MAX_FAILURE_COUNT = 3
    }
}
