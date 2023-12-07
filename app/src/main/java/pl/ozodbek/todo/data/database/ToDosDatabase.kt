package pl.ozodbek.todo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.ozodbek.todo.data.database.dao.ToDoDao
import pl.ozodbek.todo.data.database.entities.ToDoData
import pl.ozodbek.todo.utils.Convertor

@Database(
    entities = [ToDoData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertor::class)
abstract class ToDosDatabase : RoomDatabase( ){

    abstract fun toDoDao() : ToDoDao
}