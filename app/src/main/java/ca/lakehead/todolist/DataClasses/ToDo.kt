package ca.lakehead.todolist.DataClasses

data class Todo(
    val id: String,
    val name: String,
    val notes: String,
    val dueDate: String?,  // Use String for simplicity, you might want to use Date
    var isCompleted: Boolean = false
)
