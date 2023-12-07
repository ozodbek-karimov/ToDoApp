package pl.ozodbek.todo.ui.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import pl.ozodbek.profex.util.oneliner_viewbinding.viewBinding
import pl.ozodbek.todo.R
import pl.ozodbek.todo.adapters.ToDoListAdapter
import pl.ozodbek.todo.viewmodels.ToDoViewModel
import pl.ozodbek.todo.databinding.FragmentListBinding
import pl.ozodbek.todo.viewmodels.SharedViewModel
import pl.ozodbek.todo.utils.SwipeToDelete
import pl.ozodbek.todo.utils.buildDialog
import pl.ozodbek.todo.utils.changeFragmentTo
import pl.ozodbek.todo.utils.hide
import pl.ozodbek.todo.utils.hideSoftKeyboard
import pl.ozodbek.todo.utils.observeOnce
import pl.ozodbek.todo.utils.onClick
import pl.ozodbek.todo.utils.show

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), SearchView.OnQueryTextListener {

   private val binding by viewBinding(FragmentListBinding::bind)
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val toDoListAdapter: ToDoListAdapter by lazy { ToDoListAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        setupMenuProvider()
        setupRecyclerView()
        setupUIElements()
    }

    private fun setupUIElements() {
        hideSoftKeyboard()
        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            toDoListAdapter.submitList(data)
            swipeToDelete(binding.recyclerView)
        }

        binding.floatingActionButton.onClick {
            hideSoftKeyboard()
            changeFragmentTo(ListFragmentDirections.actionListFragmentToAddFragment())
        }

        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner){
            when {
                it -> {
                    binding.noDataImageview.show()
                    binding.noDataTextview.show()
                }
                else -> {
                    binding.noDataImageview.hide()
                    binding.noDataTextview.hide()
                }
            }
        }
    }

    private fun setupMenuProvider() {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.list_fragment_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.menu_delete_all -> {
                        confirmDeleteAllRemoval()
                        true
                    }

                    R.id.menu_priority_high -> { mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) { toDoListAdapter.submitList(it) }
                        return true
                    }

                    R.id.menu_priority_low -> { mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) { toDoListAdapter.submitList(it) }
                        return true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = toDoListAdapter
        toDoListAdapter.setItemClickListener { toDoData ->
            hideSoftKeyboard()
            changeFragmentTo(ListFragmentDirections.actionListFragmentToUpdateFragment(toDoData))
        }
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }
    }



    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            searchThroughDatabase(it)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            searchThroughDatabase(it)
        }
        return true
    }

    private fun searchThroughDatabase(query: String?) {
        val searchQuery = "%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observeOnce(viewLifecycleOwner) { list ->
            list.let {
                toDoListAdapter.submitList(list)
            }
        }
    }


    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = toDoListAdapter.currentList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(itemToDelete)
                toDoListAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                mSharedViewModel.showSnackbar("Deleted '${itemToDelete.title}'", requireView()) {mToDoViewModel.insertData(itemToDelete)}
            }
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(recyclerView)
    }


    private fun confirmDeleteAllRemoval() {

        buildDialog(
            getString(R.string.delete_all),
            getString(R.string.dialog_text),
            positiveButtonAction = {
                mToDoViewModel.deleteAll()
                mSharedViewModel.showSnackbar(
                    "All datas are deleted!",
                    requireView()
                )
            }
        )

    }

}