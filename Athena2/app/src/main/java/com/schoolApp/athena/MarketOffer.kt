package com.schoolApp.athena

import java.util.*

data class MarketOffer(
    var seller: String = "",
    var item: String = "",
    var imageUri: String = "",
    var price: String = "",
    var contact: String = "",
    var used: Boolean = true,
    var uid: String = "",
    var id: String = UUID.randomUUID().toString()
)
