package com.example.caniwatchitapplication.ui.adapter

import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.ItemStreamingSourcePreviewBinding
import com.example.caniwatchitapplication.util.Constants.Companion.MIN_STREAMING_SOURCE_LOGO_PX_SIZE

/**
 * Adaptador para las plataformas de streaming suscritas por el usuario.
 *
 * Características:
 *
 *      - Tamaño mínimo para los iconos.
 */
class SubscribedStreamingSourcesAdapter : BaseStreamingSourcesAdapter()
{
    override fun onBindViewHolder(holder: BaseStreamingSourcesViewHolder, position: Int)
    {
        val binding = ItemStreamingSourcePreviewBinding.bind(holder.itemView)
        val service = currentList[position]

        // Set minimal logos size
        binding.ivLogo.apply {
            maxHeight = MIN_STREAMING_SOURCE_LOGO_PX_SIZE
            maxWidth = MIN_STREAMING_SOURCE_LOGO_PX_SIZE
        }

        // Set logos image
        holder.itemView.apply {
            Glide.with(context)
                .load(service.logoUrl)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(binding.ivLogo)
        }
    }
}