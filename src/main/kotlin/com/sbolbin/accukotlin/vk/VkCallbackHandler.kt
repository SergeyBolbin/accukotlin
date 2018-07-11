package com.sbolbin.accukotlin.vk

import com.sbolbin.accukotlin.accuweather.AccuClient
import com.sbolbin.accukotlin.forecastToMessageText
import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.objects.messages.Message
import org.slf4j.LoggerFactory
import java.util.*

class VkCallbackHandler(client: VkApiClient?,
                        val actor: GroupActor?,
                        val accuClient: AccuClient) : CallbackApiLongPoll(client, actor) {

    val log = LoggerFactory.getLogger(VkCallbackHandler::class.java)
    val random = Random()

    override fun messageNew(groupId: Int?, message: Message?) {
        log.info("New : ${message}")
        if (message != null) {
            val city = accuClient.searchCity(message.body)
            if (city != null) {
                val forecast = accuClient.getForecast(city.key)
                val text = forecastToMessageText(forecast, city)
                sendMessage(message.userId, text)
            } else {
                sendMessage(message.userId, "Извини, братан, твой город я не нашел :(")
            }
        }
    }

    private fun sendMessage(userId: Int, text: String) {
        client.messages()
                .send(actor)
                .message(text)
                .userId(userId)
                .randomId(random.nextInt())
                .execute()
    }
}