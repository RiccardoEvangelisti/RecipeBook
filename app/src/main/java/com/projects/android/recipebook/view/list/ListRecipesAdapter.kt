package com.projects.android.recipebook.view.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.projects.android.recipebook.R
import com.projects.android.recipebook.databinding.ItemListRecipeBinding
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course

class ListRecipesAdapter(private val recipes: List<Recipe>, private val context: Context, private val onRecipeClicked: (recipeID: Int) -> Unit) :
	RecyclerView.Adapter<ListHolderRecipes>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolderRecipes {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemListRecipeBinding.inflate(inflater, parent, false)
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

class ListHolderRecipes(private val binding: ItemListRecipeBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

	fun bind(recipe: Recipe, onRecipeClicked: (recipeID: Int) -> Unit) {

		binding.apply {
			root.setOnClickListener {
				onRecipeClicked(recipe.id)
			}

			nameItemList.text = recipe.name
			courseItemList.setImageDrawable(
				AppCompatResources.getDrawable(
					context, when (recipe.course) {
						Course.STARTER -> R.drawable.starter
						Course.FIRST -> R.drawable.first
						Course.SECOND -> R.drawable.second
						Course.SIDE -> R.drawable.side
						else -> R.drawable.dessert
					}
				)
			)
			preparationTimeItemList.text = recipe.preparationTime.toString()
			isVegItemList.setImageDrawable(
				AppCompatResources.getDrawable(context, if (recipe.isVeg) R.drawable.is_veg else R.drawable.is_not_veg)
			)
			isCookedItemList.setImageDrawable(
				AppCompatResources.getDrawable(context, if (recipe.isCooked) R.drawable.is_cooked else R.drawable.is_not_cooked)
			)
		}
	}
}