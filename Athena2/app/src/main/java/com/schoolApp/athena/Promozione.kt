package com.schoolApp.athena

data class Promozione(
    val photoUrl: String = "",
    val businessName: String = "",
    val promotion: String = "",
    val addressText: String = "",
    val addressUrl: String = "",
    val menuButtons: Map<String, String> = mapOf()
)
