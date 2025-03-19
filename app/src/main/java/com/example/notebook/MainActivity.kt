package com.example.notebook

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.room.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val database = (application as DataBaseInit).database
        val listItemDao = database.listItemDao()

        val logo_text = findViewById<TextView>(R.id.logo_text)
        val user_input = findViewById<EditText>(R.id.user_input)
        val add_button = findViewById<Button>(R.id.add_button)
        val list_view = findViewById<ListView>(R.id.list_view)

        val listItems = mutableListOf<ListItem>()

    }
}

@Entity(tableName = "to_do_list")
data class ListItem(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var task: String,
    var status: Boolean
)

@Dao
interface ListItemDao {

}

@Database(version = 1, entities = [ListItem::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun listItemDao(): ListItemDao
}