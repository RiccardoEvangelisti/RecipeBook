package com.projects.android.recipebook.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.projects.android.recipebook.Manifest

class TakePictureUtils {
	companion object {
		private const val REQUEST_CODE_PERMISSIONS = 10
		private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).toTypedArray()

		fun askPermission(activity: Activity) {
			if (allPermissionsGranted(activity.baseContext)) {
				startCamera()
			} else {
				ActivityCompat.requestPermissions(
					activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
				)
			}
		}

		private fun allPermissionsGranted(baseContext: Context) = REQUIRED_PERMISSIONS.all {
			ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
		}
	}
}