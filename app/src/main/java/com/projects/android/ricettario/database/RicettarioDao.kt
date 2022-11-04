package com.projects.android.ricettario.database

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.projects.android.ricettario.model.Ricetta
import kotlinx.coroutines.flow.Flow

@Dao
interface RicettarioDao {

	@Query("SELECT rowid, * FROM ricettario WHERE rowid=(:id)")
	suspend fun getRicettaSingola(id: Int): Ricetta

	@RawQuery(observedEntities = [Ricetta::class])
	fun getRicette(query: SimpleSQLiteQuery): Flow<List<Ricetta>>

	fun getRicette(filters: Filters): Flow<List<Ricetta>> {
		var query = "SELECT rowid, * FROM ricettario_fts JOIN ricettario ON rowid = docid WHERE 1 == 1"
		val args = mutableListOf<String>()
		with(filters) {
			if (!string.isNullOrBlank()) {
				query += " "
				query += "AND ricettario_fts MATCH ?"
				args.add(string!!)
			}
			portate?.let {
				if (it.isNotEmpty()) {
					query += " "
					query += "AND ricettario.portata IN ("
					for (i in 1..it.size) {
						query += "?"
						if (i != it.size) {
							query += ", "
						}
					}
					query += ")"
					it.forEach { portata ->
						args.add(portata.ordinal.toString())
					}
				}
			}
			isVegetariana?.let {
				query += " "
				query += "AND ricettario.isVegetariana IS $isVegetariana"
			}
			tempoPreparazione?.let {
				query += " "
				query += "AND ricettario.tempoPreparazione <= ?"
				args.add(it.toString())
			}
			serveCottura?.let {
				query += " "
				query += "AND ricettario.serveCottura IS $serveCottura"
			}
		}
		val simpleSQLiteQuery = SimpleSQLiteQuery(query, args.toTypedArray())
		return getRicette(simpleSQLiteQuery)
	}

	@Insert
	suspend fun insertRicetta(ricetta: Ricetta)

	@Delete
	fun deleteRicetta(ricetta: Ricetta)

	@Update
	fun updateRicetta(ricetta: Ricetta)
}