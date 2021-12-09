package gortea.jgmax.wish_list.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gortea.jgmax.wish_list.app.data.local.room.dao.PageDAO
import gortea.jgmax.wish_list.app.data.local.room.dao.WishesDAO
import gortea.jgmax.wish_list.app.data.local.room.entity.Wish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

@HiltWorker
class RecognitionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val textRecognizer: TextRecognizer,
    private val pageDAO: PageDAO,
    private val wishesDAO: WishesDAO
) : CoroutineWorker(appContext, workerParams) {
    init {
        Log.e("recognition", "init")
    }
    private val digitsRegex = Regex("[^\\d]+")
    private fun onlyDigits(str: String): String = digitsRegex.replace(str, "")

    override suspend fun doWork(): Result {
        val id = inputData.getLong(ID_KEY, -1L)
        if (id == -1L) {
            return Result.failure()
        }

        val page = pageDAO.get(id) ?: return Result.failure()
        val bitmap = page.bitmap ?: return Result.failure()
        val wish = wishesDAO.getWishByUrl(page.url) ?: return Result.failure()

        val countDownLatch = CountDownLatch(1)
        var result = Result.success()

        val image = InputImage.fromBitmap(bitmap, 0)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val newPrice = onlyDigits(visionText.text).toLongOrNull()
                result = if (newPrice == null) {
                    Result.failure()
                } else {
                    onRecognitionSucceeded(id, wish, newPrice)
                    openNotificationWorker(wish, newPrice)
                    Result.success()
                }
                countDownLatch.countDown()
            }
            .addOnFailureListener {
                result = Result.failure()
                countDownLatch.countDown()
            }
        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Log.e("recognition", "finished")
        return result
    }

    private fun openNotificationWorker(wish: Wish, value: Long) {
        if (value <= wish.targetPrice) {
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

    private fun onRecognitionSucceeded(id: Long, wish: Wish, value: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            wishesDAO.updateWish(wish.copy(currentPrice = value))
            pageDAO.delete(id)
        }
    }

    companion object {
        const val ID_KEY = "ID_KEY"
    }
}
