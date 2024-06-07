package com.example.caniwatchitapplication.ui.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.data.model.TitleStreamingSource
import com.example.caniwatchitapplication.databinding.ItemStreamingSourcePreviewBinding
import com.example.caniwatchitapplication.util.Constants

/**
 * Adaptador para las plataformas de streaming en que un título específico se encuentra disponible.
 *
 * Características:
 *
 *      - Tamaño mínimo para los iconos.
 *      - Items clicables.
 *
 * @param subscribedStreamingSources Lista de las plataformas de streaming suscritas por el usuario
 * @param lifecycleOwner Propietario del ciclo de vida del adaptador
 */
class TitleStreamingSourcesAdapter(
    private val subscribedStreamingSources: LiveData<List<StreamingSource>>,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<TitleStreamingSource, TitleStreamingSourcesAdapter.TitleStreamingSourceViewHolder>(
    DiffUtilItemCallBack
)
{
    inner class TitleStreamingSourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    object DiffUtilItemCallBack: DiffUtil.ItemCallback<TitleStreamingSource>(){
        override fun areItemsTheSame(
            oldItem: TitleStreamingSource,
            newItem: TitleStreamingSource
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: TitleStreamingSource,
            newItem: TitleStreamingSource
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleStreamingSourceViewHolder
    {
        return TitleStreamingSourceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_streaming_source_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TitleStreamingSourceViewHolder, position: Int)
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
            if (!streamingSource.logoUrl.isNullOrEmpty()) {
                Glide.with(context).load(streamingSource.logoUrl).into(binding.ivLogo)
            }

            setOnClickListener {
                onItemClickListener?.let { onItemClickHandler ->
                    onItemClickHandler(streamingSource.titleUrl ?: "")
                }
            }
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

    /**
     * Establece el listener para los clics en las plataformas de streaming.
     *
     * @param listener Función Unit que proporciona la url del título en la plataforma de streaming
     * clicada
     */
    fun setupOnItemClickListener(listener: ((titleUrl: String) -> Unit))
    {
        onItemClickListener = listener
    }

    private var onItemClickListener: ((webUrl: String) -> Unit)? = null
}