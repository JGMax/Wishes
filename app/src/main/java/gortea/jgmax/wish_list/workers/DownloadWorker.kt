package gortea.jgmax.wish_list.workers

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val feature = featureFactory
        .createFeature<SelectDataZoneState, SelectDataZoneEvent, SelectDataZoneAction>(
            coroutineScope
        )
        ?: throw IllegalAccessException("Unknown feature")
    private var state = SelectDataZoneState.Default

    private val digitsRegex = Regex("[^\\d]+")
    private fun onlyDigits(str: String): String = digitsRegex.replace(str, "")
    override suspend fun doWork(): Result {
        val url = inputData.getString(URL_KEY) ?: return Result.failure()
        val leftCrop = inputData.getInt(LEFT_CROP_KEY, -1)
        val topCrop = inputData.getInt(TOP_CROP_KEY, -1)
        val rightCrop = inputData.getInt(RIGHT_CROP_KEY, -1)
        val bottomCrop = inputData.getInt(BOTTOM_CROP_KEY, -1)
        if (leftCrop == -1 || topCrop == -1 || rightCrop == -1 || bottomCrop == -1) {
            return Result.failure()
        }

        val countDownLatch = CountDownLatch(1)
        var result = Result.failure()
        withContext(Dispatchers.Main) {
            pageLoader.attach(applicationContext)
        }

        feature.handleEvent(SelectDataZoneEvent.LoadUrl(url), state)
        feature.handleEvent(SelectDataZoneEvent.GetWish(url), state)

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
                            result = Result.failure()
                            countDownLatch.countDown()
                        }
                        is SelectDataZoneAction.LoadingFailed -> {
                            result = Result.retry()
                            countDownLatch.countDown()
                        }
                        is SelectDataZoneAction.RenderBitmap -> {
                            val cropped =
                                cropBitmap(action.bitmap, leftCrop, topCrop, rightCrop, bottomCrop)
                            if (cropped != null) {
                                feature.handleEvent(
                                    SelectDataZoneEvent.RecognizeText(cropped),
                                    state
                                )
                            } else {
                                result = Result.retry()
                                countDownLatch.countDown()
                            }
                        }
                        is SelectDataZoneAction.RecognitionFailed -> {
                            result = Result.retry()
                            countDownLatch.countDown()
                        }
                        is SelectDataZoneAction.RecognitionResult -> {
                            val newPrice = onlyDigits(action.result).toLongOrNull()
                            result = if (newPrice == null || wishModel == null) {
                                countDownLatch.countDown()
                                Result.retry()
                            } else {
                                wishModel?.let {
                                    onRecognitionSucceeded(it, newPrice)
                                    openNotificationWorker(it, newPrice)
                                    Result.success()
                                } ?: Result.failure()
                            }
                        }
                        is SelectDataZoneAction.WishUpdated -> {
                            result = Result.success()
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

        return result
    }

    private fun openNotificationWorker(wish: WishModel, value: Long) {
        if (value <= requireNotNull(wish.params.targetPrice)) {
            val data = Data.Builder()
                .putString(NotificationWorker.PRODUCT_NAME_KEY, wish.title)
                .putLong(NotificationWorker.CURRENT_PRICE_KEY, value)
                .build()
            val notificationWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(data)
                    .build()
            WorkManager
                .getInstance(applicationContext)
                .enqueue(notificationWorkRequest)
        }
    }

    private fun onRecognitionSucceeded(wish: WishModel, value: Long) {
        feature.handleEvent(
            SelectDataZoneEvent.UpdateWish(wishModel = wish.copy(currentPrice = value)),
            state
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
    }
}
