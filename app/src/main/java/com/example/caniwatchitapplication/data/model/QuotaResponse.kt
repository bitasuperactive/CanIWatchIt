package com.example.caniwatchitapplication.data.model

/**
 * Clase de datos que representa la respuesta de la api a la consulta de la cuota restante.
 *
 * @property quota Número total de consultas contratadas por mes
 * @property quotaUsed Número de consultas consumidas para el mes en curso
 */
data class QuotaResponse(
    val quota: Int,
    val quotaUsed: Int
)