package com.projects.android.recipebook.view.add.tag

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast

class TagSpan(var id: String) : ClickableSpan() {

	override fun onClick(view: View) {
		Toast.makeText(view.context, "CHECK", Toast.LENGTH_SHORT).show() //TODO
	}

	override fun updateDrawState(ds: TextPaint) {
		super.updateDrawState(ds)
		ds.isUnderlineText = true
	}
}