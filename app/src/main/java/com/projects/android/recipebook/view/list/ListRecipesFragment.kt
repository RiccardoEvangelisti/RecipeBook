package com.projects.android.recipebook.view.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.android.recipebook.databinding.FragmentListRecipesBinding
import kotlinx.coroutines.launch

class ListRecipesFragment : Fragment() {

	// VIEW MODEL
	private val listRecipesViewModel: ListRecipesViewModel by viewModels()

	// VIEW BINDING
	private var _binding: FragmentListRecipesBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {

		// RECYCLER VIEW
		_binding = FragmentListRecipesBinding.inflate(inflater, container, false)
		binding.recipesRecyclerViewList.layoutManager = LinearLayoutManager(context)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// APPBAR: MENU
		setupMenu()

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				listRecipesViewModel.recipes.collect { recipes ->
					binding.recipesRecyclerViewList.adapter = ListRecipesAdapter(recipes, requireContext()) { recipeID ->
						findNavController().navigate(ListRecipesFragmentDirections.fromListRecipesFragmentToSingleRecipeFragment(recipeID))
					}
					// Separator between items
					binding.recipesRecyclerViewList.addItemDecoration(DividerItemDecoration(context, VERTICAL))
				}
			}
		}

		binding.addRecipeFABList.setOnClickListener {
			findNavController().navigate(ListRecipesFragmentDirections.fromListRecipesFragmentToAddRecipeFragment(-2))
		}
	}

	// VIEW BINDING
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	// APPBAR: MENU
	private fun setupMenu() {
		val appBarConfiguration = AppBarConfiguration(findNavController().graph, binding.drawerLayoutList)
		binding.toolbarList.setupWithNavController(findNavController(), appBarConfiguration)
	}
}