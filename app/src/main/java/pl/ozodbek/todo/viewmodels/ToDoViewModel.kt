package pl.ozodbek.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.ozodbek.todo.data.Repository
import pl.ozodbek.todo.data.database.entities.ToDoData
import pl.ozodbek.todo.utils.viewModelScopeOnIOThread
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    val getAllData: LiveData<List<ToDoData>> = repository.local.getAllData().asLiveData()
    val sortByHighPriority: LiveData<List<ToDoData>> = repository.local.sortByHighPriority().asLiveData()
    val sortByLowPriority: LiveData<List<ToDoData>> = repository.local.sortByLowPriority().asLiveData()



    fun insertData(toDoData: ToDoData) =
        viewModelScopeOnIOThread {
            repository.local.insertData(toDoData)
        }


    fun updateData(toDoData: ToDoData) =
        viewModelScopeOnIOThread {
            repository.local.updateData(toDoData)
        }


    fun deleteItem(toDoData: ToDoData) =
        viewModelScopeOnIOThread {
            repository.local.deleteItem(toDoData)
        }


    fun deleteAll() =
        viewModelScopeOnIOThread {
            repository.local.deleteAll()
        }


    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>> {
        return repository.local.searchDatabase(searchQuery).asLiveData()
    }
}