package com.projects.android.recipebook.view.add.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.view.add.AddRecipeViewModel
import com.projects.android.recipebook.databinding.FragmentAddSelectRecipeTagBinding

class AddSelectRecipeTagFragment(var mReadyListener: DialogListener) : DialogFragment() {

	var listItems = ArrayList<String>()

	// VIEW MODEL
	private val viewModel: AddRecipeViewModel by viewModels()

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
				requireContext(), android.R.layout.simple_list_item_1, Course.values()
			).also { adapter ->
				namesRecipesList.adapter = adapter
			}

			searchRecipe.doOnTextChanged { text, _, _, _ ->
				val filter = Filters()
				filter.string = text.toString()
				viewModel.getRecipes(filter)
				listItems.addAll(viewModel.recipes.value.map { it.name })
			}

			namesRecipesList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
				mReadyListener.ready((namesRecipesList.getItemAtPosition(position) as Course).toString())
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