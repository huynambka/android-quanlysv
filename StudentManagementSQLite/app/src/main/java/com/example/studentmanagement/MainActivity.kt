package com.example.studentmanagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity(), StudentAdapter.OnStudentListener {

    private lateinit var rvStudents: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var adapter: StudentAdapter
    private var studentList = mutableListOf<Student>()
    private var filteredList = mutableListOf<Student>()

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Initialize views
        rvStudents = findViewById(R.id.rvStudents)
        etSearch = findViewById(R.id.etSearch)
        fabAdd = findViewById(R.id.fabAdd)

        // Load students from database
        loadStudentsFromDb()

        filteredList = ArrayList(studentList)

        // Set up RecyclerView
        rvStudents.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter(this, filteredList, this)
        rvStudents.adapter = adapter

        // Set up search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { filterStudents(it) }
            }
        })

        // Set up add button
        fabAdd.setOnClickListener {
            showAddStudentDialog()
        }
    }

    private fun loadStudentsFromDb() {
        // Get students from database
        studentList = dbHelper.getAllStudents()

        // If database is empty, add sample data (only for first run)
        if (studentList.isEmpty()) {
            addSampleData()
        }
    }

    private fun addSampleData() {
        val sampleStudents = listOf(
            Student(0, "John Smith", "S12345", "Computer Science"),
            Student(0, "Emma Johnson", "S12346", "Business Administration"),
            Student(0, "Michael Brown", "S12347", "Electrical Engineering"),
            Student(0, "Sophia Williams", "S12348", "Psychology"),
            Student(0, "James Miller", "S12349", "Medicine")
        )

        // Add sample students to database
        for (student in sampleStudents) {
            val id = dbHelper.addStudent(student)
            student.id = id.toInt()
            studentList.add(student)
        }
    }

    private fun showAddStudentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etStudentId = dialogView.findViewById<TextInputEditText>(R.id.etStudentId)
        val etMajor = dialogView.findViewById<TextInputEditText>(R.id.etMajor)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val major = etMajor.text.toString().trim()

            if (validateInput(name, studentId, major)) {
                // Create new student
                val newStudent = Student(0, name, studentId, major)

                // Add to database
                val id = dbHelper.addStudent(newStudent)
                newStudent.id = id.toInt()

                // Add to list and update RecyclerView
                studentList.add(newStudent)
                filteredList.add(newStudent)
                adapter.updateList(filteredList)

                Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun validateInput(name: String, studentId: String, major: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (studentId.isEmpty()) {
            Toast.makeText(this, "Please enter a student ID", Toast.LENGTH_SHORT).show()
            return false
        }
        if (major.isEmpty()) {
            Toast.makeText(this, "Please enter a major", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if student ID already exists
        val existingStudent = studentList.find { it.studentId == studentId }
        if (existingStudent != null) {
            Toast.makeText(this, "Student ID already exists", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun showEditStudentDialog(student: Student) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null)

        // Get references to views
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etStudentId = dialogView.findViewById<TextInputEditText>(R.id.etStudentId)
        val etMajor = dialogView.findViewById<TextInputEditText>(R.id.etMajor)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Set dialog title
        dialogTitle.text = "Edit Student"

        // Pre-fill fields with student data
        etName.setText(student.name)
        etStudentId.setText(student.studentId)
        etMajor.setText(student.major)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val major = etMajor.text.toString().trim()

            if (validateEditInput(name, studentId, major, student.id)) {
                // Update student object
                val updatedStudent = Student(student.id, name, studentId, major)

                // Update in database
                dbHelper.updateStudent(updatedStudent)

                // Update in lists
                val index = studentList.indexOfFirst { it.id == student.id }
                if (index != -1) {
                    studentList[index] = updatedStudent
                }

                val filteredIndex = filteredList.indexOfFirst { it.id == student.id }
                if (filteredIndex != -1) {
                    filteredList[filteredIndex] = updatedStudent
                }

                // Update RecyclerView
                adapter.updateList(filteredList)

                Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun validateEditInput(name: String, studentId: String, major: String, currentId: Int): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (studentId.isEmpty()) {
            Toast.makeText(this, "Please enter a student ID", Toast.LENGTH_SHORT).show()
            return false
        }
        if (major.isEmpty()) {
            Toast.makeText(this, "Please enter a major", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if student ID already exists but exclude the current student
        val existingStudent = studentList.find {
            it.studentId == studentId && it.id != currentId
        }
        if (existingStudent != null) {
            Toast.makeText(this, "Student ID already exists", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }



    private fun filterStudents(query: String) {
        filteredList = if (query.isEmpty()) {
            ArrayList(studentList)
        } else {
            val lowerCaseQuery = query.toLowerCase()
            studentList.filter { student ->
                student.name.toLowerCase().contains(lowerCaseQuery) ||
                        student.studentId.toLowerCase().contains(lowerCaseQuery) ||
                        student.major.toLowerCase().contains(lowerCaseQuery)
            }.toMutableList()
        }
        adapter.updateList(filteredList)
    }

    override fun onEditClick(position: Int) {
        val student = filteredList[position]
        showEditStudentDialog(student)
    }

    override fun onDeleteClick(position: Int) {
        val student = filteredList[position]

        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.name}?")
            .setPositiveButton("Delete") { _, _ ->
                // Delete from database
                dbHelper.deleteStudent(student.id)

                // Remove from lists
                studentList.remove(student)
                filteredList.remove(student)

                // Update RecyclerView
                adapter.updateList(filteredList)

                Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}