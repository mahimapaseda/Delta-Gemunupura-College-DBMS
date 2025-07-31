package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.ActivityViewAlResultBinding
import com.deltagemunupuramv.dbms.manager.ALExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.google.android.material.snackbar.Snackbar

class ViewALResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewAlResultBinding
    private lateinit var alExamManager: ALExamManager
    private var alExam: Exam? = null

    companion object {
        const val EXTRA_AL_RESULT_ID = "extra_al_result_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAlResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupALExamManager()
        loadALExam()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "A/L Result Details"
    }

    private fun setupALExamManager() {
        alExamManager = ALExamManager()
    }

    private fun loadALExam() {
        val alExamId = intent.getStringExtra(EXTRA_AL_RESULT_ID)
        if (alExamId != null) {
            alExamManager.getALExamById(alExamId) { exam ->
                alExam = exam
                exam?.let { populateFields(it) }
            }
        }
    }

    private fun populateFields(alExam: Exam) {
        binding.apply {
            // Set the main title and subtitle
            titleText.text = alExam.fullName
            subtitleText.text = "Index: ${alExam.indexNo} | NIC: ${alExam.nicNo}"
            
            // Set the details
            val details = buildString {
                append("Exam Year: ${alExam.examYear}\n")
                append("Attempt No: ${alExam.attemptNo}\n")
                append("Gender: ${alExam.gender}\n")
                append("Medium: ${alExam.medium}\n")
                append("Subject Stream: ${alExam.subjectStream}\n\n")
                append("Subject 1: ${alExam.subjectNo1} (${alExam.subjectNo1Grade})\n")
                append("Subject 2: ${alExam.subjectNo2} (${alExam.subjectNo2Grade})\n")
                append("Subject 3: ${alExam.subjectNo3} (${alExam.subjectNo3Grade})\n\n")
                append("Average Z-Score: ${alExam.averageZScore}\n")
                append("District Rank: ${alExam.districtRank}\n")
                append("Island Rank: ${alExam.islandRank}\n")
                append("General English Grade: ${alExam.generalEnglishGrade}\n")
                append("Common General Test Marks: ${alExam.commonGeneralTestMarks}")
            }
            detailsText.text = details
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.staff_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_edit -> {
                editALExam()
                true
            }
            R.id.action_delete -> {
                deleteALExam()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editALExam() {
        alExam?.let { exam ->
            val intent = Intent(this, AddALResultActivity::class.java)
            intent.putExtra(AddALResultActivity.EXTRA_AL_RESULT_ID, exam.id)
            startActivity(intent)
        }
    }

    private fun deleteALExam() {
        alExam?.let { exam ->
            AlertDialog.Builder(this)
                .setTitle("Delete A/L Result")
                .setMessage("Are you sure you want to delete the A/L result for ${exam.fullName}?")
                .setPositiveButton("Delete") { _, _ ->
                    alExamManager.deleteALExam(exam) { success ->
                        if (success) {
                            Snackbar.make(binding.root, "A/L result deleted successfully", Snackbar.LENGTH_LONG).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Snackbar.make(binding.root, "Failed to delete A/L result", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alExamManager.cleanup()
    }
} 