package com.example.assessment_asset_telematics.model

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("ActName") val ActName: String,
    @SerializedName("actid") val actid: Int,
    val alterName :String
)

