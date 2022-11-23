package com.projects.android.recipebook.view.add.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.databinding.FragmentAddSelectRecipeTagBinding
import com.projects.android.recipebook.view.add.AddRecipeViewModel

class AddSelectRecipeTagFragment(var mReadyListener: DialogListener) : DialogFragment() {

	private var listItems = ArrayList<String>()

	// VIEW MODEL
	private val viewModel: AddRecipeViewModel by activityViewModels()

	// VIEW BINDING
	private var _binding: FragmentAddSelectRecipeTagBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	interface DialogListener {
		fun ready(name: String)
		fun cancelled()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = FragmentAddSelectRecipeTagBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			ArrayAdapter(
				requireContext(),
				android.R.layout.simple_list_item_1,
				listItems.also { list -> list.addAll(viewModel.recipes.value.map { it.name }) }).also {
				namesRecipesList.adapter = it
			}

			searchRecipe.doAfterTextChanged { text ->
				val filter = Filters()
				filter.string = text.toString()
				viewModel.getRecipes(filter)
				listItems.clear()
				listItems.addAll(viewModel.recipes.value.map { it.name })
				(namesRecipesList.adapter as ArrayAdapter<String>).notifyDataSetChanged()
			}

			namesRecipesList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
				mReadyListener.ready(namesRecipesList.getItemAtPosition(position).toString())
				this@AddSelectRecipeTagFragment.dismiss()
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	companion object {
		const val TAG = "AddSelectRecipeTagFragment"
	}
}