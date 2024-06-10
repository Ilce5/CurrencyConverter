package mk.com.sette_clipping.api.service

import mk.com.currencyconverter.api.model.response.Currencies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @GET("latest/USD")
    suspend fun getLatestCurrencies(): Response<Currencies>
}