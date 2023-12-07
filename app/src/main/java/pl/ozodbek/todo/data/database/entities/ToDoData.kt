package pl.ozodbek.todo.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pl.ozodbek.todo.utils.Constants.Companion.DATABASE_NAME
import pl.ozodbek.todo.utils.Constants.Companion.TODO_TABLE_NAME

@Parcelize
@Entity(tableName = TODO_TABLE_NAME)
data class ToDoData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String
): Parcelable