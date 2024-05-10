package com.example.caniwatchitapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.model.TitleDetailsResponse
import com.example.caniwatchitapplication.databinding.ItemTitlePreviewBinding
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Constants.Companion.MIN_SERVICE_LOGO_PX_SIZE
import com.example.caniwatchitapplication.util.Transformers

class TitlesAdapter(
    private val viewModel: AppViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<TitleDetailsResponse, TitlesAdapter.TitlesViewHolder>(DiffUtilCallback)
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
    
        holder.itemView.apply {
            Glide.with(this).load(details.poster).into(binding.ivMovieImage)
    
            binding.tvTitle.text = details.title
            binding.tvYear.text = details.year.toString()
            binding.tvUserRating.text = context.getString(R.string.user_score, details.userRating)
            binding.tvPlotOverview.text = details.plotOverview
            
            setOnClickListener {
                onItemClickListener?.let {
                    it(details)
                }
            }
        }
    
        val servicesAdapter = ServicesAdapter(
            MIN_SERVICE_LOGO_PX_SIZE,
            viewModel.getAllSubscribedServices(),
            lifecycleOwner,
            true
        )
    
        viewModel.availableServices.observe(lifecycleOwner) { resource ->
            resource.data?.let { allAvailableServices ->
                val titleAvailableServices =
                    Transformers.getServicesFromSources(allAvailableServices, details.sources)
                
                binding.tvTitleHasNoServices.visibility =
                    if (titleAvailableServices.isEmpty()) View.VISIBLE else View.INVISIBLE
                servicesAdapter.submitList(titleAvailableServices)
            }
        }
    
        binding.servicesDisplayer.rvSubscribedServices.apply {
        
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = servicesAdapter
        }
    }
    
    private var onItemClickListener: ((TitleDetailsResponse) -> Unit)? = null
    
    fun setupOnClickListener(listener: ((TitleDetailsResponse) -> Unit))
    {
        onItemClickListener = listener
    }
}