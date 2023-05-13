package com.projects.android.recipebook.view.add

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.view.*
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.projects.android.recipebook.R
import com.projects.android.recipebook.databinding.FragmentAddRecipeBinding
import com.projects.android.recipebook.databinding.ItemAddIngredientBinding
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import com.projects.android.recipebook.utils.ErrorUtil
import com.projects.android.recipebook.utils.PictureUtils
import com.projects.android.recipebook.view.add.utils.AddRecipeCheckErrors
import kotlinx.coroutines.launch
import java.util.*

class AddRecipeFragment : Fragment() {

	private var container: ViewGroup? = null

	// VIEW MODEL
	private val addRecipeViewModel: AddRecipeViewModel by viewModels {
		AddRecipeViewModelFactory(args.recipeID)
	}

	// SAFE ARGS
	private val args: AddRecipeFragmentArgs by navArgs()

	// VIEW BINDING
	private var _binding: FragmentAddRecipeBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	private var _bindingIngredientsList = mutableListOf<ItemAddIngredientBinding?>()

	// Variables for taking pictures
	private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { didTakePicture: Boolean ->
		if (didTakePicture && addRecipeViewModel.state.value?.pictureFileNameTemp != null) {
			PictureUtils.getCachedPicture(requireContext(), addRecipeViewModel.state.value?.pictureFileNameTemp!!).also {
				if (it.exists()) {
					addRecipeViewModel.updateState { state ->
						state.pictureFileNamePrevious = state.pictureFileName
						state.pictureFileName = it.name
					}
				} else {
					addRecipeViewModel.updateState { state -> state.pictureFileNameTemp = null }
					ErrorUtil.shortToast(requireContext(), "Failure to take picture")
				}
			}
		} else {
			ErrorUtil.shortToast(requireContext(), "Failure to take picture")
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		this.container = container
		_binding = FragmentAddRecipeBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// APPBAR: MENU
		setupMenu()

		binding.apply {

			binding.toolbarAdd.setNavigationOnClickListener {
				AlertDialog.Builder(requireContext()).setTitle("Confirm to Exit?").setIcon(R.drawable.ic_baseline_dangerous_24)
					.setMessage("The Recipe will be discarded").setPositiveButton(android.R.string.ok) { _, _ ->
						addRecipeViewModel.state.value?.canceled = true
						addRecipeViewModel.cancelInsertRecipe(requireContext())
						findNavController().navigateUp()
					}.setNegativeButton(android.R.string.cancel, null).show()
			}

			// Conditions for navigateUp
			activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
				AlertDialog.Builder(requireContext()).setTitle("Confirm to Exit?").setIcon(R.drawable.ic_baseline_dangerous_24)
					.setMessage("The Recipe will be discarded").setPositiveButton(android.R.string.ok) { _, _ ->
						addRecipeViewModel.state.value?.canceled = true
						addRecipeViewModel.cancelInsertRecipe(requireContext())
						findNavController().navigateUp()
					}.setNegativeButton(android.R.string.cancel, null).show()

			}

			// ERRORS HANDLERS
			nameAdd.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
				if (!hasFocus) AddRecipeCheckErrors.checkName(nameLayoutAdd, addRecipeViewModel.state.value?.name)
			}
			portionsAdd.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
				if (!hasFocus) AddRecipeCheckErrors.checkPortions(
					portionsLayoutAdd, addRecipeViewModel.state.value?.portions
				)
			}
			preparationAdd.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
				if (!hasFocus) AddRecipeCheckErrors.checkPreparation(preparationLayoutAdd, addRecipeViewModel.state.value?.preparation)
			}

			// Initializations
			requireContext().let {
				// Initialization spinner Course
				ArrayAdapter(
					it, android.R.layout.simple_spinner_dropdown_item, Course.values()
				).also { adapter ->
					(courseAdd.editText as AutoCompleteTextView).setAdapter(adapter)
				}
				// Initialization spinner PreparationTime
				ArrayAdapter(
					it, android.R.layout.simple_spinner_dropdown_item, PreparationTime.values()
				).also { adapter ->
					(preparationTimeAdd.editText as AutoCompleteTextView).setAdapter(adapter)
				}
				// Initialization spinner UnitOfMeasure
				ArrayAdapter(
					it, android.R.layout.simple_spinner_dropdown_item, UnitOfMeasure.values()
				).also { adapter ->
					(unitIngredientAdd.editText as AutoCompleteTextView).setAdapter(adapter)
				}
			}

			// Listeners UI->ViewModel (apply the changes to ViewModel when change the data on UI)
			nameAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateState { it.name = text.toString() }
			}

			takePictureAdd.isEnabled = canResolveIntent(takePicture.contract.createIntent(requireContext(), Uri.EMPTY))
			takePictureAdd.setOnClickListener {
				PictureUtils.createTempPicture(requireContext()).also {
					addRecipeViewModel.updateState { state ->
						state.pictureFileNameTemp = it.name
					}
					takePicture.launch(PictureUtils.getUriForFile(requireContext(), it))
				}
			}

			isVegAdd.setOnCheckedChangeListener { _, b ->
				addRecipeViewModel.updateState { it.isVeg = b }
			}
			isCookedAdd.setOnCheckedChangeListener { _, b ->
				addRecipeViewModel.updateState { it.isCooked = b }
			}

			(courseAdd.editText as AutoCompleteTextView).onItemClickListener = OnItemClickListener { _, _, position, _ ->
				addRecipeViewModel.updateState {
					it.course = Course.values()[position]
				}
			}

			(preparationTimeAdd.editText as AutoCompleteTextView).onItemClickListener = OnItemClickListener { _, _, position, _ ->
				addRecipeViewModel.updateState {
					it.preparationTime = PreparationTime.values()[position]
				}
			}

			portionsAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateState {
					it.portions = text.toString()
				}
			}

			(unitIngredientAdd.editText as AutoCompleteTextView).onItemClickListener = OnItemClickListener { _, _, position, _ ->
				// Manage UnitOfMeasure.TO_TASTE
				if (position == UnitOfMeasure.TO_TASTE.ordinal) {
					quantityIngredientAdd.setText("")
					quantityIngredientAdd.visibility = GONE
				} else {
					quantityIngredientAdd.visibility = VISIBLE
				}
				addRecipeViewModel.updateState {
					it.unitIngredient = UnitOfMeasure.values()[position]
				}
				nameIngredientAdd.requestFocus()
			}

			nameIngredientAdd.setOnEditorActionListener { _, actionId, _ ->
				return@setOnEditorActionListener when (actionId) {
					EditorInfo.IME_ACTION_DONE -> {
						// If nameIngredientAdd is present and (quantityIngredientAdd is present or unitIngredientAdd==TO_TASTE)
						if ((!quantityIngredientAdd.text.isNullOrBlank() || addRecipeViewModel.state.value?.unitIngredient == UnitOfMeasure.TO_TASTE) && !nameIngredientAdd.text.isNullOrBlank()) {
							addIngredient(true)
						}
						true
					}

					else -> false
				}
			}

			preparationAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateState {
					it.preparation = text.toString()
				}
			}
		}

		// ViewModel->UI (Collect the ViewModel StateFlow and with it update the UI)
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				addRecipeViewModel.state.collect { state ->
					state?.let {
						binding.apply {
							state.name?.let {
								if (nameAdd.text.toString() != state.name) { // it prevents an infinite-loop with the listener
									nameAdd.setText(state.name)
								}
							}
							state.isVeg?.let {
								isVegAdd.isChecked = state.isVeg!!
							}
							state.isCooked?.let {
								isCookedAdd.isChecked = state.isCooked!!
							}
							state.course?.let {
								(courseAdd.editText as AutoCompleteTextView).setText(state.course.toString(), false)
							}
							state.preparationTime?.let {
								(preparationTimeAdd.editText as AutoCompleteTextView).setText(state.preparationTime.toString(), false)
							}
							state.portions?.let {
								if (portionsAdd.text.toString() != state.portions.toString()) {
									portionsAdd.setText(state.portions.toString())
								}
							}
							state.preparation?.let {
								if (preparationAdd.text.toString() != state.preparation!!) {
									preparationAdd.setText(state.preparation!!)
								}
							}
							state.unitIngredient?.let {
								(unitIngredientAdd.editText as AutoCompleteTextView).setText(state.unitIngredient.toString(), false)
							}
							state.ingredientsList?.let {
								// if the UI is different from state
								if (_bindingIngredientsList.size != state.ingredientsList!!.size) {
									// clear layout of all ingredients
									for (i in _bindingIngredientsList.indices) {
										_bindingIngredientsList[i] = null
									}
									// set new values and insert new ingredient
									for (ingredient in state.ingredientsList!!) {
										quantityIngredientAdd.setText(ingredient.quantity)
										nameIngredientAdd.setText(ingredient.name)
										(unitIngredientAdd.editText as AutoCompleteTextView).setText(ingredient.unitOfMeasure.toString(), false)
										addIngredient(false)
									}
								} else {
									for (i in _bindingIngredientsList.indices) {
										_bindingIngredientsList[i]!!.apply {
											val ingredient = state.ingredientsList!![i]
											if (nameIngredientItemAdd.text.toString() != ingredient.name) {
												nameIngredientItemAdd.setText(ingredient.name)
											}
											if (quantityIngredientItemAdd.text.toString() != ingredient.quantity) {
												quantityIngredientItemAdd.setText(ingredient.quantity)
											}
											if ((unitIngredientItemAdd.editText as AutoCompleteTextView).text.toString() != ingredient.unitOfMeasure.toString()) {
												(unitIngredientItemAdd.editText as AutoCompleteTextView).setText(
													ingredient.unitOfMeasure.toString(), false
												)
											}
										}
									}
								}
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
		for (i in _bindingIngredientsList.indices) {
			_bindingIngredientsList[i] = null
		}
	}

	private fun addIngredient(toInsert: Boolean) {
		binding.apply {
			// Create the binding
			val bindingIngredients = ItemAddIngredientBinding.inflate(layoutInflater)
			_bindingIngredientsList.add(bindingIngredients)
			bindingIngredients.apply {
				// Initialization spinner UnitOfMeasure
				requireContext().let {
					ArrayAdapter(
						it, android.R.layout.simple_spinner_dropdown_item, UnitOfMeasure.values()
					).also { adapter ->
						(unitIngredientItemAdd.editText as AutoCompleteTextView).setAdapter(adapter)
					}
				}

				// Filling UI with new ingredient
				quantityIngredientItemAdd.text = quantityIngredientAdd.text
				(unitIngredientItemAdd.editText as AutoCompleteTextView).setText(
					(unitIngredientAdd.editText as AutoCompleteTextView).text.toString(), false
				)
				if ((unitIngredientItemAdd.editText as AutoCompleteTextView).text.toString() == UnitOfMeasure.TO_TASTE.toString()) {
					quantityIngredientItemAdd.visibility = GONE
				}
				nameIngredientItemAdd.text = nameIngredientAdd.text

				// ERROR HANDLERS
				quantityIngredientItemAdd.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
					if (!hasFocus) AddRecipeCheckErrors.checkQuantityIngredientItem(
						quantityIngredientItemLayoutAdd,
						quantityIngredientItemAdd.text.toString(),
						UnitOfMeasure.of((unitIngredientItemAdd.editText as AutoCompleteTextView).text.toString())!!
					)
				}
				nameIngredientItemAdd.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
					if (!hasFocus) AddRecipeCheckErrors.checkNameIngredientItem(
						nameIngredientItemLayoutAdd, nameIngredientItemAdd.text.toString()
					)
				}

				// Cleanup
				quantityIngredientAdd.text?.clear()
				(unitIngredientAdd.editText as AutoCompleteTextView).setText(UnitOfMeasure.GRAM.toString(), false)
				quantityIngredientAdd.visibility = VISIBLE
				nameIngredientAdd.text?.clear()
				nameIngredientLayoutAdd.error = null

				// Add the view to linearLayout
				ingredientsContainerAdd.addView(root)

				if (toInsert) {
					addRecipeViewModel.updateState { state ->
						state.ingredientsList = state.ingredientsList.also { list ->
							list!!.add(
								Ingredient(
									nameIngredientItemAdd.text.toString(),
									quantityIngredientItemAdd.text.toString(),
									UnitOfMeasure.of((unitIngredientItemAdd.editText as AutoCompleteTextView).text.toString())!!
								)
							)
						}
					}
				}

				// Listeners UI->ViewModel
				quantityIngredientItemAdd.doOnTextChanged { text, _, _, _ ->
					addRecipeViewModel.updateState { state ->
						state.ingredientsList = state.ingredientsList.also { list ->
							list!![ingredientsContainerAdd.indexOfChild(root)].quantity = text.toString()
						}
					}
				}

				(unitIngredientItemAdd.editText as AutoCompleteTextView).onItemClickListener = OnItemClickListener { _, _, position, _ ->
					if (position == UnitOfMeasure.TO_TASTE.ordinal) {
						quantityIngredientItemAdd.setText("")
						quantityIngredientItemAdd.visibility = GONE
					} else {
						quantityIngredientItemAdd.visibility = VISIBLE
					}
					addRecipeViewModel.updateState { state ->
						state.ingredientsList = state.ingredientsList.also { list ->
							list!![ingredientsContainerAdd.indexOfChild(root)].unitOfMeasure = UnitOfMeasure.values()[position]
						}
					}
				}

				nameIngredientItemAdd.doOnTextChanged { text, _, _, _ ->
					addRecipeViewModel.updateState { state ->
						state.ingredientsList = state.ingredientsList.also { list ->
							list!![ingredientsContainerAdd.indexOfChild(root)].name = text.toString()
						}
					}
				}

				deleteIngredientItemAdd.setOnClickListener {
					addRecipeViewModel.updateState { state ->
						state.ingredientsList = state.ingredientsList.also { list ->
							list!!.removeAt(
								ingredientsContainerAdd.indexOfChild(
									root
								)
							)
						}
					}
					ingredientsContainerAdd.removeView(root)
				}
			}
		}
	}

	@Suppress("DEPRECATION")
	private fun canResolveIntent(intent: Intent): Boolean {
		val packageManager: PackageManager = requireActivity().packageManager
		val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
		return resolvedActivity != null
	}

	// APPBAR: MENU
	private fun setupMenu() {
		val appBarConfiguration = AppBarConfiguration(findNavController().graph)
		binding.toolbarAdd.setupWithNavController(findNavController(), appBarConfiguration)
		binding.toolbarAdd.addMenuProvider(object : MenuProvider {
			override fun onPrepareMenu(menu: Menu) {
				// Handle for example visibility of menu items
			}

			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.fragment_menu_add_recipe, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				return when (menuItem.itemId) {
					R.id.save -> {
						if (addRecipeViewModel.checkRecipe(binding, _bindingIngredientsList)) {//if there are no errors
							addRecipeViewModel.saveRecipe(requireContext())
							findNavController().navigateUp()
							true
						} else {
							false
						}
					}

					else -> false
				}
			}
		}, viewLifecycleOwner, Lifecycle.State.RESUMED)
	}
}