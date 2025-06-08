package com.example.studentmanagement

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.studentmanagement.Student

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "StudentManager.db"

        // Student table
        private const val TABLE_STUDENTS = "students"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_STUDENT_ID = "student_id"
        private const val COLUMN_MAJOR = "major"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_STUDENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_STUDENT_ID TEXT,
                $COLUMN_MAJOR TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        // Create tables again
        onCreate(db)
    }

    // Insert a new student
    fun addStudent(student: Student): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_STUDENT_ID, student.studentId)
            put(COLUMN_MAJOR, student.major)
        }

        // Insert row
        val id = db.insert(TABLE_STUDENTS, null, values)
        db.close()
        return id
    }

    // Get a student by ID
    fun getStudent(id: Int): Student? {
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_STUDENTS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_STUDENT_ID, COLUMN_MAJOR),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null, null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val student = Student(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MAJOR))
            )
            cursor.close()
            student
        } else {
            null
        }
    }

    // Get all students
    fun getAllStudents(): MutableList<Student> {
        val studentList = mutableListOf<Student>()
        val selectQuery = "SELECT * FROM $TABLE_STUDENTS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MAJOR))
                )
                studentList.add(student)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return studentList
    }

    // Update a student
    fun updateStudent(student: Student): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_STUDENT_ID, student.studentId)
            put(COLUMN_MAJOR, student.major)
        }

        // Update row
        val result = db.update(
            TABLE_STUDENTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(student.id.toString())
        )

        db.close()
        return result
    }

    // Delete a student
    fun deleteStudent(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(
            TABLE_STUDENTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return result
    }
}