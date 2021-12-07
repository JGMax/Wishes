package gortea.jgmax.wish_list.app.data.local.room.converters

import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.lang.Exception


class BitmapConverter {
    @TypeConverter
    fun bitmapToString(bitmap: Bitmap?): String? {
        val out = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
        val b = out.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    @TypeConverter
    fun stringToBitmap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            bitmap
        } catch (e: Exception) {
            e.message
            null
        }
    }
}
