package com.example.caniwatchitapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.model.StreamingSource

/**
 * Implementa la funcionalidad base de los adaptadores para las plataformas de streaming.
 */
abstract class BaseStreamingSourcesAdapter : ListAdapter<StreamingSource,
        BaseStreamingSourcesAdapter.BaseStreamingSourcesViewHolder>(DiffUtilItemCallBack)
{
    inner class BaseStreamingSourcesViewHolder(view: View) : RecyclerView.ViewHolder(view)

    object DiffUtilItemCallBack : DiffUtil.ItemCallback<StreamingSource>()
    {
        override fun areItemsTheSame(
            oldItem: StreamingSource,
            newItem: StreamingSource
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: StreamingSource,
            newItem: StreamingSource
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseStreamingSourcesViewHolder
    {
        return BaseStreamingSourcesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_streaming_source_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseStreamingSourcesViewHolder, position: Int)
    {
        // Must be implemented by the children
    }
}