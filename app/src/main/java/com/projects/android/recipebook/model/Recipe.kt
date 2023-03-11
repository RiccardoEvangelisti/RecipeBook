package com.projects.android.recipebook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime

@Entity(tableName = "recipeBook")
data class Recipe(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var id: Int = 0,
	var name: String,
	val course: Course,
	val portions: String,
	val preparation: String,
	val ingredientsList: MutableList<Ingredient>,
	val isVeg: Boolean,
	val preparationTime: PreparationTime,
	val isCooked: Boolean,
	val pictureFileName: String? = null
)