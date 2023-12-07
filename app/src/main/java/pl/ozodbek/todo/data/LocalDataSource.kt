package pl.ozodbek.todo.data


import kotlinx.coroutines.flow.Flow
import pl.ozodbek.todo.data.database.dao.ToDoDao
import pl.ozodbek.todo.data.database.entities.ToDoData
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val toDoDao: ToDoDao
) {

    fun getAllData(): Flow<List<ToDoData>> {
        return toDoDao.getAllData()
    }

    fun sortByHighPriority(): Flow<List<ToDoData>> {
        return toDoDao.sortByHighPriority()
    }

    fun sortByLowPriority(): Flow<List<ToDoData>> {
        return toDoDao.sortByLowPriority()
    }

    fun searchDatabase(searchQuery: String): Flow<List<ToDoData>> {
        return toDoDao.searchDatabase(searchQuery)
    }



    suspend fun insertData(toDoData: ToDoData) {
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData) {
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteItem(toDoData: ToDoData) {
        toDoDao.deleteItem(toDoData)
    }

    suspend fun deleteAll() {
        toDoDao.deleteAll()
    }

}