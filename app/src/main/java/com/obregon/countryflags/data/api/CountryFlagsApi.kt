package com.obregon.countryflags.data.api


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryFlagsApi {
    @GET("/{countryCode}/{style}/64.png")
    fun getCountryFlag(
        @Path("countryCode") countryCode: String,
        @Path("style") style: String
    ): Call<ResponseBody>

}