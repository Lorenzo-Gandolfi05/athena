package com.schoolApp.athena

data class GruppoAssemblea(
    val titolo: String = "",
    val descrizione: String = "",
    val classe: String = "",
    val relatori: List<String> = listOf(),
    val iscritti: List<String> = listOf(),
    val aperto: Boolean = true,
    val iterazione: Int = 0,
    val limiteMassimo: Int = 0,
    val timestamp: Long = 0L
)
