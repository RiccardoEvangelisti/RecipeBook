package com.projects.android.ricettario.model.enums

enum class Portata(value: String) { ANTIPASTO("Antipasto"),
	PRIMO("Primo"),
	SECONDO("Secondo"),
	CONTORNO("Contorno"),
	DOLCE("Dolce");

	private val valueString: String = value

	override fun toString(): String {
		return valueString
	}
}