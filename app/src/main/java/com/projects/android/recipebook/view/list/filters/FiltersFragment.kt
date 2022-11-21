package com.projects.android.recipebook.view.list.filters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.projects.android.recipebook.R
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.databinding.FragmentFiltersBinding
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.view.list.ListRecipesViewModel

class FiltersFragment : Fragment() {

	// VIEW MODEL: It uses the parent (ListRecipesFragment) scope for viewModel to get the same instance
	private val listRecipesViewModel: ListRecipesViewModel by viewModels({ requireParentFragment() })

	// VIEW BINDING
	private var _binding: FragmentFiltersBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = FragmentFiltersBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			searchTextList.setOnEditorActionListener { _, actionId, _ ->
				return@setOnEditorActionListener when (actionId) {
					EditorInfo.IME_ACTION_SEARCH -> {
						searchButtonList.performClick()
						true
					}
					else -> false
				}
			}

			searchButtonList.setOnClickListener {
				val filtro = Filters()

				if (!searchTextList.text.isNullOrBlank()) {
					filtro.string = searchTextList.text.toString()
				}

				filtro.courses = mutableListOf()
				if (!starterFilter.isChecked && !firstFilter.isChecked && !secondFilter.isChecked && !sideFilter.isChecked && !dolceToggleButton.isChecked) {
					starterFilter.toggle()
					firstFilter.toggle()
					secondFilter.toggle()
					sideFilter.toggle()
					dolceToggleButton.toggle()
				}
				if (starterFilter.isChecked) {
					filtro.courses?.add(Course.STARTER)
				}
				if (firstFilter.isChecked) {
					filtro.courses?.add(Course.FIRST)
				}
				if (secondFilter.isChecked) {
					filtro.courses?.add(Course.SECOND)
				}
				if (sideFilter.isChecked) {
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

				activity?.currentFocus?.let { view ->
					val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
					imm?.hideSoftInputFromWindow(view.windowToken, 0)
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
		}
	}

	// VIEW BINDING
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}