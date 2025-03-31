package com.example.quanlysinhvien

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private val students: List<Student>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val idTextView: TextView = view.findViewById(R.id.textViewId)
        val deleteButton: ImageButton = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.nameTextView.text = student.name
        holder.idTextView.text = student.id
        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount() = students.size
}