package com.deltagemunupuramv.dbms.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deltagemunupuramv.dbms.databinding.ItemAssetBinding
import com.deltagemunupuramv.dbms.model.Asset

class AssetAdapter(
    private var assets: List<Asset>,
    private val onItemClick: (Asset) -> Unit,
    private val onEditClick: (Asset) -> Unit,
    private val onDeleteClick: (Asset) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    inner class AssetViewHolder(private val binding: ItemAssetBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(asset: Asset) {
            binding.apply {
                assetNameText.text = asset.name
                assetTypeText.text = asset.type
                assetCategoryText.text = asset.category
                assetLocationText.text = asset.location
                assetStatusText.text = asset.status
                assetAssignedToText.text = if (asset.assignedTo.isNotEmpty()) "Assigned to: ${asset.assignedTo}" else "Not assigned"
                
                // Set status color based on asset status
                val statusColor = when (asset.status.lowercase()) {
                    "available" -> android.graphics.Color.GREEN
                    "in use" -> android.graphics.Color.BLUE
                    "maintenance" -> android.graphics.Color.YELLOW
                    "retired" -> android.graphics.Color.GRAY
                    "lost" -> android.graphics.Color.RED
                    "damaged" -> android.graphics.Color.RED
                    else -> android.graphics.Color.BLACK
                }
                assetStatusText.setTextColor(statusColor)
                
                // Set click listeners
                root.setOnClickListener { onItemClick(asset) }
                editButton.setOnClickListener { onEditClick(asset) }
                deleteButton.setOnClickListener { onDeleteClick(asset) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(assets[position])
    }

    override fun getItemCount(): Int = assets.size

    fun updateAssets(newAssets: List<Asset>) {
        assets = newAssets
        notifyDataSetChanged()
    }
} 