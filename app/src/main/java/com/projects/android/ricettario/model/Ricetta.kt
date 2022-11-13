package com.projects.android.ricettario.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione

@Entity(tableName = "ricettario")
data class Ricetta(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "rowid")
	var id: Int = 0,
	var nome: String,
	val portata: Portata,
	val porzioni: Int,
	val preparazione: String,
	val ingredientiList: MutableList<Ingrediente>,
	val isVegetariana: Boolean,
	val tempoPreparazione: TempoPreparazione,
	val serveCottura: Boolean)