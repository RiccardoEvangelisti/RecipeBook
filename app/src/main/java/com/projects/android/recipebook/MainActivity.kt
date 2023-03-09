package com.projects.android.recipebook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}

	override fun onPause() {
		super.onPause()
		File(this@MainActivity.cacheDir.path).deleteRecursively()
		/*val dir = File(this@MainActivity.cacheDir.path)
		if (dir.isDirectory) {
			if (dir.listFiles() != null) {
				for (i in dir.listFiles()!!.indices) {
					dir.listFiles()?.get(i)?.delete()
				}
			}
		}*/
	}
}