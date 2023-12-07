package pl.ozodbek.todo.viewmodels

import android.app.Application
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import pl.ozodbek.todo.R
import pl.ozodbek.todo.data.database.entities.Priority
import pl.ozodbek.todo.data.database.entities.ToDoData
import pl.ozodbek.todo.utils.Constants.Companion.COLOR_GREEN
import pl.ozodbek.todo.utils.Constants.Companion.COLOR_RED
import pl.ozodbek.todo.utils.Constants.Companion.COLOR_YELLOW

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    /** =================================== List Fragment ========================================*/

    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfDatabaseEmpty(toDoData: List<ToDoData>) {
        emptyDatabase.value = toDoData.isEmpty()

    }

    /** =================================== Add/Update Fragment ========================================*/

    fun showSnackbar(text: String, view: View, action: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.snackbar_color))
            .setTextColor(
                ContextCompat.getColor(view.context, R.color.snackbar_text_color))
        action?.let {
            snackbar.setAction("Undo") { action.invoke() }
        }
        snackbar.show()
    }


    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val colors = listOf(COLOR_RED, COLOR_GREEN, COLOR_YELLOW)
            val textView = parent?.getChildAt(0) as? TextView ?: return
            textView.setTextColor(Color.parseColor(colors.getOrNull(position) ?: COLOR_YELLOW))
        }
    }

    fun isDataValid(title: String, description: String): Boolean {
        return title.isNotBlank() && description.isNotBlank()
    }

    fun parsePriority(priority: String): Priority = when (priority) {
        "High" -> Priority.HIGH
        "Medium" -> Priority.MEDIUM
        "Low" -> Priority.LOW
        else -> Priority.LOW
    }


    fun parsePriorityToInt(priority: Priority): Int = when (priority) {
        Priority.HIGH -> 0
        Priority.MEDIUM -> 1
        Priority.LOW -> 2
    }

}