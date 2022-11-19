package com.projects.android.recipebook.model.enums

enum class PreparationTime(value: String) {
	FIVE_MIN("5 minutes"),
	THIRTY_MIN("30 minutes"),
	ONE_HOUR("1 hour"),
	TWO_HOURS("2 hours"),
	FOUR_HOURS("4 hours"),
	UNLIMITED("Unlimited");

	private val valueString: String = value

	override fun toString(): String {
		return valueString
	}
}
