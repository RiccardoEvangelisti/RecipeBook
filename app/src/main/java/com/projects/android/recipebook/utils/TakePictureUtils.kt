package com.projects.android.recipebook.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TakePictureUtils {
	companion object {
		private const val REQUEST_CODE_PERMISSIONS = 10
		private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).toTypedArray()

		fun askPermission(activity: Activity) {
			if (allPermissionsGranted(activity.baseContext)) {
				startCamera(activity.baseContext)
			} else {
				ActivityCompat.requestPermissions(
					activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
				)
			}
		}

		private fun allPermissionsGranted(baseContext: Context) = REQUIRED_PERMISSIONS.all {
			ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
		}

		private fun startCamera(baseContext: Context) {
			val cameraProviderFuture = ProcessCameraProvider.getInstance(baseContext)

			cameraProviderFuture.addListener({
				// Used to bind the lifecycle of cameras to the lifecycle owner
				val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

				// Preview
				val preview = Preview.Builder().build().also {
					it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
				}

				// Select back camera as a default
				val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

				try {
					// Unbind use cases before rebinding
					cameraProvider.unbindAll()

					// Bind use cases to camera
					cameraProvider.bindToLifecycle(this, cameraSelector, preview)
				} catch (exc: Exception) {
					Log.e("RecipeBook", "Use case binding failed", exc)
				}

			}, ContextCompat.getMainExecutor(this))
		}
	}
}