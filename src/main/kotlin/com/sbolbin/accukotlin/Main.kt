package com.sbolbin.accukotlin

import com.sbolbin.accukotlin.accuweather.AccuClient
import com.sbolbin.accukotlin.vk.VkClient

fun main(args: Array<String>) {
    val vkUserId = 2009141
    val appKeyAccu = "4JSADiDxAxBQIyRK51tzkhl1ch9wW9Ny"
    val cityId = "289481"
    val vkGroupId = 168559262
    val vkToken = "28545509c2e94d9d668f0e467abbf1841d9ed4650381ea2fe702815b311fdb9f01d022e525eaea9e154b7"

    val accuClient = AccuClient(appKeyAccu)
    val text = forecastToMessageText(accuClient.getForecast(cityId))

    val vkClient = VkClient(vkGroupId, vkToken)
    vkClient.sendViaBot(vkUserId, text)
    vkClient.runHandler()
}