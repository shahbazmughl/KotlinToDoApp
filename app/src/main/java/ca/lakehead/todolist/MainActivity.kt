package ca.lakehead.todolist

import TodoRepository
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.lakehead.todolist.Adapter.TodoListAdapter
import ca.lakehead.todolist.DataClasses.Todo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), TodoListAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var todoListAdapter: TodoListAdapter
    private lateinit var todoRepository: TodoRepository
    private lateinit var toolbarDay: TextView
    private lateinit var toolbarDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list)

        // Initialize TodoRepository
        todoRepository = TodoRepository(this)

        // Initialize RecyclerView and UI components
        recyclerView = findViewById(R.id.recycler_view_todo)
        toolbarDay = findViewById(R.id.toolbar_day)
        toolbarDate = findViewById(R.id.toolbar_date)

        recyclerView.layoutManager = LinearLayoutManager(this)
        todoListAdapter = TodoListAdapter(todoRepository.getTodos(), this)

        // Set up the toolbar with the current day and date
        val calendar = Calendar.getInstance()
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val formattedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(calendar.time)
        toolbarDay.text = dayOfWeek
        toolbarDate.text = formattedDate

        recyclerView.adapter = todoListAdapter

        // Set up FloatingActionButton for adding new items
        val fabAdd: FloatingActionButton = findViewById(R.id.addTodo)
        fabAdd.setOnClickListener {
            // Handle click on FloatingActionButton, open the activity for adding a new item
            val intent = Intent(this, todo_detail::class.java)
            intent.putExtra("todo", "0")
            startActivity(intent)
            finish()
        }
    }

    override fun onItemClick(todo: Todo) {
        // Handle item click, you might want to show details or navigate to the detail/edit activity
        Toast.makeText(this, "Clicked: ${todo.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClick(todo: Todo) {
        // Handle edit click, navigate to the detail/edit activity with the selected todo
        val intent = Intent(this, todo_detail::class.java)
        intent.putExtra("todo", todo.id)
        startActivity(intent)
        finish()
    }

    override fun onDeleteClick(todo: Todo) {
        // Handle delete click, remove the item from the database and update the UI
        todoRepository.deleteTodo(todo.id.toString())
        updateTodoList()
        Toast.makeText(this, "Deleted: ${todo.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onSwitchChanged(todo: Todo, isChecked: Boolean) {
        // Handle switch state change, update the todo in the database
        todo.isCompleted = isChecked
        todoRepository.updateTodo(todo)
    }

    private fun updateTodoList() {
        // Update the RecyclerView with the latest todo list from the database
        todoListAdapter.updateList(todoRepository.getTodos())
    }
}
