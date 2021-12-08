package gortea.jgmax.wish_list.extentions

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun Bitmap.cache(filename: String, context: Context) {
    val file = File(context.cacheDir, filename)
    FileOutputStream(file).use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
    }
}

fun removeBitmapCache(
    filename: String,
    context: Context,
) {
    val file = File(context.cacheDir, filename)
    if (file.exists()) {
        file.delete()
    }
}

fun decodeBitmapFromCache(
    filename: String,
    context: Context
): Bitmap? {
    val file = File(context.cacheDir, filename)
    return if (file.exists()) {
        BitmapFactory.Options().run {
            inMutable = true
            inJustDecodeBounds = false
            val bitmap = FileInputStream(file).use {
                BitmapFactory.decodeStream(it, null, this)
            }
            bitmap
        }
    } else {
        null
    }
}

fun decodeBitmapFromResource(
    res: Resources,
    resId: Int
): Bitmap {
    val options = BitmapFactory.Options()
    return options.run {
        inMutable = true
        inJustDecodeBounds = false
        BitmapFactory.decodeResource(res, resId, this)
    }
}

fun Bitmap.toByteArray(): ByteArray? {
    return ByteArrayOutputStream().use { out ->
        compress(Bitmap.CompressFormat.PNG, 100, out)
        out.toByteArray()
    }
}
