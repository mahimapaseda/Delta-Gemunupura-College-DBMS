package com.deltagemunupuramv.dbms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deltagemunupuramv.dbms.R
import com.deltagemunupuramv.dbms.databinding.ItemStudentBinding
import com.deltagemunupuramv.dbms.model.Student

class StudentAdapter(
    private var students: List<Student>,
    private val onItemClick: (Student) -> Unit,
    private val onEditClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount() = students.size

    fun updateStudents(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.studentName.text = student.fullName
            binding.studentId.text = "Index: ${student.indexNumber}"
            binding.studentClass.text = student.grade

            // Set avatar background based on gender
            val avatarBg = if (student.gender.equals("Male", true)) {
                binding.root.context.getColor(R.color.maroon_primary)
            } else {
                binding.root.context.getColor(R.color.maroon_light)
            }
            binding.studentAvatar.setBackgroundColor(avatarBg)

            // Set click listeners
            binding.root.setOnClickListener {
                onItemClick(student)
            }

            binding.editButton.setOnClickListener {
                onEditClick(student)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClick(student)
            }
        }
    }
} 