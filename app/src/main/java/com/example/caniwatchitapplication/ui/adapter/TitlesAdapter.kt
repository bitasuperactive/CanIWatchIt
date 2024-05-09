package com.example.caniwatchitapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.databinding.ItemTitlePreviewBinding
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Transformers

class TitlesAdapter(private val viewModel: AppViewModel) : ListAdapter<TitleDetailsResponse, TitlesAdapter.TitlesViewHolder>(DiffUtilCallback)
{
    inner class TitlesViewHolder(itemView: View) : ViewHolder(itemView)
    
    object DiffUtilCallback: DiffUtil.ItemCallback<TitleDetailsResponse>()
    {
        override fun areItemsTheSame(
            oldItem: TitleDetailsResponse,
            newItem: TitleDetailsResponse
        ): Boolean
        {
            return oldItem.id == newItem.id
        }
    
        override fun areContentsTheSame(
            oldItem: TitleDetailsResponse,
            newItem: TitleDetailsResponse
        ): Boolean
        {
            return oldItem == newItem
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitlesViewHolder
    {
        return TitlesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_title_preview,
                parent,
                false
            )
        )
    }
    
    override fun onBindViewHolder(holder: TitlesViewHolder, position: Int)
    {
        val binding = ItemTitlePreviewBinding.bind(holder.itemView)
        val details = currentList[position]
    
        binding.tvTitle.text = details.title
    
        holder.itemView.apply {
            Glide.with(this).load(details.poster).into(binding.ivMovieImage)
            binding.tvTitle.text = details.title
            binding.tvYear.text = details.year.toString()
            binding.tvUserRating.text = context.getString(R.string.user_score, details.userRating)
            binding.tvPlotOverview.text = details.plotOverview
        
            binding.servicesDisplayer.rvSubscribedServices.apply {
                val servicesAdapter = ServicesAdapter(75).apply {
                    viewModel.availableServices.value?.let { resource ->
                        resource.data?.let { serviceResponse ->
                            submitList(Transformers.getServicesFromSources(serviceResponse, details.sources))
                        }
                    }
                }
                adapter = servicesAdapter
                layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            }
            
            this.setOnClickListener {
                onItemClickListener?.let {
                    it(details)
                }
            }
        }
    }
    
    private var onItemClickListener: ((TitleDetailsResponse) -> Unit)? = null
    
    fun setupOnClickListener(listener: ((TitleDetailsResponse) -> Unit))
    {
        onItemClickListener = listener
    }
}