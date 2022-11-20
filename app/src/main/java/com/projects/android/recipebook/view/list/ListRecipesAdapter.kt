package com.projects.android.recipebook.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.android.recipebook.databinding.ItemListRecipesBinding
import com.projects.android.recipebook.model.Recipe

class RicetteListAdapter(private val ricette: List<Recipe>, private val onRicettaClicked: (ricettaID: Int) -> Unit) :
	RecyclerView.Adapter<RicetteListHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RicetteListHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemListRecipesBinding.inflate(inflater, parent, false)
		return RicetteListHolder(binding)
	}

	override fun onBindViewHolder(holder: RicetteListHolder, position: Int) {
		val ricetta = ricette[position]
		holder.bind(ricetta, onRicettaClicked)
	}

	override fun getItemCount(): Int {
		return ricette.size
	}
}

class RicetteListHolder(private val binding: ItemListRecipesBinding) : RecyclerView.ViewHolder(binding.root) {

	fun bind(recipe: Recipe, onRicettaClicked: (ricettaID: Int) -> Unit) {

		binding.apply {
			root.setOnClickListener {
				onRicettaClicked(recipe.id)
			}

			nameItemList.text = recipe.name
			courseItemList.text = recipe.course.toString()
			preparationTimeItemList.text = recipe.preparationTime.toString()
			isVegetarianItemList.text = if (recipe.isVegetarian) "VEG" else "ONN"
			isCookedItemList.text = if (recipe.isCooked) "COTTO" else "CRUDO"
		}
	}
}