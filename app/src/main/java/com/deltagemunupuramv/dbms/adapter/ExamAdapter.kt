package com.deltagemunupuramv.dbms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deltagemunupuramv.dbms.databinding.ItemExamBinding
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType

class ExamAdapter(
    private var exams: List<Exam>,
    private val onExamClick: (Exam) -> Unit,
    private val onEditClick: (Exam) -> Unit,
    private val onDeleteClick: (Exam) -> Unit
) : RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

    inner class ExamViewHolder(private val binding: ItemExamBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(exam: Exam) {
            binding.apply {
                examTitleText.text = exam.title
                examTypeText.text = getExamTypeDisplayName(exam.examType)
                examDateText.text = exam.examDate
                examTimeText.text = if (exam.startTime.isNotEmpty() && exam.endTime.isNotEmpty()) {
                    "${exam.startTime} - ${exam.endTime}"
                } else {
                    exam.duration
                }
                examSubjectText.text = exam.subject
                examVenueText.text = exam.venue
                examStatusChip.text = getStatusDisplayName(exam.status)
                examStatusChip.setChipBackgroundColorResource(getStatusColor(exam.status))
                
                // Set click listeners
                root.setOnClickListener { onExamClick(exam) }
                editButton.setOnClickListener { onEditClick(exam) }
                deleteButton.setOnClickListener { onDeleteClick(exam) }
            }
        }
        
        private fun getExamTypeDisplayName(examType: ExamType): String {
            return when (examType) {
                ExamType.TERM_TEST -> "Term Test"
                ExamType.A_L_RESULTS -> "A/L Results"
                ExamType.O_L_RESULTS -> "O/L Results"
            }
        }
        
        private fun getStatusDisplayName(status: ExamStatus): String {
            return when (status) {
                ExamStatus.SCHEDULED -> "Scheduled"
                ExamStatus.ONGOING -> "Ongoing"
                ExamStatus.COMPLETED -> "Completed"
                ExamStatus.CANCELLED -> "Cancelled"
                ExamStatus.POSTPONED -> "Postponed"
            }
        }
        
        private fun getStatusColor(status: ExamStatus): Int {
            return when (status) {
                ExamStatus.SCHEDULED -> android.R.color.holo_blue_light
                ExamStatus.ONGOING -> android.R.color.holo_green_light
                ExamStatus.COMPLETED -> android.R.color.holo_green_dark
                ExamStatus.CANCELLED -> android.R.color.holo_red_light
                ExamStatus.POSTPONED -> android.R.color.holo_orange_light
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(exams[position])
    }

    override fun getItemCount(): Int = exams.size

    fun updateExams(newExams: List<Exam>) {
        exams = newExams
        notifyDataSetChanged()
    }
} 