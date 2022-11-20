package com.projects.android.recipebook.model.enums

enum class Course(value: String) {
	STARTER("Starter"), FIRST("First"), SECOND("Second"), SIDE("Side"), DESSERT("Dessert");

	private val valueString: String = value

	override fun toString(): String {
		return valueString
	}
}