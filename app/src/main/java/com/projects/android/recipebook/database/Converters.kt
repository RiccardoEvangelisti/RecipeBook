package com.projects.android.recipebook.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.Preparation
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime

class Converters {

	@TypeConverter
	fun fromCourse(course: Course): Int {
		return course.ordinal
	}

	@TypeConverter
	fun toCourse(i: Int): Course {
		return enumValues<Course>()[i]
	}

	@TypeConverter
	fun fromPreparationTime(preparationTime: PreparationTime): Int {
		return preparationTime.ordinal
	}

	@TypeConverter
	fun toPreparationTime(preparationTime: Int): PreparationTime {
		return enumValues<PreparationTime>()[preparationTime]
	}

	@TypeConverter
	fun fromIngredientsList(ingredientsList: List<Ingredient>): String {
		val gson = Gson()
		val type = object : TypeToken<List<Ingredient>>() {}.type
		return gson.toJson(ingredientsList, type)
	}

	@TypeConverter
	fun toIngredientsList(ingredientsList: String): List<Ingredient> {
		val gson = Gson()
		val type = object : TypeToken<List<Ingredient>>() {}.type
		return gson.fromJson(ingredientsList, type)
	}

	@TypeConverter
	fun fromPreparation(preparation: Preparation): String {
		val gson = Gson()
		val type = object : TypeToken<Preparation>() {}.type
		return gson.toJson(preparation, type)
	}

	@TypeConverter
	fun toPreparation(preparation: String): Preparation {
		val gson = Gson()
		val type = object : TypeToken<Preparation>() {}.type
		return gson.fromJson(preparation, type)
	}
}