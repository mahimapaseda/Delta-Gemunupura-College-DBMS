package com.deltagemunupuramv.dbms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deltagemunupuramv.dbms.R
import com.deltagemunupuramv.dbms.model.Staff

class StaffAdapter(
    private var staffList: List<Staff>,
    private val onItemClick: (Staff) -> Unit,
    private val onEditClick: (Staff) -> Unit,
    private val onDeleteClick: (Staff) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    class StaffViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val departmentTextView: TextView = view.findViewById(R.id.departmentTextView)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
        val editButton: ImageView = view.findViewById(R.id.editButton)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]
        
        holder.nameTextView.text = staff.fullName
        holder.emailTextView.text = staff.email
        holder.departmentTextView.text = staff.appointedSubject.ifEmpty { "Staff Member" }

        if (staff.photoUrl.isNotEmpty()) {
            Glide.with(holder.profileImageView.context)
                .load(staff.photoUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(holder.profileImageView)
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_person)
        }

        holder.itemView.setOnClickListener { onItemClick(staff) }
        holder.editButton.setOnClickListener { onEditClick(staff) }
        holder.deleteButton.setOnClickListener { onDeleteClick(staff) }
    }

    override fun getItemCount() = staffList.size

    fun updateStaffList(newList: List<Staff>) {
        staffList = newList
        notifyDataSetChanged()
    }
} 