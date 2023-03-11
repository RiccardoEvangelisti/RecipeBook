package com.projects.android.recipebook.utils

import android.content.Context
import android.widget.Toast

class ErrorUtil {
	companion object {

		fun shortToast(context: Context, text: String) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
		}
	}
}