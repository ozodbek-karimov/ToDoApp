package pl.ozodbek.todo.adapters

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.ozodbek.todo.data.database.entities.Priority
import pl.ozodbek.todo.data.database.entities.ToDoData
import pl.ozodbek.todo.databinding.RowLayoutBinding
import pl.ozodbek.todo.utils.Constants.Companion.COLOR_GREEN
import pl.ozodbek.todo.utils.Constants.Companion.COLOR_RED
import pl.ozodbek.todo.utils.Constants.Companion.COLOR_YELLOW
import pl.ozodbek.todo.utils.onClick
import pl.ozodbek.todo.utils.viewBinding

class ToDoListAdapter :
    ListAdapter<ToDoData, ToDoListAdapter.MyViewHolder>(ToDoDiffUtil()) {

    private var itemClickListener: ((ToDoData) -> Unit)? = null

    fun setItemClickListener(listener: (ToDoData) -> Unit) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(parent.viewBinding(RowLayoutBinding::inflate))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val todoList = getItem(position)
        todoList?.let { holder.onBind(it, itemClickListener) }
    }


    class MyViewHolder(private val binding: RowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(toDoData: ToDoData, clickListener: ((ToDoData) -> Unit)?) {
            binding.apply {

                titleText.text = toDoData.title
                descriptionTextview.text = toDoData.description

                rowBackground.onClick {
                    clickListener?.invoke(toDoData)
                }

                binding.priorityIndicator.setCardBackgroundColor(
                    Color.parseColor(
                        when (toDoData.priority) {
                            Priority.HIGH -> COLOR_RED
                            Priority.MEDIUM -> COLOR_GREEN
                            Priority.LOW -> COLOR_YELLOW
                        }
                    )
                )
            }
        }
    }


    private class ToDoDiffUtil : DiffUtil.ItemCallback<ToDoData>() {
        override fun areItemsTheSame(oldItem: ToDoData, newItem: ToDoData) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ToDoData, newItem: ToDoData) =
            oldItem == newItem

    }
}


