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

class ServicesAdapter(
    private val logoPxSize: Int,
    private val subscribedServices: LiveData<List<Service>> = MutableLiveData(),
    private val lifecycleOwner: LifecycleOwner,
    private val isTitleRelated: Boolean
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
        
        binding.ivLogo.apply {
            maxHeight = logoPxSize
            maxWidth = logoPxSize
        }
        
        holder.itemView.apply {
            Glide.with(context).load(service.logo100pxUrl).into(binding.ivLogo)
            
            setOnClickListener {
                onItemClickListener?.let { func ->
                    // Show or hide CheckBox.
                    val oppIsChecked = !binding.cbService.isChecked
                    binding.cbService.isChecked = oppIsChecked
                    binding.cbService.visibility =
                        if (oppIsChecked) View.VISIBLE else View.INVISIBLE
                    
                    func(service, oppIsChecked)
                }
            }
        }
        
        subscribedServices.observe(lifecycleOwner) { subscribedServiceList ->
            val isServiceSubscribed = subscribedServiceList.any { subscribedService ->
                service.logo100pxUrl == subscribedService.logo100pxUrl
            }
            
            binding.cbService.isChecked = isServiceSubscribed
            // We just want the CheckBox to be visible in the RecyclerView that is not related to the titles search.
            binding.cbService.visibility =
                if (!isTitleRelated && isServiceSubscribed) View.VISIBLE else View.INVISIBLE
            
            if (isTitleRelated && !isServiceSubscribed)
            {
                // Gray out the image if the title's service is not subscribed.
                binding.ivLogo.colorFilter = ColorMatrixColorFilter(
                    ColorMatrix().apply { setSaturation(0f) }
                )
            }
        }
    }
    
    private var onItemClickListener: ((Service, Boolean) -> Unit)? = null
    
    fun setupItemOnClickListener(listener: ((Service, Boolean) -> Unit))
    {
        onItemClickListener = listener
    }
}