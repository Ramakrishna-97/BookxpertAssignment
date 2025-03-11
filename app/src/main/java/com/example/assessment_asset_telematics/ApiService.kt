package com.example.assessment_asset_telematics
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

        @GET("Fillaccounts/nadc/2024-2025")
        fun getAccounts(): Call<ResponseBody>

}