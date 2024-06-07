package com.example.caniwatchitapplication.ui.adapter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import com.example.caniwatchitapplication.data.model.TitleIds
import com.example.caniwatchitapplication.databinding.ItemTitlePreviewBinding
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Transformations.Companion.recreate
import com.google.android.material.snackbar.Snackbar

/**
 * Adaptador para los detalles de los títulos resultantes de una búsqueda. Además,
 * define el adaptador correspondiente a las plataformas en que el título se encuentra disponible.
 *
 * Características:
 *
 *      - Items clicables.
 *
 * @param owner Vista propietaria del RecyclerView que implementa el adaptador
 * @param viewModel La VistaModelo de la aplicación
 * @param lifecycleOwner Propietario del ciclo de vida del adaptador
 *
 * @see com.example.caniwatchitapplication.ui.adapter.TitleStreamingSourcesAdapter
 */
class TitlesAdapter(
    private val owner: View,
    private val viewModel: AppViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<TitleDetailsResponse, TitlesAdapter.TitlesViewHolder>(
    DiffUtilItemCallBack
)
{
    inner class TitlesViewHolder(itemView: View) : ViewHolder(itemView)

    object DiffUtilItemCallBack: DiffUtil.ItemCallback<TitleDetailsResponse>(){
        override fun areItemsTheSame(
            oldItem: TitleDetailsResponse,
            newItem: TitleDetailsResponse
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: TitleDetailsResponse,
            newItem: TitleDetailsResponse
        ): Boolean = oldItem == newItem
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
        val titleDetails = currentList[position]

        // Set titles image and data, and set clicks functionality
        holder.itemView.apply {

            val posterUrl = titleDetails.posterUrl
            if (!posterUrl.isNullOrEmpty()) {
                Glide.with(this).load(posterUrl).into(binding.ivTitleImage)
            }
            
            binding.tvTitle.text = titleDetails.title
            binding.tvYear.text = titleDetails.year.toString()
            binding.tvUserRating.text = context.getString(R.string.user_score, titleDetails.userRating)
            binding.tvPlotOverview.text = titleDetails.plotOverview
            
            setOnClickListener {
                onItemClickListener?.let {
                    it(TitleIds(titleDetails.id, titleDetails.imdbId))
                }
            }
        }
        
        val titleStreamingSourcesAdapter = TitleStreamingSourcesAdapter(
            viewModel.subscribedStreamingSources,
            lifecycleOwner
        )

        // Setup streaming sources displayer where the title is available in
        binding.streamingSourcesDisplayer.rvStreamingSources.apply {

            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = titleStreamingSourcesAdapter
        }

        // Update streaming sources displayer where the title is available in
        viewModel.availableStreamingSources.observe(lifecycleOwner) { resource ->

            resource.data?.let { allAvailableSources ->
                val titleSources =
                    titleDetails.streamingSources?.recreate(allAvailableSources)
                
                binding.tvTitleHasNoStreamingSources.visibility =
                    if (titleSources.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE

                titleStreamingSourcesAdapter.setupOnItemClickListener { titleUrl ->
                    openTitleUrl(titleUrl)
                }

                titleStreamingSourcesAdapter.submitList(titleSources)
            }
        }
    }

    /**
     * Establece el listener para los clics en los títulos resultantes de una búsqueda específica.
     *
     * @param listener Función Unit que proporciona los identificadores únicos del título clicado
     */
    fun setupOnItemClickListener(listener: ((TitleIds) -> Unit))
    {
        onItemClickListener = listener
    }

    private var onItemClickListener: ((TitleIds) -> Unit)? = null

    /**
     * Abre el enlace a un título específico en la plataforma de streaming correspondiente.
     * Si la aplicación de la plataforma está instalada en el sistema esta abrirá el enlace, de
     * lo contrario se abrirá en el navegador por defecto.
     *
     * @param titleUrl Enlace a un título específico
     */
    private fun openTitleUrl(titleUrl: String)
    {
        val context = owner.context
        val uri = Uri.parse(titleUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(
                owner,
                "No ha sido posible abrir el enlace: $titleUrl",
                Snackbar.LENGTH_LONG
            ).apply {
                anchorView = owner.rootView.findViewById(R.id.bottomNavigationView)
            }.show()
        }
    }
}