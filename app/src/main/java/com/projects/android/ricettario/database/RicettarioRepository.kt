package com.projects.android.ricettario.database

import android.content.Context
import androidx.room.Room
import com.projects.android.ricettario.model.Ricetta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "ricettario-database"

class RicettarioRepository private constructor(context: Context, private val coroutineScope: CoroutineScope = GlobalScope) {

	companion object {// Singleton

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

	fun getRicette(filtri: Filters): Flow<List<Ricetta>> {
		return database.ricettarioDao().getRicette(filtri)
	}

	suspend fun getRicettaSingola(id: Int): Ricetta {
		return database.ricettarioDao().getRicettaSingola(id)
	}

	fun insertRicetta(ricetta: Ricetta) {
		coroutineScope.launch {
			database.ricettarioDao().insertRicetta(ricetta)
		}
	}

	fun updateRicetta(ricetta: Ricetta) {
		coroutineScope.launch {
			database.ricettarioDao().updateRicetta(ricetta)
		}
	}

	fun deleteRicetta(ricetta: Ricetta) {
		coroutineScope.launch {
			database.ricettarioDao().deleteRicetta(ricetta)
		}
	}
}