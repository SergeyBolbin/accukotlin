package com.sbolbin.accukotlin.vk

import com.sbolbin.accukotlin.accuweather.AccuClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import org.slf4j.LoggerFactory
import java.util.*

class VkClient(val groupId: Int, val token: String, val accuClient: AccuClient) {
    private val log = LoggerFactory.getLogger(VkClient::class.java)

    fun runHandler() {
        val transportClient = HttpTransportClient.getInstance()
        val vkApiClient = VkApiClient(transportClient)
        val groupActor = initGroupActor(vkApiClient)

        vkApiClient.groups().setLongPollSettings(groupActor)
                .enabled(true)
                .wallPostNew(true)
                .messageNew(true)
                .messageReply(true)
                .messageAllow(true)
                .execute()

        val handler = VkCallbackHandler(vkApiClient, groupActor, accuClient)
        handler.run()
    }

    fun sendViaBot(userId: Int, text: String) {
        val transportClient = HttpTransportClient.getInstance()
        val vkApiClient = VkApiClient(transportClient)
        val groupActor = initGroupActor(vkApiClient)
        val random = Random()

        vkApiClient.messages()
                .send(groupActor)
                .message(text)
                .userId(userId)
                .randomId(random.nextInt())
                .execute()
    }

    private fun initGroupActor(apiClient: VkApiClient): GroupActor {
        val actor = GroupActor(groupId, token)
        return actor
    }
}