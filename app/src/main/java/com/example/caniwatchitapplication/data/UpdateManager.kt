package com.example.caniwatchitapplication.data

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.caniwatchitapplication.BuildConfig
import com.example.caniwatchitapplication.data.api.RetrofitProvider
import retrofit2.Response
import retrofit2.http.GET

/**
 * Conjunto de clases provisional que permite comprobar si existen actualizaciones disponibles
 * para la aplicación.
 *
 * _Estas clases no formarán parte de la versión final del proyecto, su propósito se limita a
 * facilitar el testing durante su desarrollo._
 */
@Deprecated("Utilizar solo para testing.")
class UpdateManager(private val context: Context)
{
    /**
     * Comprueba si existe una nueva versión para la aplicación y pregunta al usuario si
     * desea descargarla.
     *
     * @see UpdateDialog
     */
    suspend fun checkForUpdates()
    {
        try {
            val versionInfo = fetchVersionInfo()

            versionInfo?.let { info ->

                Log.d(TAG, info.toString())

                if (isNewVersionAvailable(info.versionCode)) {
                    UpdateDialog(context, info.downloadUrl).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object
    {
        private const val TAG = "UpdateManager"
        private const val APP_REPOSITORY_BASE_URL = "https://github.com/bitasuperactive/CanIWatchIt/"

        private suspend fun fetchVersionInfo(): AppVersionInfo? {
            return try {
                val retrofit = RetrofitProvider.build(APP_REPOSITORY_BASE_URL)
                val versionInfoApi = retrofit.create(VersionInfoApi::class.java)
                val response = versionInfoApi.getVersionInfo()
                response.body()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun isNewVersionAvailable(latestVersion: Int): Boolean
        {
            return BuildConfig.VERSION_CODE < latestVersion
        }
    }
}

interface VersionInfoApi
{
    /**
     * Recupera el BuildConfig.json del último release de la aplicación en GitHub.
     */
    @GET("releases/latest/download/BuildConfig.json")
    suspend fun getVersionInfo(): Response<AppVersionInfo>
}

/**
 * Información de una versión específica de la aplicación.
 */
data class AppVersionInfo(
    /**
     * Id de la aplicación.
     */
    val applicationId: String,

    /**
     * Código de la versión.
     */
    val versionCode: Int,

    /**
     * Nombre de la versión.
     */
    val versionName: String,

    /**
     * Tipo de versión (debug/release).
     */
    val buildType: String,

    /**
     * Enlace de descarga de la versión.
     */
    val downloadUrl: String,
) {
    override fun toString(): String {
        return "AppVersionInfo(applicationId='$applicationId', versionCode=$versionCode, versionName='$versionName', buildType='$buildType', downloadUrl='$downloadUrl')"
    }
}

/**
 * Permite consultar al usuario si desea descargar la última versión de la aplicación.
 */
private class UpdateDialog(private val context: Context, private val apkDownloadUrl: String)
{
    private val alertDialog: AlertDialog

    init {
        alertDialog = AlertDialog.Builder(context).apply {
            setTitle("Nueva Versión Disponible")
            setMessage("¿Desea actualizar la aplicación?")
            setPositiveButton("Actualizar") { dialog, _ ->
                dialog.dismiss()
                // Aquí puedes abrir el navegador o iniciar la descarga directamente desde la URL
                // Por ejemplo, abrir el navegador con la URL de la actualización
                downloadUpdate()
            }
            setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.create()
    }

    /**
     * Muestra el AlertDialog con la opción de descargar la última versión disponible del apk.
     */
    fun show()
    {
        alertDialog.show()
    }

    /**
     * Abre el url de descarga del apk en el navegador.
     */
    private fun downloadUpdate()
    {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(apkDownloadUrl)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
