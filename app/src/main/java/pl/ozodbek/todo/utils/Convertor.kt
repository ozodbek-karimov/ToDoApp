package pl.ozodbek.todo.utils

import androidx.room.TypeConverter
import pl.ozodbek.todo.data.database.entities.Priority

class Convertor {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = enumValueOf(priority)

}