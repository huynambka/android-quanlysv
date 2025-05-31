package com.example.studentmanagement

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private val context: Context,
    private var studentList: List<Student>,
    private val onStudentListener: OnStudentListener
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    interface OnStudentListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]

        holder.tvName.text = student.name
        holder.tvStudentId.text = student.studentId
        holder.tvMajor.text = student.major

        holder.btnEdit.setOnClickListener {
            onStudentListener.onEditClick(position)
        }

        holder.btnDelete.setOnClickListener {
            onStudentListener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = studentList.size

    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvStudentId: TextView = itemView.findViewById(R.id.tvStudentId)
        val tvMajor: TextView = itemView.findViewById(R.id.tvMajor)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }
}