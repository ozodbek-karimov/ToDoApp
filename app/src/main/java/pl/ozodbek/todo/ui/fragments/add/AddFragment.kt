package pl.ozodbek.todo.ui.fragments.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import pl.ozodbek.todo.databinding.FragmentAddBinding
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pl.ozodbek.profex.util.oneliner_viewbinding.viewBinding
import pl.ozodbek.todo.R
import pl.ozodbek.todo.data.database.entities.ToDoData
import pl.ozodbek.todo.utils.changeFragmentTo
import pl.ozodbek.todo.utils.fullText
import pl.ozodbek.todo.utils.onBackPressed
import pl.ozodbek.todo.utils.onClick
import pl.ozodbek.todo.utils.popBackStack
import pl.ozodbek.todo.viewmodels.ToDoViewModel
import pl.ozodbek.todo.viewmodels.SharedViewModel

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    private val binding by viewBinding(FragmentAddBinding::bind)
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoDoViewModel: ToDoViewModel by viewModels()

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
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.prioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        binding.floatingActionButtonCheck.onClick {
            insertDataToDB()
        }
    }

    private fun insertDataToDB() {
        val title = binding.titleEt.fullText
        val description = binding.descriptionEt.fullText
        val priority = binding.prioritiesSpinner.selectedItem.toString()

        if (mSharedViewModel.isDataValid(title, description)) {
            val newToDoData = ToDoData(
                0,
                title,
                mSharedViewModel.parsePriority(priority),
                description
            )

            mToDoDoViewModel.insertData(newToDoData)

            mSharedViewModel.showSnackbar("Succesfully added!",  requireView())

            changeFragmentTo(AddFragmentDirections.actionAddFragmentToListFragment())

        } else {
            mSharedViewModel.showSnackbar("Please fill out all fields!", requireView())
        }
    }

}