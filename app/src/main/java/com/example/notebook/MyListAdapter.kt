package com.example.notebook

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast


class MyListAdapter(
    context: Activity,
    private val dataSource: MutableList<ListItem>,
    private val coroutineScope: CoroutineScope,
    private val database: AppDatabase
) : ArrayAdapter<ListItem>(context, R.layout.list_item , dataSource) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val deleteButton = view.findViewById<Button>(R.id.delete_button)
        val item = dataSource[position]
        checkBox.text = item.task
        checkBox.isChecked = item.status

        deleteButton.setOnClickListener {
            coroutineScope.launch {
                database.listItemDao().delete(item)
                dataSource.removeAt(position)
                notifyDataSetChanged()
                Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT).show()
            }
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            coroutineScope.launch {
                item.status = isChecked
                database.listItemDao().update(item)
            }
        }

        return view
    }
}