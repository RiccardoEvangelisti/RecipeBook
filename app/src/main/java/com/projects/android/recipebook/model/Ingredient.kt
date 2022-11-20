package com.projects.android.recipebook.model

import com.projects.android.recipebook.model.enums.UnitOfMeasure

data class Ingredient(var name: String, var quantity: String, var unitOfMeasure: UnitOfMeasure)