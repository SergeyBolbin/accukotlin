package com.sbolbin.accukotlin.accuweather

import com.fasterxml.jackson.annotation.JsonProperty

data class DailyPayload(
        @JsonProperty("DailyForecasts")
        val dailyForecasts: List<DailyForecast>
)

data class Temperature(
        val value: Double,
        val unit: String
)

data class TemperatureRange(
        val minimum: Temperature,
        val maximum: Temperature
)

data class DailyForecast(
        val date: String,
        val epochDate: Long,
        val temperature: TemperatureRange,
        val day: Description,
        val night: Description
)

data class Description(
        @JsonProperty("IconPhrase")
        val iconPhrase: String
)