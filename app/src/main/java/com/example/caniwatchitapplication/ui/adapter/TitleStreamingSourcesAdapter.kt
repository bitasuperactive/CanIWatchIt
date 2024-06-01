package com.example.caniwatchitapplication.ui.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.databinding.ItemStreamingSourcePreviewBinding
import com.example.caniwatchitapplication.util.Constants

/**
 * Adaptador para las plataformas de streaming en que un título específico se encuentra disponible.
 *
 * Características:
 *
 *      - Tamaño mínimo para los iconos.
 *
 * @param subscribedStreamingSources Lista de las plataformas de streaming suscritas por el usuario
 * @param lifecycleOwner Propietario del ciclo de vida del adaptador
 */
class TitleStreamingSourcesAdapter(
    private val subscribedStreamingSources: LiveData<List<StreamingSource>> = MutableLiveData(),
    private val lifecycleOwner: LifecycleOwner
) : BaseStreamingSourcesAdapter()
{
    override fun onBindViewHolder(holder: BaseStreamingSourcesViewHolder, position: Int)
    {
        val binding = ItemStreamingSourcePreviewBinding.bind(holder.itemView)
        val streamingSource = currentList[position]

        // Set logo size
        binding.ivLogo.apply {
            maxHeight = Constants.MIN_STREAMING_SOURCE_LOGO_PX_SIZE
            maxWidth = Constants.MIN_STREAMING_SOURCE_LOGO_PX_SIZE
        }

        // Set logos image
        holder.itemView.apply {
            Glide.with(context).load(streamingSource.logoUrl).into(binding.ivLogo)
        }

        // Set logos color filter
        subscribedStreamingSources.observe(lifecycleOwner) { subscribedStreamingSources ->

            val isSourceSubscribed = subscribedStreamingSources.any { subscribedSource ->
                streamingSource.id == subscribedSource.id
            }

            if (!isSourceSubscribed) {
                // Gray out the image if the user is not subscribed to the titles streaming source
                binding.ivLogo.colorFilter = ColorMatrixColorFilter(
                    ColorMatrix().apply { setSaturation(0f) }
                )
            }
        }
    }
}