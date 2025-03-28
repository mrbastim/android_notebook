package com.example.notebook

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
//import android.widget.TextView
import android.widget.Toast
import androidx.room.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Entity(tableName = "to_do_list")
data class ListItem(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var task: String,
    var status: Boolean
)

@Dao
interface ListItemDao {
    @Update
    suspend fun update(listItem: ListItem)

    @Delete
    suspend fun delete(listItem: ListItem)

    @Insert
    suspend fun insert(listItem: ListItem): Long

    @Query("SELECT * FROM to_do_list")
    suspend fun getAllItems(): List<ListItem>

    @Query("DELETE FROM to_do_list")
    suspend fun deleteAllItems()
}

@Database(version = 1, entities = [ListItem::class])

abstract class AppDatabase : RoomDatabase() {
    abstract fun listItemDao(): ListItemDao
}


class MainActivity : AppCompatActivity() {

    private lateinit var listItems: MutableList<ListItem>
    private lateinit var adapter: MyListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user_input = findViewById<EditText>(R.id.user_input)
        val add_button = findViewById<Button>(R.id.add_button)
        val delete_button = findViewById<Button>(R.id.delete_button)
        val list_view = findViewById<ListView>(R.id.list_view)

        val database = (application as DataBaseInit).database
        val listItemDao = database.listItemDao()
        listItems = mutableListOf()
        adapter = MyListAdapter(this, listItems, lifecycleScope, database)
        list_view.adapter = adapter

        add_button.setOnClickListener {
            val taskText = user_input.text.toString()
            if (taskText.isNotBlank()) {
                val newItem = ListItem(task = taskText, status = false)
                lifecycleScope.launch {
                    try {
                        val newId = listItemDao.insert(newItem) // Добавление задачи в базу данных
                        newItem.id = newId
                        listItems.add(newItem) // Добавление задачи в список
                        adapter.notifyDataSetChanged() // Обновление адаптера
                        user_input.setText("") // Очистка поля ввода
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)) // Вибрация
                        }
                        Toast.makeText(this@MainActivity, "Задача добавлена", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error adding item", e) // Логирование ошибки
                    }
                }
            } else {
                Toast.makeText(this, "Может быть вы хотите что-то сделать?", Toast.LENGTH_SHORT).show()
            }
        }
        delete_button.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Очистка списка")
                .setMessage("Вы уверены, что хотите удалить все задачи?")
                .setCancelable(true)
                .setPositiveButton ("Да"){ _, _ ->
                    lifecycleScope.launch {
                        listItemDao.deleteAllItems()
                        listItems.clear()
                        adapter.notifyDataSetChanged() // Обновление адаптера
                        Toast.makeText(this@MainActivity, "Список был полностью очищен", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNeutralButton("Нет", null)
                .show()
        }
        try {
            lifecycleScope.launch {
                listItems.addAll(listItemDao.getAllItems())
                adapter.notifyDataSetChanged() // Обновление адаптера
            }

        } catch (e: Exception) {
            Log.e("MainActivity", "Error adding item", e) // Логирование ошибки
        }

    }
}

