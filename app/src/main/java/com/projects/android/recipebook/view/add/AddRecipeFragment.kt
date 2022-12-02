package com.projects.android.recipebook.view.add

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.projects.android.recipebook.databinding.FragmentAddRecipeBinding
import com.projects.android.recipebook.databinding.ItemAddIngredientBinding
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import com.projects.android.recipebook.view.add.tag.AddSelectRecipeTagFragment
import com.projects.android.recipebook.view.add.tag.AddSelectRecipeTagFragment.DialogListener
import com.projects.android.recipebook.view.add.tag.TagSpan
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddRecipeFragment : Fragment() {

	private var container: ViewGroup? = null
	private var isDeleting = false
	private var textWatcherPreparationAdd: TextWatcher? = null

	// VIEW MODEL
	private val addRecipeViewModel: AddRecipeViewModel by viewModels()

	// VIEW BINDING
	private var _binding: FragmentAddRecipeBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	private var _bindingIngredientsList = mutableListOf<ItemAddIngredientBinding?>()

	// Variables for taking photos
	private var photoName: String? = null
	private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { didTakePhoto: Boolean ->
		if (didTakePhoto && photoName != null) {
			addRecipeViewModel.updateRecipe { it.photoFileName = photoName }
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		this.container = container
		_binding = FragmentAddRecipeBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			// Conditions for navigateUp
			activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
				val check = addRecipeViewModel.checkRecipe()
				if (!check.isNullOrBlank()) {
					Toast.makeText(context, "ERROR: $check", Toast.LENGTH_SHORT).show()
				} else {
					findNavController().navigateUp()
				}
			}

			requireContext().let {
				// Initialization spinner Course
				ArrayAdapter(
					it, android.R.layout.simple_spinner_item, Course.values()
				).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					courseAdd.adapter = adapter
				}
				// Initialization spinner PreparationTime
				ArrayAdapter(
					it, android.R.layout.simple_spinner_item, PreparationTime.values()
				).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					preparationTimeAdd.adapter = adapter
				}
				// Initialization spinner UnitOfMeasure
				ArrayAdapter(
					it, android.R.layout.simple_spinner_item, UnitOfMeasure.values()
				).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					unitIngredientAdd.adapter = adapter
				}
			}

			// Listeners UI->ViewModel (apply the changes to ViewModel when change the data on UI)
			nameAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateRecipe { it.name = text.toString() }
			}

			takePhotoAdd.isEnabled = canResolveIntent(takePhoto.contract.createIntent(requireContext(), Uri.EMPTY))
			takePhotoAdd.setOnClickListener {
				photoName = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(Date())}.JPG"
				val photoFile = File(requireContext().applicationContext.filesDir, photoName!!)
				val photoUri = FileProvider.getUriForFile(requireContext(), "com.projects.android.recipebook.fileprovider", photoFile)
				takePhoto.launch(photoUri)
			}

			isVegAdd.setOnCheckedChangeListener { _, b ->
				addRecipeViewModel.updateRecipe { it.isVeg = b }
			}
			isCookedAdd.setOnCheckedChangeListener { _, b ->
				addRecipeViewModel.updateRecipe { it.isCooked = b }
			}

			courseAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(parent: AdapterView<*>?) {}

				override fun onItemSelected(
					parent: AdapterView<*>?, view: View?, position: Int, id: Long
				) {
					parent!!.let {
						addRecipeViewModel.updateRecipe {
							it.course = parent.adapter.getItem(position) as Course
						}
					}
				}
			}

			preparationTimeAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(parent: AdapterView<*>?) {
				}

				override fun onItemSelected(
					parent: AdapterView<*>?, view: View?, position: Int, id: Long
				) {
					parent!!.let {
						addRecipeViewModel.updateRecipe {
							it.preparationTime = parent.adapter.getItem(position) as PreparationTime
						}
					}
				}
			}

			portionsAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateRecipe {
					it.portions = text.toString()
				}
			}

			unitIngredientAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onItemSelected(
					parent: AdapterView<*>?, view: View, position: Int, id: Long
				) {
					// Manage UnitOfMeasure.TO_TASTE
					if (position == UnitOfMeasure.TO_TASTE.ordinal) {
						quantityIngredientAdd.setText("")
						quantityIngredientAdd.visibility = GONE
					} else {
						quantityIngredientAdd.visibility = VISIBLE
					}
					nameIngredientAdd.requestFocus()
				}

				override fun onNothingSelected(parent: AdapterView<*>?) {}
			}

			nameIngredientAdd.setOnEditorActionListener { _, actionId, _ ->
				return@setOnEditorActionListener when (actionId) {
					EditorInfo.IME_ACTION_DONE -> {
						// If nameIngredientAdd is present and (quantityIngredientAdd is present or unitIngredientAdd==TO_TASTE)
						if ((!quantityIngredientAdd.text.isNullOrBlank() || unitIngredientAdd.selectedItemPosition == UnitOfMeasure.TO_TASTE.ordinal) && !nameIngredientAdd.text.isNullOrBlank()) {
							// Create the binding
							val bindingIngredients = ItemAddIngredientBinding.inflate(layoutInflater)
							_bindingIngredientsList.add(bindingIngredients)
							bindingIngredients.apply {
								// Initialization spinner UnitOfMeasure
								requireContext().let {
									ArrayAdapter(
										it, android.R.layout.simple_spinner_item, UnitOfMeasure.values()
									).also { adapter ->
										adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
										unitIngredientItemAdd.adapter = adapter
									}
								}

								// Filling UI with new ingredient
								quantityIngredientItemAdd.text = quantityIngredientAdd.text
								unitIngredientItemAdd.setSelection(unitIngredientAdd.selectedItemPosition)
								if (unitIngredientItemAdd.selectedItemPosition == UnitOfMeasure.TO_TASTE.ordinal) {
									quantityIngredientItemAdd.visibility = GONE
								}
								nameIngredientItemAdd.text = nameIngredientAdd.text

								// Cleanup
								quantityIngredientAdd.text.clear()
								unitIngredientAdd.setSelection(0)
								unitIngredientAdd.visibility = VISIBLE
								nameIngredientAdd.text.clear()

								// Add the view to linearLayout
								ingredientsContainerAdd.addView(root)

								addRecipeViewModel.updateRecipe { state ->
									state.ingredientsList = state.ingredientsList.also { list ->
										list!!.add(
											Ingredient(
												nameIngredientItemAdd.text.toString(),
												quantityIngredientItemAdd.text.toString(),
												unitIngredientItemAdd.selectedItem as UnitOfMeasure
											)
										)
									}
								}

								// Listeners UI->ViewModel
								quantityIngredientItemAdd.doOnTextChanged { text, _, _, _ ->
									addRecipeViewModel.updateRecipe { state ->
										state.ingredientsList = state.ingredientsList.also { list ->
											list!![ingredientsContainerAdd.indexOfChild(root)].quantity = text.toString()
										}
									}
								}

								unitIngredientItemAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
									override fun onItemSelected(
										parent: AdapterView<*>?, view: View, position: Int, id: Long
									) {
										if (position == UnitOfMeasure.TO_TASTE.ordinal) {
											quantityIngredientItemAdd.setText("")
											quantityIngredientItemAdd.visibility = INVISIBLE
										} else {
											quantityIngredientItemAdd.visibility = VISIBLE
										}
										addRecipeViewModel.updateRecipe { state ->
											state.ingredientsList = state.ingredientsList.also { list ->
												list!![ingredientsContainerAdd.indexOfChild(root)].unitOfMeasure = UnitOfMeasure.values()[position]
											}
										}
									}

									override fun onNothingSelected(parent: AdapterView<*>?) {}
								}

								nameIngredientItemAdd.doOnTextChanged { text, _, _, _ ->
									addRecipeViewModel.updateRecipe { state ->
										state.ingredientsList = state.ingredientsList.also { list ->
											list!![ingredientsContainerAdd.indexOfChild(root)].name = text.toString()
										}
									}
								}

								deleteIngredientItemAdd.setOnClickListener {
									addRecipeViewModel.updateRecipe { state ->
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
						true
					}

					else -> false
				}
			}

			textWatcherPreparationAdd = object : TextWatcher {

				// Detects if the user is deleting text
				override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
					if (after < count) {
						isDeleting = true
					}
				}

				override fun onTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

				override fun afterTextChanged(text: Editable?) {
					if (!text.isNullOrBlank()) {

						addRecipeViewModel.updateRecipe {
							it.preparationEditable = text
						}

						if (isDeleting) {
							// when deleting, if detects a span, deletes it entirely
							text.getSpans(preparationAdd.selectionStart, preparationAdd.selectionEnd, TagSpan::class.java).also {
								if (it.isNotEmpty()) {
									editText(preparationAdd) {
										text.delete(text.getSpanStart(it[0]), text.getSpanEnd(it[0]))
										text.removeSpan(it[0])
									}
									addRecipeViewModel.updateRecipe { state ->
										state.preparationEditable = text
									}
								}
							}
							isDeleting = false
						}
					}

					if (!text.isNullOrBlank() && preparationAdd.selectionEnd > 0) {
						if (text[preparationAdd.selectionEnd - 1] == "#".toCharArray()[0]) {
							AddSelectRecipeTagFragment(object : DialogListener {
								override fun cancelled() {
									editText(preparationAdd) {
										text.delete(preparationAdd.selectionStart - 1, preparationAdd.selectionStart)
									}
								}

								override fun ready(recipe: Recipe) {
									val name = "#${recipe.name}" // add "#" in span
									val spannableText: Spannable = SpannableString(name)
									spannableText.setSpan(TagSpan(recipe.id.toString()) {
										findNavController().navigate(AddRecipeFragmentDirections.fromAddRecipeFragmentToSingleRecipeFragment(recipe.id))
									}, 0, spannableText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
									preparationAdd.movementMethod = LinkMovementMethod.getInstance()
									editText(preparationAdd) {
										text.replace(
											preparationAdd.selectionStart - 1, preparationAdd.selectionStart, spannableText
										) // replacing the inserted "#"
										text.append(" ")
									}
									addRecipeViewModel.updateRecipe {
										it.preparationEditable = text
									}
								}
							}).show(childFragmentManager, AddSelectRecipeTagFragment.TAG)
						}
					}
				}
			}

			preparationAdd.addTextChangedListener(textWatcherPreparationAdd)
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
								courseAdd.setSelection(state.course!!.ordinal)
							}
							state.preparationTime?.let {
								preparationTimeAdd.setSelection(state.preparationTime!!.ordinal)
							}
							state.portions?.let {
								if (portionsAdd.text.toString() != state.portions.toString()) {
									portionsAdd.setText(state.portions.toString())
								}
							}
							state.preparationEditable?.let {
								if (preparationAdd.text != state.preparationEditable) {
									preparationAdd.text = state.preparationEditable
								}
							}
							state.ingredientsList?.let {
								for (i in _bindingIngredientsList.indices) {
									_bindingIngredientsList[i]!!.apply {
										val ingredient = state.ingredientsList!![i]
										if (nameIngredientItemAdd.text.toString() != ingredient.name) {
											nameIngredientItemAdd.setText(ingredient.name)
										}
										if (quantityIngredientItemAdd.text.toString() != ingredient.quantity) {
											quantityIngredientItemAdd.setText(ingredient.quantity)
										}
										if (unitIngredientItemAdd.selectedItemPosition != ingredient.unitOfMeasure.ordinal) {
											unitIngredientItemAdd.setSelection(ingredient.unitOfMeasure.ordinal)
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

	@Suppress("DEPRECATION")
	private fun canResolveIntent(intent: Intent): Boolean {
		val packageManager: PackageManager = requireActivity().packageManager
		val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
		return resolvedActivity != null
	}

	private fun editText(editText: EditText, edit: () -> Unit) {
		editText.removeTextChangedListener(textWatcherPreparationAdd)
		edit()
		editText.addTextChangedListener(textWatcherPreparationAdd)
	}
}