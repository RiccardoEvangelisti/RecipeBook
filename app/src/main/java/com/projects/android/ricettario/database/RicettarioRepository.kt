package com.projects.android.ricettario.database

import android.content.Context
import androidx.room.Room
import com.projects.android.ricettario.model.Ricetta

private const val DATABASE_NAME = "ricettario-database"

class RicettarioRepository private constructor(context: Context) {

	// Singleton
	companion object {

		private var INSTANCE: RicettarioRepository? = null
		fun initialize(context: Context) {
			if (INSTANCE == null) {
				INSTANCE = RicettarioRepository(context)
			}
		}

		fun get(): RicettarioRepository {
			return INSTANCE ?: throw IllegalStateException("RicettarioRepository must be initialized")
		}
	}

	private val database: RicettarioDatabase =
		Room.databaseBuilder(context.applicationContext, RicettarioDatabase::class.java, DATABASE_NAME).build()

	fun getRicette(filtri: Filters): List<Ricetta> {
		return database.ricettarioDao().getRicette(filtri)
	}

	fun getRicettaSingola(id: Int): Ricetta {
		return database.ricettarioDao().getRicettaSingola(id)
	}

	fun insertRicetta(ricetta: Ricetta) {
		return database.ricettarioDao().insertRicetta(ricetta)
	}

	fun updateRicetta(ricetta: Ricetta) {
		return database.ricettarioDao().updateRicetta(ricetta)
	}

	fun deleteRicetta(ricetta: Ricetta) {
		return database.ricettarioDao().deleteRicetta(ricetta)
	}
}