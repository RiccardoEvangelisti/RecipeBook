package com.projects.android.ricettario.database

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.projects.android.ricettario.model.Ricetta

@Dao
interface RicettarioDao {

	@Query("SELECT * FROM ricettario WHERE rowid=(:id)")
	fun getRicettaSingola(id: Int): Ricetta

	@RawQuery
	fun getRicette(query: SimpleSQLiteQuery): List<Ricetta>

	fun getRicette(filters: Filters): List<Ricetta> {
		var query = "SELECT rowid, * FROM ricettario_fts JOIN ricettario ON rowid = docid WHERE 1 == 1"
		val args = mutableListOf<String>()
		with(filters) {
			if (!string.isNullOrBlank()) {
				query += " "
				query += "AND ricettario_fts MATCH ?"
				args.add(string!!)
			}
			portata?.let {
				query += " "
				query += "AND ricettario.portata == ?"
				args.add(it.ordinal.toString())
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
	fun insertRicetta(ricetta: Ricetta)

	@Delete
	fun deleteRicetta(ricetta: Ricetta)

	@Update
	fun updateRicetta(ricetta: Ricetta)
}