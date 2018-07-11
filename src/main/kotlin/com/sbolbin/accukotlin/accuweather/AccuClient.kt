package com.sbolbin.accukotlin.accuweather

import com.sbolbin.accukotlin.configureObjectMapper
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

class AccuClient(val appKey: String) {
    private val log = LoggerFactory.getLogger(AccuClient::class.java)
    private val client = HttpClients.createDefault()
    private val mapper = configureObjectMapper()
    private val host = "http://dataservice.accuweather.com"

    fun getForecast(cityKey: String): DailyForecast {
        val uri = "$host/forecasts/v1/daily/1day/$cityKey"
        val params = hashMapOf("apikey" to appKey, "metric" to "true", "language" to "ru-ru")
        val response = httpGet(uri, params)
        val dailyPayload = handleResponse(response, DailyPayload::class.java)
        return dailyPayload.dailyForecasts[0]
    }

    fun searchCity(query: String): CitySearchItem? {
        val uri = "$host/locations/v1/cities/search"
        val params = hashMapOf("apikey" to appKey, "q" to query, "language" to "ru-ru")
        val response = httpGet(uri, params)
        val searchItemResults: Array<CitySearchItem> = handleResponse(response, Array<CitySearchItem>::class.java)
        return if (searchItemResults.isEmpty()) null else searchItemResults[0]
    }

    private fun httpGet(uriStr: String, params: Map<String, String>): CloseableHttpResponse {
        val uri = URIBuilder(uriStr)
        for ((name, value) in params) {
            uri.setParameter(name, value)
        }
        return client.execute(HttpGet(uri.build()))
    }

    private fun <T> handleResponse(response: CloseableHttpResponse, clazz: Class<T>): T {
        response.use {
            val statusCode = response.statusLine.statusCode
            if (statusCode == 200) {
                val responseBodyAsString = EntityUtils.toString(response.entity)
                return  mapper.readValue(responseBodyAsString, clazz)
            } else {
                val message = "Accuweather responds with ${response.statusLine}"
                log.error(message)
                throw RuntimeException(message)
            }
        }
    }
}