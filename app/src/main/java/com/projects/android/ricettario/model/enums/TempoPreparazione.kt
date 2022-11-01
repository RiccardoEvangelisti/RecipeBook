package com.projects.android.ricettario.model.enums

enum class TempoPreparazione(value: String) { CINQUE_MIN("5 minuti"),
	TRENTA_MIN("30 minuti"),
	UN_ORA("1 ora"),
	DUE_ORE("2 ore"),
	QUATTRO_ORE("4 ore"),
	ILLIMITATO_TEMPO("Oltre");

	private val valueString: String = value

	override fun toString(): String {
		return valueString
	}
}
