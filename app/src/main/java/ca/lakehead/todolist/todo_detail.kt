package ca.lakehead.todolist

import TodoRepository
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Switch
import ca.lakehead.todolist.DataClasses.Todo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class todo_detail : AppCompatActivity() {

    private lateinit var todoRepository: TodoRepository
    private lateinit var id: String
    private lateinit var taskNameEditText: EditText
    private lateinit var taskDetailEditText: EditText
    private lateinit var dueDateSwitch: Switch
    private lateinit var calendarView: CalendarView
    private lateinit var addButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_detail)

        // Initialize TodoRepository
        todoRepository = TodoRepository(this)

        // Retrieve the todo object ID from the intent
        id = intent.getStringExtra("todo") as String

        // Initialize UI components
        taskNameEditText = findViewById(R.id.taskNameEditText)
        taskDetailEditText = findViewById(R.id.taskDetailEditText)
        dueDateSwitch = findViewById(R.id.dueDateSwitch)
        calendarView = findViewById(R.id.calendarView)
        addButton = findViewById(R.id.addButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Retrieve todo object from the repository based on ID
        val todo = todoRepository.getById(id)
        var selectedDate:String = "";

        if (todo != null) {
            // If the todo exists, it means we are in edit mode
            addButton.text = "Update"
            // Set initial values based on the todo object
            taskNameEditText.setText(todo.name)
            taskDetailEditText.setText(todo.notes)
            dueDateSwitch.isChecked = !todo.dueDate.isNullOrEmpty()

            addButton.setOnClickListener {
                // Update the existing todo with new information

                val updatedTodo = Todo(
                    id,
                    taskNameEditText.text.toString(),
                    taskDetailEditText.text.toString(),
                    if (dueDateSwitch.isChecked){selectedDate} else null,
                    todo.isCompleted
                )

                todoRepository.updateTodo(updatedTodo)

                // Navigate back to the MainActivity after updating
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        else {
            // If the todo doesn't exist, it means we are in add mode
            addButton.setOnClickListener {
                // Add a new todo with the entered information


                val newTodo = Todo(
                    id,
                    taskNameEditText.text.toString(),
                    taskDetailEditText.text.toString(),
                    if (dueDateSwitch.isChecked){selectedDate} else null,
                    false
                )

                todoRepository.addTodo(newTodo)

                // Navigate back to the MainActivity after adding
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Use the selected date from the CalendarView
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                GregorianCalendar(year, month, dayOfMonth).time
            )
        }


        // Handle cancellation by finishing the activity without saving changes
        cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}


