package gortea.jgmax.wish_list.workers

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gortea.jgmax.wish_list.app.data.local.room.dao.PageDAO
import gortea.jgmax.wish_list.app.data.local.room.entity.Page
import gortea.jgmax.wish_list.app.data.remote.loader.PageLoader
import gortea.jgmax.wish_list.di.BackgroundLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @BackgroundLoader private val pageLoader: PageLoader,
    private val pageDAO: PageDAO
) : CoroutineWorker(appContext, workerParams) {
    init {
        Log.e("download", "init")
    }

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
        var result = Result.success()
        withContext(Dispatchers.Main) {
            pageLoader.attach(applicationContext)
            pageLoader.attachListeners(
                onComplete = { page, _ ->
                    Log.e("page", "${page.width} ${page.height}")
                    val cropped = cropBitmap(page, leftCrop, topCrop, rightCrop, bottomCrop)
                    page.recycle()

                    result = if (cropped == null) {
                        Result.failure()
                    } else {
                        startRecognitionWorker(cropped, url)
                        Result.success()
                    }
                    countDownLatch.countDown()
                },
                onError = {
                    result = Result.failure()
                    countDownLatch.countDown()
                }
            )
            pageLoader.loadAsBitmap(url)
        }

        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        withContext(Dispatchers.Main) {
            pageLoader.detach()
        }

        return result
    }

    private fun startRecognitionWorker(bitmap: Bitmap, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = pageDAO.add(Page(0L, bitmap, url))
            val data = Data.Builder()
                .putLong(RecognitionWorker.ID_KEY, id)
                .build()
            val recognitionWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<RecognitionWorker>()
                    .setInputData(data)
                    .build()
            WorkManager
                .getInstance(applicationContext)
                .enqueue(recognitionWorkRequest)
        }
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
