package com.example.caniwatchitapplication.ui.view.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.caniwatchitapplication.BuildConfig
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.DialogAppUpdateBinding
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.UpdateManager
import kotlinx.coroutines.launch
import java.io.File

/**
 * Diálogo gestor de las actualizaciones de la aplicación.
 *
 * Se inicia la comprobación de actualizaciones al mostrar el diálogo.
 *
 * @see com.example.caniwatchitapplication.util.UpdateManager
 */
class UpdateDialog(private val ownerActivity: MainActivity): Dialog(ownerActivity)
{
    private val binding: DialogAppUpdateBinding =
        DialogAppUpdateBinding.inflate(LayoutInflater.from(context))
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ownerActivity.appViewModel
        setCancelable(false)
    }

    override fun show()
    {
        super.show()
        checkForUpdates()
    }

    /**
     * Comprueba si existe una nueva versión para la aplicación y gestiona su actualización con
     * previa aprobación del usuario.
     */
    private fun checkForUpdates() = ownerActivity.lifecycleScope.launch {

        try {

            when (val resource = viewModel.fetchAppLatestRelease()) {

                is Resource.Success -> {
                    resource.data?.let { releaseInfo ->

                        if (!releaseInfo.isWhole()) {
                            throw Exception("La información del paquete de actualización está incompleta")
                        }

                        if (isNewVersionAvailable(releaseInfo.versionCode ?: 0)) {
                            setStatus(
                                context.getString(
                                    R.string.new_version_available,
                                    releaseInfo.versionName
                                ))
                            setupButtonListeners(
                                UpdateManager(
                                    context,
                                    releaseInfo.downloadUrl!!,
                                    File(
                                        context.getExternalFilesDir(null),
                                        UpdateManager.parsePackageName(releaseInfo.versionName!!)
                                    )
                                )
                            )
                            showButtons()
                        } else {
                            dismiss()
                        }
                    }
                }
                else -> {
                    setCancelable(true)
                    resource.message?.let {
                        setStatus(context.getString(R.string.update_status_error, resource.message))
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            setCancelable(true)
            setStatus(context.getString(R.string.update_status_exception))
        }
    }

    private fun setStatus(status: String)
    {
        binding.tvUpdateStatus.text = status
    }

    private fun setupButtonListeners(updateManager: UpdateManager)
    {
        binding.btnUpdate.setOnClickListener {
            hideButtons()
            ownerActivity.lifecycleScope.launch {
                setStatus(context.getString(R.string.update_status_downloading))
                updateManager.download().join()
                setStatus(context.getString(R.string.update_status_installing))
                updateManager.install()
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showButtons()
    {
        binding.progressBar.visibility = View.GONE
        binding.layoutButtons.visibility = View.VISIBLE
    }

    private fun hideButtons()
    {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.GONE
    }

    /**
     * Compara un código de versión específico con el de la versión actual de la aplicación.
     *
     * @return Verdadero si la versión especificada es superior a la actual, falso en su defecto.
     */
    private fun isNewVersionAvailable(latestVersion: Int): Boolean
    {
        return BuildConfig.VERSION_CODE < latestVersion
    }
}