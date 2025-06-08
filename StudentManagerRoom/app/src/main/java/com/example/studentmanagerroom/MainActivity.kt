package com.example.studentmanagerroom

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddStudent: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)
        setupRecyclerView()
        setupFab()
        observeStudents()
        addSampleData()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewStudents)
        studentAdapter = StudentAdapter(
            onEditClick = { student -> showEditStudentDialog(student) },
            onDeleteClick = { student -> showDeleteConfirmationDialog(student) }
        )
        recyclerView.adapter = studentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFab() {
        fabAddStudent = findViewById(R.id.fabAddStudent)
        fabAddStudent.setOnClickListener {
            showAddStudentDialog()
        }
    }

    private fun observeStudents() {
        database.studentDao().getAllStudents().observe(this) { students ->
            studentAdapter.submitList(students)
        }
    }

    private fun addSampleData() {
        lifecycleScope.launch {
            val existingStudents = database.studentDao().getAllStudents().value
            if (existingStudents.isNullOrEmpty()) {
                val sampleStudents = listOf(
                    Student("2021001", "Nguyen Van A", "Computer Science"),
                    Student("2021002", "Tran Thi B", "Information Technology"),
                    Student("2021003", "Le Van C", "Software Engineering"),
                    Student("2021004", "Pham Thi D", "Data Science"),
                    Student("2021005", "Hoang Van E", "Cybersecurity")
                )
                
                sampleStudents.forEach { student ->
                    database.studentDao().insertStudent(student)
                }
            }
        }
    }

    private fun showAddStudentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_student, null)
        val editTextId = dialogView.findViewById<TextInputEditText>(R.id.editTextStudentId)
        val editTextName = dialogView.findViewById<TextInputEditText>(R.id.editTextStudentName)
        val editTextMajor = dialogView.findViewById<TextInputEditText>(R.id.editTextStudentMajor)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            val id = editTextId.text.toString().trim()
            val name = editTextName.text.toString().trim()
            val major = editTextMajor.text.toString().trim()

            if (id.isNotEmpty() && name.isNotEmpty() && major.isNotEmpty()) {
                val newStudent = Student(id, name, major)
                lifecycleScope.launch {
                    try {
                        database.studentDao().insertStudent(newStudent)
                        Toast.makeText(this@MainActivity, "Student added successfully", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Error: Student ID already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showEditStudentDialog(student: Student) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_student, null)
        val editTextId = dialogView.findViewById<TextInputEditText>(R.id.editTextStudentId)
        val editTextName = dialogView.findViewById<TextInputEditText>(R.id.editTextStudentName)
        val editTextMajor = dialogView.findViewById<TextInputEditText>(R.id.editTextStudentMajor)

        editTextId.setText(student.id)
        editTextName.setText(student.name)
        editTextMajor.setText(student.major)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.buttonUpdate).setOnClickListener {
            val name = editTextName.text.toString().trim()
            val major = editTextMajor.text.toString().trim()

            if (name.isNotEmpty() && major.isNotEmpty()) {
                val updatedStudent = student.copy(name = name, major = major)
                lifecycleScope.launch {
                    database.studentDao().updateStudent(updatedStudent)
                    Toast.makeText(this@MainActivity, "Student updated successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.name}?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    database.studentDao().deleteStudent(student)
                    Toast.makeText(this@MainActivity, "Student deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}