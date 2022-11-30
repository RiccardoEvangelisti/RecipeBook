package com.projects.android.recipebook.view.add.tag

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.databinding.FragmentAddSelectRecipeTagBinding
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.view.add.AddRecipeViewModel
import kotlinx.coroutines.launch

class AddSelectRecipeTagFragment(private var listener: DialogListener) : DialogFragment() {

	private var listItems = ArrayList<String>()

	// VIEW MODEL
	private val viewModel: AddRecipeViewModel by viewModels(ownerProducer = { requireParentFragment() })

	// VIEW BINDING
	private var _binding: FragmentAddSelectRecipeTagBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	interface DialogListener {
		fun ready(recipe: Recipe)
		fun cancelled()
	}

	override fun onCancel(dialog: DialogInterface) {
		super.onCancel(dialog)
		listener.cancelled()
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
				requireContext(), android.R.layout.simple_list_item_1, listItems
			).also {
				namesRecipesList.adapter = it
			}

			searchRecipe.doAfterTextChanged { text ->
				viewModel.getRecipes(Filters().also { it.string = text.toString() })
			}

			namesRecipesList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
				listener.ready(viewModel.recipes.value!![position])
				this@AddSelectRecipeTagFragment.dismiss()
			}

			viewLifecycleOwner.lifecycleScope.launch {
				viewModel.getRecipes(Filters())
				viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
					viewModel.recipes.collect { recipes ->
						recipes?.let {
							binding.apply {
								listItems.clear()
								listItems.addAll(recipes.map { it.name })
								(namesRecipesList.adapter as ArrayAdapter<*>).notifyDataSetChanged()
							}
						}
					}
				}
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