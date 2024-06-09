package com.example.caniwatchitapplication.data.model.github

/**
 * Información de una versión específica de la aplicación.
 */
data class AppVersionInfo(
    /**
     * Id de la aplicación.
     */
    val applicationId: String?,

    /**
     * Código de la versión.
     */
    val versionCode: Int?,

    /**
     * Nombre de la versión.
     */
    val versionName: String?,

    /**
     * Tipo de versión (debug/release).
     */
    val buildType: String?,

    /**
     * Enlace de descarga de la versión.
     */
    val downloadUrl: String?,
) {
    override fun toString(): String = "AppVersionInfo(" +
            "applicationId='$applicationId', " +
            "versionCode=$versionCode, " +
            "versionName='$versionName', " +
            "buildType='$buildType', " +
            "downloadUrl='$downloadUrl'" +
            ")"

    /**
     * Comprueba si ninguno de sus atributos son nulos.
     *
     * @return Verdadero si existe algún atributo nulo, falso en su defecto.
     */
    fun isWhole(): Boolean
    {
        if (applicationId == null) return false
        if (versionCode == null) return false
        if (versionName == null) return false
        if (buildType == null) return false
        if (downloadUrl == null) return false

        return true
    }
}