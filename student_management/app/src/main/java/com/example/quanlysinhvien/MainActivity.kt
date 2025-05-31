package com.example.quanlysinhvien

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
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

        initViews()
        setupRecyclerView()
        setupListeners()
        addSampleData()
    }

    private fun initViews() {
        nameEditText = findViewById(R.id.editTextName)
        idEditText = findViewById(R.id.editTextId)
        addButton = findViewById(R.id.buttonAdd)
        recyclerView = findViewById(R.id.recyclerViewStudents)
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(studentList) { position ->
            removeStudent(position)
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupListeners() {
        addButton.setOnClickListener {
            addStudent()
        }
    }

    private fun addStudent() {
        val name = nameEditText.text.toString().trim()
        val id = idEditText.text.toString().trim()

        if (name.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        // Check for duplicate student ID
        if (studentList.any { it.id == id }) {
            Toast.makeText(this, "MSSV đã tồn tại", Toast.LENGTH_SHORT).show()
            return
        }

        val newStudent = Student(name, id)
        studentList.add(0, newStudent)
        adapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)

        // Clear input fields
        nameEditText.text.clear()
        idEditText.text.clear()
        nameEditText.requestFocus()
    }

    private fun removeStudent(position: Int) {
        if (position >= 0 && position < studentList.size) {
            val removedStudent = studentList[position]
            studentList.removeAt(position)
            adapter.notifyItemRemoved(position)
            
            // Show removal confirmation
            Toast.makeText(
                this, 
                "Đã xóa sinh viên: ${removedStudent.name}", 
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addSampleData() {
        val samples = listOf(
            Student("Nguyễn Văn A", "MSSV1"),
            Student("Trần Thị B", "MSSV2"),
            Student("Lê Hoàng C", "MSSV3")
        )
        
        studentList.addAll(samples)
        adapter.notifyDataSetChanged()
    }
}
