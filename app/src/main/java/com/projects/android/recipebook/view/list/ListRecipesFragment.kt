package com.projects.android.recipebook.view.list

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.databinding.FragmentListRecipesBinding
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
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

			searchTextList.setOnEditorActionListener { _, actionId, _ ->
				return@setOnEditorActionListener when (actionId) {
					EditorInfo.IME_ACTION_SEARCH -> {
						searchButtonList.callOnClick()
						true
					}

					else -> false
				}
			}

			vegetarianoSwitch.setOnClickListener {
				// se ho spento vegetarianoSwitch ed è già spento nonVegetarianoSwitch
				if (!vegetarianoSwitch.isChecked && !nonVegetarianoSwitch.isChecked) {
					nonVegetarianoSwitch.isChecked = true
				}
			}
			nonVegetarianoSwitch.setOnClickListener {
				// se ho spento nonVegetarianoSwitch ed è già spento vegetarianoSwitch
				if (!nonVegetarianoSwitch.isChecked && !vegetarianoSwitch.isChecked) {
					vegetarianoSwitch.isChecked = true
				}
			}

			serveCotturaSwitch.setOnClickListener {
				if (!serveCotturaSwitch.isChecked && !nonServeCotturaSwitch.isChecked) {
					nonServeCotturaSwitch.isChecked = true
				}
			}
			nonServeCotturaSwitch.setOnClickListener {
				if (!nonServeCotturaSwitch.isChecked && !serveCotturaSwitch.isChecked) {
					serveCotturaSwitch.isChecked = true
				}
			}


			searchButtonList.setOnClickListener {
				val filtro = Filters()

				if (!searchTextList.text.isNullOrBlank()) {
					filtro.string = searchTextList.text.toString()
				}

				filtro.courses = mutableListOf()
				if (!starterFilter.isChecked && !firstFilter.isChecked && !secondoToggleButton.isChecked && !contornoToggleButton.isChecked &&
					!dolceToggleButton.isChecked) {
					starterFilter.toggle()
					firstFilter.toggle()
					secondoToggleButton.toggle()
					contornoToggleButton.toggle()
					dolceToggleButton.toggle()
				}
				if (starterFilter.isChecked) {
					filtro.courses?.add(Course.STARTER)
				}
				if (firstFilter.isChecked) {
					filtro.courses?.add(Course.FIRST)
				}
				if (secondoToggleButton.isChecked) {
					filtro.courses?.add(Course.SECOND)
				}
				if (contornoToggleButton.isChecked) {
					filtro.courses?.add(Course.SIDE)
				}
				if (dolceToggleButton.isChecked) {
					filtro.courses?.add(Course.DESSERT)
				}

				filtro.preparationTime = when (tempoPrepRadioGroup.checkedRadioButtonId) {
					R.id.cinqueMinRadioButton -> PreparationTime.FIVE_MIN
					R.id.trentaMinRadioButton -> PreparationTime.THIRTY_MIN
					R.id.unaOraRadioButton -> PreparationTime.ONE_HOUR
					R.id.dueOreRadioButton -> PreparationTime.TWO_HOURS
					R.id.quattroOreRadioButton -> PreparationTime.FOUR_HOURS
					R.id.oltreRadioButton -> PreparationTime.UNLIMITED
					else -> null
				}

				if (!(vegetarianoSwitch.isChecked && nonVegetarianoSwitch.isChecked)) {
					filtro.isVegetarian = vegetarianoSwitch.isChecked
				}

				if (!(serveCotturaSwitch.isChecked && nonServeCotturaSwitch.isChecked)) {
					filtro.isCooked = serveCotturaSwitch.isChecked
				}

				listRecipesViewModel.getRicette(filtro)
			}

			dropFiltri.setOnClickListener {
				if (filtersGroup.visibility == VISIBLE) {
					filtersGroup.visibility = GONE
					dropFiltri.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_keyboard_arrow_down_24))
				} else {
					filtersGroup.visibility = VISIBLE
					dropFiltri.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_keyboard_arrow_up_24))
				}
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				listRecipesViewModel.ricette.collect { ricette ->
					binding.ricetteRecyclerView.adapter = RicetteListAdapter(ricette) { ricettaID ->
						findNavController().navigate(ListRecipesFragmentDirections.fromListaToDettaglio(ricettaID))
					}
					// Separatore tra gli item
					binding.ricetteRecyclerView.addItemDecoration(DividerItemDecoration(context, VERTICAL))
				}
			}
		}

		binding.addRecipeFAB.setOnClickListener {
			findNavController().navigate(R.id.from_lista_to_aggiungi)
		}
	}

	// VIEW BINDING
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}