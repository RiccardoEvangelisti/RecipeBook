package com.projects.android.recipebook.database

import android.content.Context
import androidx.room.Room
import com.projects.android.recipebook.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "recipebook-database"

class RecipeBookRepository private constructor(context: Context, private val coroutineScope: CoroutineScope = GlobalScope) {

	companion object {// Singleton

		private var INSTANCE: RecipeBookRepository? = null
		fun initialize(context: Context) {
			if (INSTANCE == null) {
				INSTANCE = RecipeBookRepository(context)
			}
		}

		fun get(): RecipeBookRepository {
			return INSTANCE ?: throw IllegalStateException("RecipeBookRepository must be initialized")
		}
	}

	private val database: RecipeBookDatabase = Room.databaseBuilder(context.applicationContext, RecipeBookDatabase::class.java, DATABASE_NAME).build()

	fun getRecipes(filters: Filters): Flow<List<Recipe>> {
		return database.recipeBookDao().getRecipes(filters)
	}

	fun getSingleRecipe(id: Int): Flow<Recipe> {
		return database.recipeBookDao().getSingleRecipe(id)
	}

	fun insertRecipe(recipe: Recipe) {
		coroutineScope.launch {
			database.recipeBookDao().insertRecipe(recipe)
		}
	}

	fun updateRecipe(recipe: Recipe) {
		coroutineScope.launch {
			database.recipeBookDao().updateRecipe(recipe)
		}
	}

	fun deleteRecipe(recipe: Recipe) {
		coroutineScope.launch {
			database.recipeBookDao().deleteRecipe(recipe)
		}
	}
}