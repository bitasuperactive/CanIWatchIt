package com.example.caniwatchitapplication.ui.adapter

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        val streamingSource = currentList[position]

        // Set minimal logos size
        binding.ivLogo.apply {
            maxHeight = MIN_STREAMING_SOURCE_LOGO_PX_SIZE
            maxWidth = MIN_STREAMING_SOURCE_LOGO_PX_SIZE
        }

        // Set logos image
        holder.itemView.apply {
            binding.tvName.text = streamingSource.name

            Glide.with(context)
                .load(streamingSource.logoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Hide sources name
                        binding.tvName.visibility = View.GONE
                        return false
                    }
                })
                .placeholder(R.mipmap.ic_android)
                .error(R.mipmap.ic_android)
                .into(binding.ivLogo)
        }
    }
}