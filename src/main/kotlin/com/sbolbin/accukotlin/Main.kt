package com.sbolbin.accukotlin

import com.sbolbin.accukotlin.accuweather.AccuClient
import com.sbolbin.accukotlin.vk.VkClient

fun main(args: Array<String>) {
    val appKeyAccu = "4JSADiDxAxBQIyRK51tzkhl1ch9wW9Ny"
    val vkGroupId = 168559262
    val vkToken = "28545509c2e94d9d668f0e467abbf1841d9ed4650381ea2fe702815b311fdb9f01d022e525eaea9e154b7"

    val accuClient = AccuClient(appKeyAccu)
    val vkClient = VkClient(vkGroupId, vkToken, accuClient)
    vkClient.runHandler()
}