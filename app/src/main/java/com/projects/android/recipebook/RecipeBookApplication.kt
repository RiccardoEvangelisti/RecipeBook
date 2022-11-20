package com.projects.android.recipebook

import android.app.Application
import com.projects.android.recipebook.database.RecipeBookRepository

class RecipeBookApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		RecipeBookRepository.initialize(this)
	}
}