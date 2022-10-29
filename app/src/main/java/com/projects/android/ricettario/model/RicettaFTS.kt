package com.projects.android.ricettario.model

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4(contentEntity = Ricetta::class)
@Entity(tableName = "ricettario_fts")
class RicettaFTS(
	val nome: String, val preparazione: String, val ingredientiList: List<Ingrediente>) {

	@PrimaryKey()
	var rowid: Int = 0
}