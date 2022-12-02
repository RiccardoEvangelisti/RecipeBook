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
			stringFilter.setOnEditorActionListener { _, actionId, _ ->
				return@setOnEditorActionListener when (actionId) {
					EditorInfo.IME_ACTION_SEARCH -> {
						searchButtonFilter.performClick()
						true
					}
					else -> false
				}
			}

			searchButtonFilter.setOnClickListener {
				val filter = Filters()

				if (!stringFilter.text.isNullOrBlank()) {
					filter.string = stringFilter.text.toString()
				}

				filter.courses = mutableListOf()
				if (!starterFilter.isChecked && !firstFilter.isChecked && !secondFilter.isChecked && !sideFilter.isChecked && !dessertFilter.isChecked) {
					starterFilter.toggle()
					firstFilter.toggle()
					secondFilter.toggle()
					sideFilter.toggle()
					dessertFilter.toggle()
				}
				if (starterFilter.isChecked) {
					filter.courses?.add(Course.STARTER)
				}
				if (firstFilter.isChecked) {
					filter.courses?.add(Course.FIRST)
				}
				if (secondFilter.isChecked) {
					filter.courses?.add(Course.SECOND)
				}
				if (sideFilter.isChecked) {
					filter.courses?.add(Course.SIDE)
				}
				if (dessertFilter.isChecked) {
					filter.courses?.add(Course.DESSERT)
				}

				filter.preparationTime = when (preparationTimeFilter.checkedRadioButtonId) {
					R.id.five_min_filter -> PreparationTime.FIVE_MIN
					R.id.thirty_min_filter -> PreparationTime.THIRTY_MIN
					R.id.one_hour_filter -> PreparationTime.ONE_HOUR
					R.id.two_hours_filter -> PreparationTime.TWO_HOURS
					R.id.four_hours_filter -> PreparationTime.FOUR_HOURS
					R.id.unlimited_filter -> PreparationTime.UNLIMITED
					else -> null
				}

				if (!(isVegFilter.isChecked && isNotVegFilter.isChecked)) {
					filter.isVeg = isVegFilter.isChecked
				}

				if (!(isCookedFilter.isChecked && isNotCookedFilter.isChecked)) {
					filter.isCooked = isCookedFilter.isChecked
				}

				listRecipesViewModel.getRecipes(filter)

				activity?.currentFocus?.let { view ->
					val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
					imm?.hideSoftInputFromWindow(view.windowToken, 0)
				}
			}

			isVegFilter.setOnClickListener {
				if (!isVegFilter.isChecked && !isNotVegFilter.isChecked) {
					isNotVegFilter.isChecked = true
				}
			}
			isNotVegFilter.setOnClickListener {
				if (!isNotVegFilter.isChecked && !isVegFilter.isChecked) {
					isVegFilter.isChecked = true
				}
			}

			isCookedFilter.setOnClickListener {
				if (!isCookedFilter.isChecked && !isNotCookedFilter.isChecked) {
					isNotCookedFilter.isChecked = true
				}
			}
			isNotCookedFilter.setOnClickListener {
				if (!isNotCookedFilter.isChecked && !isCookedFilter.isChecked) {
					isCookedFilter.isChecked = true
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