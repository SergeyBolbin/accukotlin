package com.sbolbin.accukotlin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sbolbin.accukotlin.accuweather.DailyForecast

fun configureObjectMapper(): ObjectMapper {
    val mapper = jacksonObjectMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.propertyNamingStrategy = PropertyNamingStrategy.UPPER_CAMEL_CASE
    return mapper
}

fun forecastToMessageText(forecast: DailyForecast): String {
    return "Погода в Выксе  ${forecast.date}: <br>" +
            "температура: от ${forecast.temperature.minimum.value}${forecast.temperature.minimum.unit} " +
            "до ${forecast.temperature.maximum.value}${forecast.temperature.maximum.unit}, <br>" +
            "днем: ${forecast.day.iconPhrase.toLowerCase()}, <br>" +
            "ночью: ${forecast.night.iconPhrase.toLowerCase()}"
}