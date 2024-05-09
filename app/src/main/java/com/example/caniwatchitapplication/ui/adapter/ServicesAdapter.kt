package com.example.caniwatchitapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.model.Service
import com.example.caniwatchitapplication.databinding.ItemServicePreviewBinding

class ServicesAdapter(
    private val logoPxSize: Int
) : ListAdapter<Service, ServicesAdapter.ServiceViewHolder>(DiffUtilItemCallback)
{
    inner class ServiceViewHolder(view: View) : ViewHolder(view)
    
    object DiffUtilItemCallback : DiffUtil.ItemCallback<Service>()
    {
        override fun areItemsTheSame(
            oldItem: Service,
            newItem: Service
        ): Boolean
        {
            return oldItem.id == newItem.id
        }
    
        override fun areContentsTheSame(
            oldItem: Service,
            newItem: Service
        ): Boolean
        {
            return oldItem == newItem
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder
    {
        return ServiceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_service_preview,
                parent,
                false
            )
        )
    }
    
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int)
    {
        val binding = ItemServicePreviewBinding.bind(holder.itemView)
        val service = currentList[position]
        
        holder.itemView.apply {
            Glide.with(context).load(service.logo100pxUrl).into(binding.ivLogo)
            
            binding.ivLogo.apply {
                maxHeight = logoPxSize
                maxWidth = logoPxSize
            }
    
            setOnClickListener {
                onItemClickListener?.let {
                    // Show/Hide CheckBox.
                    val oppCheck = !binding.cbService.isChecked
                    binding.cbService.isChecked = oppCheck
                    binding.cbService.visibility = if (oppCheck) View.VISIBLE else View.INVISIBLE
                    
                    it(service)
                }
            }
        }
    }
    
    private var onItemClickListener: ((Service) -> Unit)? = null
    
    fun setupOnClickListener(listener: ((Service) -> Unit))
    {
        onItemClickListener = listener
    }
}