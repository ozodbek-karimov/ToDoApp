package pl.ozodbek.todo.ui.fragments.update

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import pl.ozodbek.profex.util.oneliner_viewbinding.viewBinding
import pl.ozodbek.todo.R
import pl.ozodbek.todo.data.database.entities.ToDoData
import pl.ozodbek.todo.databinding.FragmentUpdateBinding
import pl.ozodbek.todo.utils.buildDialog
import pl.ozodbek.todo.utils.changeFragmentTo
import pl.ozodbek.todo.utils.onBackPressed
import pl.ozodbek.todo.utils.onClick
import pl.ozodbek.todo.utils.popBackStack
import pl.ozodbek.todo.viewmodels.SharedViewModel
import pl.ozodbek.todo.viewmodels.ToDoViewModel

@AndroidEntryPoint
class UpdateFragment : Fragment(R.layout.fragment_update) {

    private val binding by viewBinding(FragmentUpdateBinding::bind)
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoDoViewModel: ToDoViewModel by viewModels()
    private val safeArgs: UpdateFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressed {
            popBackStack()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }


    private fun setupUI() {
        setupMenuProvider()
        setupClickListeners()
        setupUiElements()
    }

    private fun setupUiElements() {
        binding.apply {
            currentTitleEt.setText(safeArgs.currentItem.title)
            currentDescriptionEt.setText(safeArgs.currentItem.description)
            currentPrioritiesSpinner.setSelection(mSharedViewModel.parsePriorityToInt(safeArgs.currentItem.priority))
            currentPrioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener
        }
    }

    private fun setupClickListeners() {
        binding.floatingActionButtonCheck.onClick { updateItem() }

    }

    private fun setupMenuProvider() {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {

                    R.id.menu_delete -> {
                        confirmItemRemoval()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun confirmItemRemoval() {

        buildDialog(
            getString(R.string.delete_dialog_title, safeArgs.currentItem.title),
            getString(R.string.delete_dialog_message, safeArgs.currentItem.title),
            positiveButtonAction = {
                mToDoDoViewModel.deleteItem(safeArgs.currentItem)
                findNavController().navigate(UpdateFragmentDirections.actionUpdateFragmentToListFragment())
                mSharedViewModel.showSnackbar(
                    "Item deleted!",
                    requireView()
                )
            }

        )

    }

    private fun updateItem() {
        val title = binding.currentTitleEt.text.toString()
        val priority = binding.currentPrioritiesSpinner.selectedItem.toString()
        val description = binding.currentDescriptionEt.text.toString()

        if (mSharedViewModel.isDataValid(title, description)) {
            val newToDoData = ToDoData(
                safeArgs.currentItem.id,
                title,
                mSharedViewModel.parsePriority(priority),
                description
            )

            mToDoDoViewModel.updateData(newToDoData)
            mSharedViewModel.showSnackbar(
                "Succesfully updated!",
                requireView()
            )

            changeFragmentTo(UpdateFragmentDirections.actionUpdateFragmentToListFragment())
        } else {
            mSharedViewModel.showSnackbar(
                "Please fill out all fields to update !",
                requireView()
            )
        }
    }

}