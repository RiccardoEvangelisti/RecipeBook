package com.projects.android.recipebook.database

import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime

data class Filters(
	var string: String? = null,
	var courses: MutableList<Course>? = null,
	var isVeg: Boolean? = null,
	var preparationTime: PreparationTime? = null,
	var isCooked: Boolean? = null
)