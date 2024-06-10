package mk.com.sette_clipping.api.service

import mk.com.currencyconverter.api.model.response.Currencies
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiManager: ApiService {

    private val apiService: ApiService
    private val apiKey = "f1b030d9fbad4d3abbfe1c69"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/$apiKey/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    override suspend fun getLatestCurrencies(): Response<Currencies> {
        return try {
            val response = apiService.getLatestCurrencies()
            println("getLatestCurrencies code: ${response.code()}")
            response
        }catch (e: Exception) {
            println("Exception: ${e.message}")
            Response.error(860, "".toResponseBody("application/json".toMediaTypeOrNull()))
        }
    }
}