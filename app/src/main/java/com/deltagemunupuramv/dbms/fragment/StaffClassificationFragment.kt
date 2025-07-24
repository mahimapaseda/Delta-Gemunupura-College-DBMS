package com.deltagemunupuramv.dbms.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.deltagemunupuramv.dbms.R

class StaffClassificationFragment : Fragment() {
    
    // UI Components
    lateinit var staffClassificationSpinner: AutoCompleteTextView
    lateinit var statusSpinner: AutoCompleteTextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_staff_classification, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupDropdowns()
    }
    
    private fun initializeViews(view: View) {
        staffClassificationSpinner = view.findViewById(R.id.staffClassificationSpinner)
        statusSpinner = view.findViewById(R.id.statusSpinner)
    }
    
    private fun setupDropdowns() {
        // Setup staff classification dropdown
        val staffClassificationArray = resources.getStringArray(R.array.staff_classification_array)
        val staffClassificationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, staffClassificationArray)
        staffClassificationSpinner.setAdapter(staffClassificationAdapter)
        
        // Setup status dropdown
        val statusArray = resources.getStringArray(R.array.staff_status_array)
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusArray)
        statusSpinner.setAdapter(statusAdapter)
        
        // Set default selections
        staffClassificationSpinner.setText("Academic Staff (Partial Access)", false)
        statusSpinner.setText("Active", false)
    }
    
    fun getStaffClassification(): String = staffClassificationSpinner.text?.toString()?.trim() ?: ""
    fun getStatus(): String = statusSpinner.text?.toString()?.trim() ?: ""
    
    fun validateFields(): Boolean {
        var isValid = true
        
        if (getStaffClassification().isEmpty()) {
            staffClassificationSpinner.error = "Staff classification is required"
            isValid = false
        }
        
        return isValid
    }
} 