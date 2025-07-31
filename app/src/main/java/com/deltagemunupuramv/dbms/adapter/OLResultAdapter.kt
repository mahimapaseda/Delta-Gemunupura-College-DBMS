package com.deltagemunupuramv.dbms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deltagemunupuramv.dbms.databinding.ItemOlResultBinding
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus

class OLResultAdapter(
    private var olResults: List<Exam>,
    private val onOLResultClick: (Exam) -> Unit,
    private val onEditClick: (Exam) -> Unit,
    private val onDeleteClick: (Exam) -> Unit
) : RecyclerView.Adapter<OLResultAdapter.OLResultViewHolder>() {

    inner class OLResultViewHolder(private val binding: ItemOlResultBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(exam: Exam) {
            binding.apply {
                // Basic Information
                indexNoText.text = "Index No: ${exam.indexNo}"
                fullNameText.text = exam.fullName
                yearText.text = "Year: ${exam.examYear}"
                nicNoText.text = "NIC: ${exam.nicNo}"
                attemptNoText.text = "Attempt: ${exam.attemptNo}"
                genderText.text = exam.gender
                mediumText.text = exam.medium
                religionText.text = exam.religion
                
                // Subject Results
                languageLiteratureText.text = "Language & Literature: ${exam.languageLiterature}"
                englishText.text = "English: ${exam.english}"
                scienceText.text = "Science: ${exam.science}"
                mathematicsText.text = "Mathematics: ${exam.mathematics}"
                historyText.text = "History: ${exam.history}"
                
                // Subject Groups
                firstSubjectGroupText.text = "1st Group: ${exam.firstSubjectGroup}"
                secondSubjectGroupText.text = "2nd Group: ${exam.secondSubjectGroup}"
                thirdSubjectGroupText.text = "3rd Group: ${exam.thirdSubjectGroup}"
                
                // Status
                statusChip.text = getStatusDisplayName(exam.status)
                statusChip.setChipBackgroundColorResource(getStatusColor(exam.status))
                
                // Set click listeners
                root.setOnClickListener { onOLResultClick(exam) }
                editButton.setOnClickListener { onEditClick(exam) }
                deleteButton.setOnClickListener { onDeleteClick(exam) }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OLResultViewHolder {
        val binding = ItemOlResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OLResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OLResultViewHolder, position: Int) {
        holder.bind(olResults[position])
    }

    override fun getItemCount(): Int = olResults.size

    fun updateOLResults(newOLResults: List<Exam>) {
        olResults = newOLResults
        notifyDataSetChanged()
    }
} 