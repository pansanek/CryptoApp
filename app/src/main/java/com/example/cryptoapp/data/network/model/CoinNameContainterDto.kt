package com.example.cryptoapp.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinNameContainterDto (
    @SerializedName("CoinInfo")
    @Expose
    val coinName: CoinNameDto? = null
)
