package com.deltagemunupuramv.dbms.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.deltagemunupuramv.dbms.R
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class PersonalDetailsFragment : Fragment() {
    
    // UI Components
    lateinit var dateOfBirthEditText: TextInputEditText
    lateinit var genderRadioGroup: RadioGroup
    lateinit var maritalStatusSpinner: AutoCompleteTextView
    lateinit var spouseDetailsLayout: LinearLayout
    lateinit var spouseNameEditText: TextInputEditText
    lateinit var spouseAddressEditText: TextInputEditText
    lateinit var spouseTelephoneEditText: TextInputEditText
    lateinit var emergencyContactNameEditText: TextInputEditText
    lateinit var emergencyContactPhoneEditText: TextInputEditText
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_details, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupDropdowns()
        setupDatePicker()
        setupMaritalStatusListener()
    }
    
    private fun initializeViews(view: View) {
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText)
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup)
        maritalStatusSpinner = view.findViewById(R.id.maritalStatusSpinner)
        spouseDetailsLayout = view.findViewById(R.id.spouseDetailsLayout)
        spouseNameEditText = view.findViewById(R.id.spouseNameEditText)
        spouseAddressEditText = view.findViewById(R.id.spouseAddressEditText)
        spouseTelephoneEditText = view.findViewById(R.id.spouseTelephoneEditText)
        emergencyContactNameEditText = view.findViewById(R.id.emergencyContactNameEditText)
        emergencyContactPhoneEditText = view.findViewById(R.id.emergencyContactPhoneEditText)
    }
    
    private fun setupDropdowns() {
        val maritalStatusArray = resources.getStringArray(R.array.marital_status_array)
        val maritalStatusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, maritalStatusArray)
        maritalStatusSpinner.setAdapter(maritalStatusAdapter)
        maritalStatusSpinner.setText("Single", false)
    }
    
    private fun setupDatePicker() {
        dateOfBirthEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateOfBirthEditText.setText(date)
            }, year, month, day).show()
        }
    }
    
    private fun setupMaritalStatusListener() {
        maritalStatusSpinner.setOnItemClickListener { _, _, _, _ ->
            val selectedStatus = maritalStatusSpinner.text.toString()
            spouseDetailsLayout.visibility = if (selectedStatus == "Married") View.VISIBLE else View.GONE
        }
    }
    
    fun getDateOfBirth(): String = dateOfBirthEditText.text?.toString()?.trim() ?: ""
    fun getGender(): String {
        return when (genderRadioGroup.checkedRadioButtonId) {
            R.id.maleRadioButton -> "Male"
            R.id.femaleRadioButton -> "Female"
            else -> ""
        }
    }
    fun getMaritalStatus(): String = maritalStatusSpinner.text?.toString()?.trim() ?: ""
    fun getSpouseName(): String = spouseNameEditText.text?.toString()?.trim() ?: ""
    fun getSpouseAddress(): String = spouseAddressEditText.text?.toString()?.trim() ?: ""
    fun getSpouseTelephone(): String = spouseTelephoneEditText.text?.toString()?.trim() ?: ""
    fun getEmergencyContactName(): String = emergencyContactNameEditText.text?.toString()?.trim() ?: ""
    fun getEmergencyContactPhone(): String = emergencyContactPhoneEditText.text?.toString()?.trim() ?: ""
} 