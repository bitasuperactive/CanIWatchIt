package com.example.caniwatchitapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.databinding.ItemStreamingSourcePreviewBinding
import com.example.caniwatchitapplication.util.Constants.Companion.MIN_STREAMING_SOURCE_LOGO_PX_SIZE

class SubscribedStreamingSourcesAdapter : ListAdapter<StreamingSource,
        SubscribedStreamingSourcesAdapter.SubscribedStreamingSourcesViewHolder>(
    DiffUtilItemCallback
)
{
    inner class SubscribedStreamingSourcesViewHolder(view: View) : ViewHolder(view)
    
    object DiffUtilItemCallback : DiffUtil.ItemCallback<StreamingSource>()
    {
        override fun areItemsTheSame(
            oldItem: StreamingSource,
            newItem: StreamingSource
        ): Boolean
        {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(
            oldItem: StreamingSource,
            newItem: StreamingSource
        ): Boolean
        {
            return oldItem == newItem
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribedStreamingSourcesViewHolder
    {
        return SubscribedStreamingSourcesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_streaming_source_preview,
                parent,
                false
            )
        )
    }
    
    override fun onBindViewHolder(holder: SubscribedStreamingSourcesViewHolder, position: Int)
    {
        val binding = ItemStreamingSourcePreviewBinding.bind(holder.itemView)
        val service = currentList[position]
        
        binding.ivLogo.apply {
            maxHeight = MIN_STREAMING_SOURCE_LOGO_PX_SIZE
            maxWidth = MIN_STREAMING_SOURCE_LOGO_PX_SIZE
        }
        
        holder.itemView.apply {
            Glide.with(context).load(service.logoUrl).into(binding.ivLogo)
        }
    }
}