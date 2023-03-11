package com.projects.android.recipebook.view.single

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.projects.android.recipebook.R
import com.projects.android.recipebook.databinding.FragmentSingleRecipeBinding
import com.projects.android.recipebook.utils.PictureUtils
import com.projects.android.recipebook.utils.PictureUtils.Companion.getScaledBitmap
import kotlinx.coroutines.launch

class SingleRecipeFragment : Fragment() {

	// SAFE ARGS
	private val args: SingleRecipeFragmentArgs by navArgs()

	// VIEW MODEL
	private val singleRecipeViewModel: SingleRecipeViewModel by viewModels {
		SingleRecipeViewModelFactory(args.recipeID)
	}

	// VIEW BINDING
	private var _binding: FragmentSingleRecipeBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = FragmentSingleRecipeBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// APPBAR: MENU
		setupMenu()

		// Custom back navigation
		activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
			if (binding.nameSingle.text.isNotBlank()) {
				findNavController().navigateUp()
			} else {
				Toast.makeText(context, "ERROR: Enter the name", Toast.LENGTH_SHORT).show()
			}
		}

		binding.apply {

		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				singleRecipeViewModel.state.collect { state ->
					state?.apply {
						recipe?.let {
							binding.apply {
								if (nameSingle.text.toString() != recipe!!.name) {
									nameSingle.text = recipe!!.name
								}
								if (preparationSingle.text.toString() != recipe!!.preparation) {
									preparationSingle.text = recipe!!.preparation
								}
								if (photoSingle.tag != recipe!!.pictureFileName) { // Update photo only when the name is different
									val photoFile = recipe!!.pictureFileName?.let {
										PictureUtils.getPicture(requireContext(), it)
									}
									if (photoFile?.exists() == true) {
										photoSingle.doOnLayout { measuredView ->
											val scaledBitmap = getScaledBitmap(
												photoFile.path, measuredView.width, measuredView.height
											)
											photoSingle.setImageBitmap(scaledBitmap)
											photoSingle.tag = recipe!!.pictureFileName
										}
									} else {
										photoSingle.setImageBitmap(null)
										photoSingle.tag = null
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
	}

	// APPBAR: MENU
	private fun setupMenu() {
		(requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
			override fun onPrepareMenu(menu: Menu) {
				// Handle for example visibility of menu items
			}

			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.fragment_menu_single_recipe, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				return when (menuItem.itemId) {
					R.id.delete_recipe -> {
						AlertDialog.Builder(requireContext()).setTitle("Confirm to Delete?").setIcon(R.drawable.ic_baseline_dangerous_24)
							.setPositiveButton(android.R.string.ok) { _, _ ->
								viewLifecycleOwner.lifecycleScope.launch {
									singleRecipeViewModel.deleteRecipe(requireContext())
								}
								findNavController().navigateUp()
							}.setNegativeButton(android.R.string.cancel, null).show()
						true
					}

					R.id.edit_recipe -> {
						findNavController().navigate(SingleRecipeFragmentDirections.fromSingleRecipeFragmentToAddRecipeFragment(singleRecipeViewModel.state.value!!.recipe!!.id))
						true
					}

					else -> false
				}
			}
		}, viewLifecycleOwner, Lifecycle.State.RESUMED)
	}
}