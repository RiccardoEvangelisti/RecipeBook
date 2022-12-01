package com.projects.android.recipebook.view.add.tag

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class TagSpan(var id: String, var onClick: () -> Unit) : ClickableSpan() {

	override fun onClick(view: View) {
		onClick()
	}

	override fun updateDrawState(ds: TextPaint) {
		super.updateDrawState(ds)
		ds.isUnderlineText = true
	}
}