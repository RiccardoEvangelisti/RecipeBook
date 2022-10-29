package com.projects.android.ricettario.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.projects.android.ricettario.model.Ingrediente
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione

class Converters {

	@TypeConverter
	fun fromPortata(portata: Portata): Int {
		return portata.ordinal
	}

	@TypeConverter
	fun toPortata(portata: Int): Portata {
		return enumValues<Portata>()[portata]
	}

	@TypeConverter
	fun fromTempoPreparazione(tempoPreparazione: TempoPreparazione): Int {
		return tempoPreparazione.ordinal
	}

	@TypeConverter
	fun toTempoPreparazione(tempoPreparazione: Int): TempoPreparazione {
		return enumValues<TempoPreparazione>()[tempoPreparazione]
	}

	@TypeConverter
	fun fromIngredientiList(ingredientiList: List<Ingrediente>): String {
		val gson = Gson()
		val type = object : TypeToken<List<Ingrediente>>() {}.type
		return gson.toJson(ingredientiList, type)
	}

	@TypeConverter
	fun toIngredientiList(ingredientiList: String): List<Ingrediente> {
		val gson = Gson()
		val type = object : TypeToken<List<Ingrediente>>() {}.type
		return gson.fromJson(ingredientiList, type)
	}
}