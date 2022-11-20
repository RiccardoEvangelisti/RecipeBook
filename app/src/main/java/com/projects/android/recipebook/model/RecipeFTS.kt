package com.projects.android.recipebook.model

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4(contentEntity = Recipe::class)
@Entity(tableName = "recipeBook_fts")
@Suppress("unused")
class RecipeFTS(
	val name: String, val preparation: String, val ingredientsList: List<Ingredient>
) {

	@PrimaryKey
	var rowid: Int = 0
}