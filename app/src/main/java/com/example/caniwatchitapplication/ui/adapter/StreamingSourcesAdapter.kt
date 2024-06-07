package com.example.caniwatchitapplication.ui.adapter

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.data.model.StreamingSource
import com.example.caniwatchitapplication.databinding.ItemStreamingSourcePreviewBinding
import com.example.caniwatchitapplication.util.Constants

/**
 * Adaptador para las plataformas de streaming disponibles.
 *
 * Características:
 *
 *      - Tamaño máximo para los iconos.
 *      - Items clicables.
 *      - CheckBox indicativo de las plataformas suscritas, se oculta si el item no está seleccionado.
 *
 * @param subscribedStreamingSources Lista de las plataformas de streaming suscritas por el usuario
 * @param lifecycleOwner Propietario del ciclo de vida del adaptador
 */
class StreamingSourcesAdapter(
    private val subscribedStreamingSources: LiveData<List<StreamingSource>>,
    private val lifecycleOwner: LifecycleOwner
) : BaseStreamingSourcesAdapter()
{
    override fun onBindViewHolder(holder: BaseStreamingSourcesViewHolder, position: Int)
    {
        val binding = ItemStreamingSourcePreviewBinding.bind(holder.itemView)
        val streamingSource = currentList[position]

        // Set maximum logo size
        binding.ivLogo.apply {
            maxHeight = Constants.MAX_STREAMING_SOURCE_LOGO_PX_SIZE
            maxWidth = Constants.MAX_STREAMING_SOURCE_LOGO_PX_SIZE
        }

        // Set logos image and click functionality
        holder.itemView.apply {
            Glide.with(context).load(streamingSource.logoUrl).into(binding.ivLogo)

            setOnClickListener {
                onItemClickListener?.let { onItemClickHandler ->

                    // Show/Hide CheckBox
                    val checkToggled: Boolean = !binding.cbStreamingSource.isChecked
                    binding.cbStreamingSource.isChecked = checkToggled
                    binding.cbStreamingSource.visibility =
                        if (checkToggled) View.VISIBLE else View.INVISIBLE

                    // Execute the listeners handler
                    onItemClickHandler(streamingSource, checkToggled)
                }
            }
        }

        // Set CheckBox availability and state, and logos color filter
        subscribedStreamingSources.observe(lifecycleOwner) { subscribedStreamingSources ->

            val isSourceSubscribed = subscribedStreamingSources.any { subscribedSource ->
                streamingSource.id == subscribedSource.id
            }
            binding.cbStreamingSource.isChecked = isSourceSubscribed

            // Only show the CheckBox when its checked
            binding.cbStreamingSource.visibility =
                if (isSourceSubscribed) View.VISIBLE else View.INVISIBLE
        }
    }

    /**
     * Establece el listener para los clics en las plataformas disponibles a suscribir.
     *
     * @param listener Función Unit que proporciona la plataforma de streaming clicada y un
     * booleano indicativo de si la plataforma ha sido suscrita o anulada
     */
    fun setupOnItemClickListener(listener: ((StreamingSource, Boolean) -> Unit))
    {
        onItemClickListener = listener
    }


    private var onItemClickListener: ((StreamingSource, Boolean) -> Unit)? = null
}