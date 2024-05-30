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
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.databinding.ItemStreamingSourcePreviewBinding

class StreamingSourcesAdapter(
    private val logoPxSize: Int,
    private val subscribedStreamingSources: LiveData<List<StreamingSource>> = MutableLiveData(),
    private val lifecycleOwner: LifecycleOwner,
    private val isTitleRelated: Boolean
) : ListAdapter<StreamingSource, StreamingSourcesAdapter.StreamingSourceViewHolder>(DiffUtilItemCallback)
{
    inner class StreamingSourceViewHolder(view: View) : ViewHolder(view)
    
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
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamingSourceViewHolder
    {
        return StreamingSourceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_streaming_source_preview,
                parent,
                false
            )
        )
    }
    
    override fun onBindViewHolder(holder: StreamingSourceViewHolder, position: Int)
    {
        val binding = ItemStreamingSourcePreviewBinding.bind(holder.itemView)
        val streamingSource = currentList[position]
        
        binding.ivLogo.apply {
            maxHeight = logoPxSize
            maxWidth = logoPxSize
        }
        
        holder.itemView.apply {
            Glide.with(context).load(streamingSource.logo100pxUrl).into(binding.ivLogo)
            
            setOnClickListener {
                onItemClickListener?.let { func ->
                    // Show or hide CheckBox.
                    val oppIsChecked = !binding.cbStreamingSource.isChecked
                    binding.cbStreamingSource.isChecked = oppIsChecked
                    binding.cbStreamingSource.visibility =
                        if (oppIsChecked) View.VISIBLE else View.INVISIBLE
                    
                    func(streamingSource, oppIsChecked)
                }
            }
        }
        
        subscribedStreamingSources.observe(lifecycleOwner) { streamingSources ->
            val isSubscribedToSource = streamingSources.any { source ->
                streamingSource.logo100pxUrl == source.logo100pxUrl
            }
            
            binding.cbStreamingSource.isChecked = isSubscribedToSource
            // We just want the CheckBox to be visible in the RecyclerView that is not related to the titles search.
            binding.cbStreamingSource.visibility =
                if (!isTitleRelated && isSubscribedToSource) View.VISIBLE else View.INVISIBLE
            
            if (isTitleRelated && !isSubscribedToSource)
            {
                // Gray out the image if the user is not subscribed to the title's streaming source.
                binding.ivLogo.colorFilter = ColorMatrixColorFilter(
                    ColorMatrix().apply { setSaturation(0f) }
                )
            }
        }
    }
    
    private var onItemClickListener: ((StreamingSource, Boolean) -> Unit)? = null
    
    fun setupItemOnClickListener(listener: ((StreamingSource, Boolean) -> Unit))
    {
        onItemClickListener = listener
    }
}