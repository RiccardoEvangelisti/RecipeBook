package com.projects.android.recipebook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.RecipeFTS

@Database(entities = [Recipe::class, RecipeFTS::class], version = 1)
@TypeConverters(Converters::class)
abstract class RecipeBookDatabase : RoomDatabase() {

	abstract fun recipeBookDao(): RecipeBookDao
}