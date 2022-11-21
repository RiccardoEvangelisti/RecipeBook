package com.projects.android.recipebook.view.add

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddRecipeFragment : Fragment() {

	// VIEW MODEL
	private val addRecipeViewModel: AddRecipeViewModel by viewModels()

	// VIEW BINDING
	private var _binding: FragmentAddRecipeBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	private var _bindingIngredientiList = mutableListOf<ItemAddIngredientBinding?>()

	// Variables for taking photos
	private var photoName: String? = null
	private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { didTakePhoto: Boolean ->
		if (didTakePhoto && photoName != null) {
			addRecipeViewModel.updateRicetta { it.photoFileName = photoName }
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentAddRecipeBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			// Condizioni per navigateUp e quindi salvataggio della recipe
			activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
				val check = addRecipeViewModel.checkRicetta()
				if (!check.isNullOrBlank()) {
					Toast.makeText(context, "ERRORE: $check", Toast.LENGTH_SHORT).show()
				} else {
					findNavController().navigateUp()
				}
			}

			requireContext().let {
				// Inizializzazione spinner Course
				ArrayAdapter(
					it, android.R.layout.simple_spinner_item, Course.values()
				).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					courseAdd.adapter = adapter
				}
				// Inizializzazione spinner PreparationTime
				ArrayAdapter(
					it, android.R.layout.simple_spinner_item, PreparationTime.values()
				).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					preparationTimeAdd.adapter = adapter
				}
				// Inizializzazione spinner UnitaIngrediente
				ArrayAdapter(
					it, android.R.layout.simple_spinner_item, UnitOfMeasure.values()
				).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					unitIngredientAdd.adapter = adapter
				}
			}

			// Listeners UI->ViewModel (applicano la modifica al ViewModel al cambiamento del dato sulla UI)
			nameAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateRicetta { it.name = text.toString() }
			}

			photoAdd.isEnabled = canResolveIntent(takePhoto.contract.createIntent(requireContext(), Uri.EMPTY))
			photoAdd.setOnClickListener {
				photoName = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(Date())}.JPG"
				val photoFile = File(requireContext().applicationContext.filesDir, photoName!!)
				val photoUri = FileProvider.getUriForFile(requireContext(), "com.projects.android.recipebook.fileprovider", photoFile)
				takePhoto.launch(photoUri)
			}

			isVegetarianAdd.setOnCheckedChangeListener { _, b ->
				addRecipeViewModel.updateRicetta { it.isVegetarian = b }
			}
			isCookedAdd.setOnCheckedChangeListener { _, b ->
				addRecipeViewModel.updateRicetta { it.isCooked = b }
			}

			courseAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(parent: AdapterView<*>?) {}

				override fun onItemSelected(
					parent: AdapterView<*>?, view: View?, position: Int, id: Long
				) {
					parent!!.let {
						addRecipeViewModel.updateRicetta {
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
						addRecipeViewModel.updateRicetta {
							it.preparationTime = parent.adapter.getItem(position) as PreparationTime
						}
					}
				}
			}

			portionsAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateRicetta {
					it.portions = text.toString()
				}
			}

			unitIngredientAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onItemSelected(
					parent: AdapterView<*>?, view: View, position: Int, id: Long
				) {
					// Gestione UnitOfMeasure.QUANTOBASTA
					if (position == UnitOfMeasure.TOTASTE.ordinal) {
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
						// Se nameIngredientAdd e' consistente e (quantityIngredientAdd e' consistente oppure unitaIngrediente==QUANTOBASTA)
						if ((!quantityIngredientAdd.text.isNullOrBlank() || unitIngredientAdd.selectedItemPosition == UnitOfMeasure.TOTASTE.ordinal) && !nameIngredientAdd.text.isNullOrBlank()) {
							// Si genera il binding
							val bindingIngredienti = ItemAddIngredientBinding.inflate(layoutInflater)
							_bindingIngredientiList.add(bindingIngredienti)
							bindingIngredienti.apply {
								// Inizializzione spinner UnitOfMeasure
								requireContext().let {
									ArrayAdapter(
										it, android.R.layout.simple_spinner_item, UnitOfMeasure.values()
									).also { adapter ->
										adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
										unitIngredientItemAdd.adapter = adapter
									}
								}

								// Riempimento UI nuovo ingrediente
								quantityIngredientItemAdd.text = quantityIngredientAdd.text
								unitIngredientItemAdd.setSelection(unitIngredientAdd.selectedItemPosition)
								if (unitIngredientItemAdd.selectedItemPosition == UnitOfMeasure.TOTASTE.ordinal) {
									quantityIngredientItemAdd.visibility = GONE
								}
								nameIngredientItemAdd.text = nameIngredientAdd.text

								// Cleanup
								quantityIngredientAdd.text.clear()
								unitIngredientAdd.setSelection(0)
								unitIngredientAdd.visibility = VISIBLE
								nameIngredientAdd.text.clear()

								// Si aggiunge la view al linearLayout
								ingredientsContainerAdd.addView(root)

								addRecipeViewModel.updateRicetta { stato ->
									stato.ingredientsList = stato.ingredientsList.also { list ->
										list!!.add(
											Ingredient(
												nameIngredientItemAdd.text.toString(),
												quantityIngredientItemAdd.text.toString(),
												unitIngredientItemAdd.selectedItem as UnitOfMeasure
											)
										)
									}
								}

								// Listeners UI->ViewModel (applicano la modifica al ViewModel al cambiamento del dato sulla UI)
								quantityIngredientItemAdd.doOnTextChanged { text, _, _, _ ->
									addRecipeViewModel.updateRicetta { state ->
										state.ingredientsList = state.ingredientsList.also { list ->
											list!![ingredientsContainerAdd.indexOfChild(root)].quantity = text.toString()
										}
									}
								}

								unitIngredientItemAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
									override fun onItemSelected(
										parent: AdapterView<*>?, view: View, position: Int, id: Long
									) {
										if (position == UnitOfMeasure.TOTASTE.ordinal) {
											quantityIngredientItemAdd.setText("")
											quantityIngredientItemAdd.visibility = INVISIBLE
										} else {
											quantityIngredientItemAdd.visibility = VISIBLE
										}
										addRecipeViewModel.updateRicetta { stato ->
											stato.ingredientsList = stato.ingredientsList.also { list ->
												list!![ingredientsContainerAdd.indexOfChild(root)].unitOfMeasure = UnitOfMeasure.values()[position]
											}
										}
									}

									override fun onNothingSelected(parent: AdapterView<*>?) {}
								}

								nameIngredientItemAdd.doOnTextChanged { text, _, _, _ ->
									addRecipeViewModel.updateRicetta { stato ->
										stato.ingredientsList = stato.ingredientsList.also { list ->
											list!![ingredientsContainerAdd.indexOfChild(root)].name = text.toString()
										}
									}
								}

								deleteIngredientItemAdd.setOnClickListener {
									addRecipeViewModel.updateRicetta { stato ->
										stato.ingredientsList = stato.ingredientsList.also { list ->
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

			preparationAdd.doOnTextChanged { text, _, _, _ ->
				addRecipeViewModel.updateRicetta {
					it.preparation = text.toString()
				}
			}
		}

		// Colleziono lo StateFlow del ViewModel e con esso aggiorno la UI (ViewModel->UI)
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				addRecipeViewModel.state.collect { recipe ->
					recipe?.let {
						binding.apply {
							recipe.name?.let {
								if (nameAdd.text.toString() != recipe.name) { // previene un infinite-loop con il listener
									nameAdd.setText(recipe.name)
								}
							}
							recipe.isVegetarian?.let {
								isVegetarianAdd.isChecked = recipe.isVegetarian!!
							}
							recipe.isCooked?.let {
								isCookedAdd.isChecked = recipe.isCooked!!
							}
							recipe.course?.let {
								courseAdd.setSelection(recipe.course!!.ordinal)
							}
							recipe.preparationTime?.let {
								preparationTimeAdd.setSelection(recipe.preparationTime!!.ordinal)
							}
							recipe.portions?.let {
								if (portionsAdd.text.toString() != recipe.portions.toString()) {
									portionsAdd.setText(recipe.portions.toString())
								}
							}
							recipe.preparation?.let {
								if (preparationAdd.text.toString() != recipe.preparation) {
									preparationAdd.setText(recipe.preparation)
								}
							}
							recipe.ingredientsList?.let {
								for (i in _bindingIngredientiList.indices) {
									_bindingIngredientiList[i]!!.apply {
										val ingrediente = recipe.ingredientsList!![i]
										if (nameIngredientItemAdd.text.toString() != ingrediente.name) {
											nameIngredientItemAdd.setText(ingrediente.name)
										}
										if (quantityIngredientItemAdd.text.toString() != ingrediente.quantity) {
											quantityIngredientItemAdd.setText(ingrediente.quantity)
										}
										if (unitIngredientItemAdd.selectedItemPosition != ingrediente.unitOfMeasure.ordinal) {
											unitIngredientItemAdd.setSelection(ingrediente.unitOfMeasure.ordinal)
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
		for (i in _bindingIngredientiList.indices) {
			_bindingIngredientiList[i] = null
		}
	}

	@Suppress("DEPRECATION")
	private fun canResolveIntent(intent: Intent): Boolean {
		val packageManager: PackageManager = requireActivity().packageManager
		val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
		return resolvedActivity != null
	}
}