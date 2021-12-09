package gortea.jgmax.wish_list.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.app.MainActivity
import kotlin.random.Random

class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    init {
        Log.e("notification", "init")
    }

    override suspend fun doWork(): Result {
        val name = inputData.getString(PRODUCT_NAME_KEY) ?: return Result.failure()
        val currentPrice = inputData.getLong(CURRENT_PRICE_KEY, -1)
        if (currentPrice == -1L) {
            return Result.failure()
        }
        sendNotification(name.hashCode(), name, currentPrice)
        return Result.success()
    }

    private fun sendNotification(id: Int, productName: String, currentPrice: Long) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = applicationContext.getString(R.string.notification_title)
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(titleNotification)
            .setContentText("$productName: $currentPrice")
            .setDefaults(DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)
            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }

    companion object {
        const val NOTIFICATION_NAME = "Price Tracker"
        const val NOTIFICATION_CHANNEL = "PriceTracker Channel 1"
        const val PRODUCT_NAME_KEY = "PRODUCT_NAME_KEY"
        const val CURRENT_PRICE_KEY = "CURRENT_PRICE_KEY"
    }
}
