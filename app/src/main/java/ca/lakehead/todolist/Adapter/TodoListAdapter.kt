package ca.lakehead.todolist.Adapter

import TodoRepository
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.lakehead.todolist.DataClasses.Todo
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import ca.lakehead.todolist.R

class TodoListAdapter(
    private var todoList: List<Todo>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    // Interface for item click events
    interface OnItemClickListener {
        fun onItemClick(todo: Todo)
        fun onEditClick(todo: Todo)
        fun onDeleteClick(todo: Todo)
        fun onSwitchChanged(todo: Todo, isChecked: Boolean)
    }

    // Function to update the todo list and trigger UI refresh
    fun updateList(newTodoList: List<Todo>) {
        todoList = newTodoList
        notifyDataSetChanged()
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.todoNameTextView)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private val switch: Switch = itemView.findViewById(R.id.todoCompletionSwitch)
        private val dueDate: TextView = itemView.findViewById(R.id.todoDueDateTextView)
        private val editImage: ImageView = itemView.findViewById(R.id.todoEditImageView)
        private val deleteImage: ImageView = itemView.findViewById(R.id.todoDeleteImageView)

        init {
            // Set click listeners for item, edit, and delete actions
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(todoList[position])
                }
            }

            editImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onEditClick(todoList[position])
                }
            }

            deleteImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onDeleteClick(todoList[position])
                }
            }

            // Set switch change listener to handle item background color and switch state changes
            switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Change the item background color to gray when the switch is checked
                    itemView.setBackgroundColor(itemView.resources.getColor(R.color.grayBackground))
                } else {
                    // Change the item background color back to the default color when the switch is unchecked
                    itemView.setBackgroundColor(itemView.resources.getColor(android.R.color.white))
                }
                val position = adapterPosition
                itemClickListener.onSwitchChanged(todoList[position], isChecked)
            }
        }

        // Bind data to the UI components
        fun bind(todo: Todo) {
            textView.text = todo.name
            switch.isChecked = todo.isCompleted
            dueDate.text = todo.dueDate ?: "No Due Date"

            // Set item background color based on switch state
            if (todo.isCompleted) {
                // Change the item background color to gray when the switch is checked
                itemView.setBackgroundColor(itemView.resources.getColor(R.color.grayBackground))
            } else {
                // Change the item background color back to the default color when the switch is unchecked
                itemView.setBackgroundColor(itemView.resources.getColor(android.R.color.white))
            }
        }
    }

    // Create a new view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)

        return TodoViewHolder(itemView)
    }

    // Bind data to the view holder
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = todoList[position]
        holder.bind(currentItem)
    }

    // Return the number of items in the todo list
    override fun getItemCount(): Int {
        return todoList.size
    }

}
