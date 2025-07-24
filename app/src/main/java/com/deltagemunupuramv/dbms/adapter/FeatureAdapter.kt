package com.deltagemunupuramv.dbms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deltagemunupuramv.dbms.databinding.ItemFeatureBinding
import com.deltagemunupuramv.dbms.model.Feature

class FeatureAdapter(
    private val features: List<Feature>,
    private val onFeatureClick: (Feature) -> Unit
) : RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val binding = ItemFeatureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        holder.bind(features[position])
    }

    override fun getItemCount(): Int = features.size

    inner class FeatureViewHolder(
        private val binding: ItemFeatureBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFeatureClick(features[position])
                }
            }
        }

        fun bind(feature: Feature) {
            binding.featureIcon.setImageResource(feature.icon)
            binding.featureTitle.text = feature.title
            binding.featureDescription.text = feature.description
            
            // Set alpha based on access level
            binding.root.alpha = if (feature.canView) 1.0f else 0.5f
        }
    }
} 