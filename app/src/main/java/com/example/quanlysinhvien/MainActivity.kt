package com.example.quanlysinhvien

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var idEditText: EditText
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.editTextName)
        idEditText = findViewById(R.id.editTextId)
        addButton = findViewById(R.id.buttonAdd)
        recyclerView = findViewById(R.id.recyclerViewStudents)

        adapter = StudentAdapter(studentList) { position ->
            removeStudent(position)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            addStudent()
        }

        addSampleData()
    }

    private fun addStudent() {
        val name = nameEditText.text.toString().trim()
        val id = idEditText.text.toString().trim()

        if (name.isNotEmpty() && id.isNotEmpty()) {
            studentList.add(0, Student(name, id))
            adapter.notifyItemInserted(0)
            recyclerView.scrollToPosition(0)

            // Clear input fields
            nameEditText.text.clear()
            idEditText.text.clear()
        }
    }

    private fun removeStudent(position: Int) {
        studentList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun addSampleData() {
        studentList.add(Student("Họ tên sinh viên 1", "MSSV1"))
        studentList.add(Student("Họ tên sinh viên 2", "MSSV2"))
        studentList.add(Student("Họ tên sinh viên 3", "MSSV3"))
        adapter.notifyDataSetChanged()
    }
}
