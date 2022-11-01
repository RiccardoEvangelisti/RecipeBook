package com.projects.android.ricettario

import android.app.Application
import com.projects.android.ricettario.database.RicettarioRepository

class RicettarioApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		RicettarioRepository.initialize(this)
	}
}