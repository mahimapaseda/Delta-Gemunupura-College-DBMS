package com.deltagemunupuramv.dbms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deltagemunupuramv.dbms.databinding.ItemAlResultBinding
import com.deltagemunupuramv.dbms.model.Exam

class ALExamAdapter(
    private var alExams: List<Exam>,
    private val onALExamClick: (Exam) -> Unit,
    private val onEditClick: (Exam) -> Unit,
    private val onDeleteClick: (Exam) -> Unit
) : RecyclerView.Adapter<ALExamAdapter.ALExamViewHolder>() {

    inner class ALExamViewHolder(private val binding: ItemAlResultBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(exam: Exam) {
            binding.apply {
                // Set the main title and subtitle
                titleText.text = exam.fullName
                subtitleText.text = "Index: ${exam.indexNo} | NIC: ${exam.nicNo}"

                // Concatenate other details for the detailsText
                val details = StringBuilder().apply {
                    append("Exam Year: ${exam.examYear}\n")
                    append("Attempt No: ${exam.attemptNo}\n")
                    append("Gender: ${exam.gender}\n")
                    append("Medium: ${exam.medium}\n")
                    append("Subject Stream: ${exam.subjectStream}\n")
                    append("Subject 1: ${exam.subjectNo1} (${exam.subjectNo1Grade})\n")
                    append("Subject 2: ${exam.subjectNo2} (${exam.subjectNo2Grade})\n")
                    append("Subject 3: ${exam.subjectNo3} (${exam.subjectNo3Grade})\n")
                    append("Average Z-Score: ${exam.averageZScore}\n")
                    append("District Rank: ${exam.districtRank}\n")
                    append("Island Rank: ${exam.islandRank}\n")
                    append("General English Grade: ${exam.generalEnglishGrade}\n")
                    append("Common General Test Marks: ${exam.commonGeneralTestMarks}")
                }
                detailsText.text = details.toString()

                // Set click listeners
                root.setOnClickListener { onALExamClick(exam) }
                editButton.setOnClickListener { onEditClick(exam) }
                deleteButton.setOnClickListener { onDeleteClick(exam) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ALExamViewHolder {
        val binding = ItemAlResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ALExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ALExamViewHolder, position: Int) {
        holder.bind(alExams[position])
    }

    override fun getItemCount(): Int = alExams.size

    fun updateALExams(newALExams: List<Exam>) {
        alExams = newALExams
        notifyDataSetChanged()
    }
} 