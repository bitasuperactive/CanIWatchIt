package com.example.caniwatchitapplication.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.caniwatchitapplication.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL

/**
 * Funciones complementarias al diálogo gestor de actualizaciones, encargadas de descargar y
 * lanzar un apk específico.
 *
 * @param context Contexto para la instalación
 * @param downloadUrl Enlace de descarga de la actualización
 * @param outputFile Ruta objetivo para la descarga e instalación de la actualización
 *
 * @see com.example.caniwatchitapplication.ui.view.dialogs.UpdateDialog
 */
class UpdateManager(
    private val context: Context,
    private val downloadUrl: String,
    private val outputFile: File
) {
    companion object
    {
        /**
         * Estandariza el nombre de los paquetes para la aplicación.
         *
         * @param versionName Nombre de la versión
         */
        fun parsePackageName(versionName: String): String = "caniwatchit-v$versionName.apk"
    }

    /**
     * Realiza la descarga de un enlace específico en la ruta predefinida.
     */
    fun download() = CoroutineScope(Dispatchers.IO).launch {

        try {
            val urlConnection = URL(downloadUrl).openConnection()
            urlConnection.connect()

            val inputStream = urlConnection.getInputStream()
            val outputStream = FileOutputStream(outputFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Abre el paquete android de la ruta predefinida.
     *
     * @throws FileNotFoundException Si la ruta predefinida no existe o no es un archivo normal.
     * @throws IllegalArgumentException Si la ruta predefinida no es un paquete android o APK.
     */
    fun install() // TODO - COPY-PASTED
    {
        if (!outputFile.exists() || !outputFile.isFile) {
            throw FileNotFoundException("Provided file path does not exist.")
        }
        if (!outputFile.extension.equals("apk", ignoreCase = true)) {
            throw IllegalArgumentException("Provided file path is not an android package.")
        }

        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            outputFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        context.startActivity(intent)
    }
}