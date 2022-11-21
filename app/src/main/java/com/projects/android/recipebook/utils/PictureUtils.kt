package com.projects.android.recipebook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.InputStream
import kotlin.math.roundToInt

class PictureUtils {

	companion object {

		fun getScaledBitmap(context: Context, path: String, destWidth: Int, destHeight: Int): Bitmap {
			// Read in the dimensions of the image on disk
			val options = BitmapFactory.Options()
			options.inJustDecodeBounds = true
			BitmapFactory.decodeFile(path, options)
			val srcWidth = options.outWidth.toFloat()
			val srcHeight = options.outHeight.toFloat()
			// Figure out how much to scale down by
			val sampleSize = if (srcHeight <= destHeight && srcWidth <= destWidth) {
				1
			} else {
				val heightScale = srcHeight / destHeight
				val widthScale = srcWidth / destWidth
				minOf(heightScale, widthScale).roundToInt()
			}
			// Read in and create final bitmap
			var bitmap = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sampleSize })

			// Rotate image if required
			val input: InputStream? = context.contentResolver.openInputStream(File(path).toUri())
			bitmap = when (ExifInterface(input!!).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
				ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
				ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
				ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
				else -> bitmap
			}
			input.close()

			return bitmap
		}

		private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
			val matrix = Matrix()
			matrix.postRotate(degree.toFloat())
			val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
			img.recycle()
			return rotatedImg
		}
	}
}