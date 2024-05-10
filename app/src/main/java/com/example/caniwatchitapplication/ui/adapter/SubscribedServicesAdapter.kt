package com.example.caniwatchitapplication.ui.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.model.Service
import com.example.caniwatchitapplication.databinding.ItemServicePreviewBinding
import com.example.caniwatchitapplication.util.Constants.Companion.MIN_SERVICE_LOGO_PX_SIZE

class SubscribedServicesAdapter(
) : ListAdapter<Service, SubscribedServicesAdapter.SubscribedServicesViewHolder>(DiffUtilItemCallback)
{
    inner class SubscribedServicesViewHolder(view: View) : ViewHolder(view)
    
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
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribedServicesViewHolder
    {
        return SubscribedServicesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_service_preview,
                parent,
                false
            )
        )
    }
    
    override fun onBindViewHolder(holder: SubscribedServicesViewHolder, position: Int)
    {
        val binding = ItemServicePreviewBinding.bind(holder.itemView)
        val service = currentList[position]
        
        binding.ivLogo.apply {
            maxHeight = MIN_SERVICE_LOGO_PX_SIZE
            maxWidth = MIN_SERVICE_LOGO_PX_SIZE
        }
        
        holder.itemView.apply {
            Glide.with(context).load(service.logo100pxUrl).into(binding.ivLogo)
        }
    }
}