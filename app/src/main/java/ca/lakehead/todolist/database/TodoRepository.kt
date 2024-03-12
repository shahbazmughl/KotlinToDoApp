import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ca.lakehead.todolist.DataClasses.Todo

class TodoRepository(context: Context) {

    private val databaseHelper = TodoDatabaseHelper(context)

    // Add a new todo to the database
    fun addTodo(todo: Todo): Long {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(TodoDatabaseHelper.COLUMN_NAME, todo.name)
            put(TodoDatabaseHelper.COLUMN_NOTES, todo.notes)
            put(TodoDatabaseHelper.COLUMN_DUE_DATE, todo.dueDate)
            put(TodoDatabaseHelper.COLUMN_IS_COMPLETED, if (todo.isCompleted) 1 else 0)
        }

        val id = db.insert(TodoDatabaseHelper.TABLE_NAME, null, values)
        db.close()
        return id
    }

    // Add sample todos to the database
    fun addSampleTodos() {
        val sampleTodos = listOf(
            Todo("0", "Sample Task 1", "Notes for Sample Task 1", "2024-03-15", false),
            Todo("0", "Sample Task 2", "Notes for Sample Task 2", "2024-03-18", true),
            // Add more sample todos as needed
        )

        sampleTodos.forEach { addTodo(it) }
    }

    // Get all todos from the database
    fun getTodos(): List<Todo> {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            TodoDatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val todos = parseCursor(cursor)
        cursor.close()
        db.close()
        return todos
    }

    // Get a todo by its ID
    fun getById(todoId: String): Todo? {
        val db = databaseHelper.readableDatabase
        val selection = "${TodoDatabaseHelper.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(todoId.toString())

        val cursor = db.query(
            TodoDatabaseHelper.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val todos = parseCursor(cursor)
        cursor.close()
        db.close()

        return todos.firstOrNull() // Return the first matching Todo or null if not found
    }

    // Delete a todo from the database
    fun deleteTodo(todoId: String) {
        val db = databaseHelper.writableDatabase
        val whereClause = "${TodoDatabaseHelper.COLUMN_ID} = ?"
        val whereArgs = arrayOf(todoId.toString())
        db.delete(TodoDatabaseHelper.TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    // Update a todo in the database
    fun updateTodo(todo: Todo) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(TodoDatabaseHelper.COLUMN_NAME, todo.name)
            put(TodoDatabaseHelper.COLUMN_NOTES, todo.notes)
            put(TodoDatabaseHelper.COLUMN_DUE_DATE, todo.dueDate)
            put(TodoDatabaseHelper.COLUMN_IS_COMPLETED, if (todo.isCompleted) 1 else 0)
        }

        val whereClause = "${TodoDatabaseHelper.COLUMN_ID} = ?"
        val whereArgs = arrayOf(todo.id.toString())

        db.update(TodoDatabaseHelper.TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    // Parse cursor data to a list of Todo objects
    @SuppressLint("Range")
    private fun parseCursor(cursor: Cursor): List<Todo> {
        val todos = mutableListOf<Todo>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_NAME))
            val notes = cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_NOTES))
            val dueDate = cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_DUE_DATE))
            val isCompleted =
                cursor.getInt(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_IS_COMPLETED)) == 1
            val todo = Todo(id.toString(), name, notes, dueDate, isCompleted)
            todos.add(todo)
        }

        return todos
    }
}
