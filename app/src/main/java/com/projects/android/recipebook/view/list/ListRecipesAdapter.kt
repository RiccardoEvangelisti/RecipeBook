package com.projects.android.recipebook.view.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.projects.android.recipebook.R
import com.projects.android.recipebook.databinding.ItemListRecipesBinding
import com.projects.android.recipebook.model.Recipe

class ListRecipesAdapter(private val recipes: List<Recipe>, private val context: Context, private val onRecipeClicked: (recipeID: Int) -> Unit) :
	RecyclerView.Adapter<ListHolderRecipes>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolderRecipes {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemListRecipesBinding.inflate(inflater, parent, false)
		return ListHolderRecipes(binding, context)
	}

	override fun onBindViewHolder(holder: ListHolderRecipes, position: Int) {
		val recipe = recipes[position]
		holder.bind(recipe, onRecipeClicked)
	}

	override fun getItemCount(): Int {
		return recipes.size
	}
}

class ListHolderRecipes(private val binding: ItemListRecipesBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

	fun bind(recipe: Recipe, onRecipeClicked: (recipeID: Int) -> Unit) {

		binding.apply {
			root.setOnClickListener {
				onRecipeClicked(recipe.id)
			}

			nameItemList.text = recipe.name
			courseItemList.text = recipe.course.toString()
			preparationTimeItemList.text = recipe.preparationTime.toString()
			isVegetarianItemList.setImageDrawable(
				AppCompatResources.getDrawable(context, if (recipe.isVegetarian) R.drawable.is_veg else R.drawable.is_not_veg)
			)
			isCookedItemList.setImageDrawable(
				AppCompatResources.getDrawable(context, if (recipe.isCooked) R.drawable.is_cooked else R.drawable.is_not_cooked)
			)
		}
	}
}