package com.projects.android.recipebook.view.list

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.android.recipebook.R
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
		binding.ricetteRecyclerView.layoutManager = LinearLayoutManager(context)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			dropFiltri.setOnClickListener {
				if (filtersLayout.visibility == VISIBLE) {
					filtersLayout.visibility = GONE
					dropFiltri.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_keyboard_arrow_down_24))
				} else {
					filtersLayout.visibility = VISIBLE
					dropFiltri.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_keyboard_arrow_up_24))
				}
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				listRecipesViewModel.recipes.collect { recipes ->
					binding.ricetteRecyclerView.adapter = RicetteListAdapter(recipes) { ricettaID ->
						findNavController().navigate(ListRecipesFragmentDirections.fromListRecipesFragmentToSingleRecipeFragment(ricettaID))
					}
					// Separatore tra gli item
					binding.ricetteRecyclerView.addItemDecoration(DividerItemDecoration(context, VERTICAL))
				}
			}
		}

		binding.addRecipeFAB.setOnClickListener {
			findNavController().navigate(ListRecipesFragmentDirections.fromListRecipesFragmentToAddRecipeFragment())
		}
	}

	// VIEW BINDING
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}