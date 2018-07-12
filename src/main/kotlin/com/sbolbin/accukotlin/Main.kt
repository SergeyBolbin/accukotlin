package com.sbolbin.accukotlin

import com.sbolbin.accukotlin.accuweather.AccuClient
import com.sbolbin.accukotlin.vk.VkClient

fun main(args: Array<String>) {

    val properties = readPropertiesFromClasspath("dev.accukotlin.properties")
    val accuweatherHost = properties.getProperty("accuweather.api.host")
    val accuweatherKey = properties.getProperty("accuweather.api.key")
    val accuweatherLang = properties.getProperty("accuweather.api.lang")

    val vkGroupId = properties.getProperty("vk.group.id").toInt()
    val vkToken = properties.getProperty("vk.token")

    val accuClient = AccuClient(accuweatherKey, accuweatherHost, accuweatherLang)
    val vkClient = VkClient(vkGroupId, vkToken, accuClient)
    vkClient.runHandler()
}