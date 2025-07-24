package com.deltagemunupuramv.dbms.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.deltagemunupuramv.dbms.R
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class EmploymentDetailsFragment : Fragment() {
    
    // UI Components
    lateinit var dateOfFirstAppointmentEditText: TextInputEditText
    lateinit var dateOfAppointmentToSchoolEditText: TextInputEditText
    lateinit var classAndGradeEditText: TextInputEditText
    lateinit var previouslyServedSchoolsEditText: TextInputEditText
    lateinit var educationalQualificationsEditText: TextInputEditText
    lateinit var professionalQualificationsEditText: TextInputEditText
    lateinit var appointedSubjectEditText: TextInputEditText
    lateinit var subjectsTaughtEditText: TextInputEditText
    lateinit var gradesTaughtEditText: TextInputEditText
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_employment_details, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupDatePickers()
    }
    
    private fun initializeViews(view: View) {
        dateOfFirstAppointmentEditText = view.findViewById(R.id.dateOfFirstAppointmentEditText)
        dateOfAppointmentToSchoolEditText = view.findViewById(R.id.dateOfAppointmentToSchoolEditText)
        classAndGradeEditText = view.findViewById(R.id.classAndGradeEditText)
        previouslyServedSchoolsEditText = view.findViewById(R.id.previouslyServedSchoolsEditText)
        educationalQualificationsEditText = view.findViewById(R.id.educationalQualificationsEditText)
        professionalQualificationsEditText = view.findViewById(R.id.professionalQualificationsEditText)
        appointedSubjectEditText = view.findViewById(R.id.appointedSubjectEditText)
        subjectsTaughtEditText = view.findViewById(R.id.subjectsTaughtEditText)
        gradesTaughtEditText = view.findViewById(R.id.gradesTaughtEditText)
    }
    
    private fun setupDatePickers() {
        dateOfFirstAppointmentEditText.setOnClickListener { showDatePicker(dateOfFirstAppointmentEditText) }
        dateOfAppointmentToSchoolEditText.setOnClickListener { showDatePicker(dateOfAppointmentToSchoolEditText) }
    }
    
    private fun showDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            editText.setText(date)
        }, year, month, day).show()
    }
    
    fun getDateOfFirstAppointment(): String = dateOfFirstAppointmentEditText.text?.toString()?.trim() ?: ""
    fun getDateOfAppointmentToSchool(): String = dateOfAppointmentToSchoolEditText.text?.toString()?.trim() ?: ""
    fun getClassAndGrade(): String = classAndGradeEditText.text?.toString()?.trim() ?: ""
    fun getPreviouslyServedSchools(): List<String> = previouslyServedSchoolsEditText.text?.toString()?.trim()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    fun getEducationalQualifications(): List<String> = educationalQualificationsEditText.text?.toString()?.trim()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    fun getProfessionalQualifications(): List<String> = professionalQualificationsEditText.text?.toString()?.trim()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    fun getAppointedSubject(): String = appointedSubjectEditText.text?.toString()?.trim() ?: ""
    fun getSubjectsTaught(): List<String> = subjectsTaughtEditText.text?.toString()?.trim()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    fun getGradesTaught(): List<String> = gradesTaughtEditText.text?.toString()?.trim()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
} 