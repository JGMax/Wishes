package gortea.jgmax.wish_list.extentions

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun Bitmap.cache(filename: String, context: Context) {
    val file = File(context.cacheDir, filename)
    FileOutputStream(file).use {
        compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
}

fun removeBitmapCache(
    filename: String,
    context: Context,
) {
    val file = File(context.cacheDir, filename)
    file.delete()
}

fun decodeBitmapFromCache(
    filename: String,
    context: Context
): Bitmap? {
    val file = File(context.cacheDir, filename)
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        FileInputStream(file).use {
            BitmapFactory.decodeStream(it, null, this)
        }

        inJustDecodeBounds = false
        inMutable = true

        val bitmap = FileInputStream(file).use {
            BitmapFactory.decodeStream(it, null, this)
        }
        bitmap
    }
}

fun decodeSampledBitmapFromResource(
    res: Resources,
    resId: Int
): Bitmap {
    // First decode with inJustDecodeBounds=true to check dimensions
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, this)

        // Decode bitmap with inSampleSize set
        inMutable = true
        inJustDecodeBounds = false

        BitmapFactory.decodeResource(res, resId, this)
    }
}
