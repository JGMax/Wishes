package gortea.jgmax.wish_list.screens.extensions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

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
