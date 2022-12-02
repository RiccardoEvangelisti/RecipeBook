package com.projects.android.recipebook.view.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.projects.android.recipebook.R
import com.projects.android.recipebook.databinding.ItemListRecipesBinding
import com.projects.android.recipebook.model.Recipe

class RicetteListAdapter(private val ricette: List<Recipe>, private val context: Context, private val onRicettaClicked: (ricettaID: Int) -> Unit) :
	RecyclerView.Adapter<RicetteListHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RicetteListHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemListRecipesBinding.inflate(inflater, parent, false)
		return RicetteListHolder(binding, context)
	}

	override fun onBindViewHolder(holder: RicetteListHolder, position: Int) {
		val ricetta = ricette[position]
		holder.bind(ricetta, onRicettaClicked)
	}

	override fun getItemCount(): Int {
		return ricette.size
	}
}

class RicetteListHolder(private val binding: ItemListRecipesBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

	fun bind(recipe: Recipe, onRicettaClicked: (ricettaID: Int) -> Unit) {

		binding.apply {
			root.setOnClickListener {
				onRicettaClicked(recipe.id)
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