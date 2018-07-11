package com.sbolbin.accukotlin.vk

import com.sbolbin.accukotlin.accuweather.AccuClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import org.slf4j.LoggerFactory

class VkClient(val groupId: Int, val token: String, val accuClient: AccuClient) {
    private val log = LoggerFactory.getLogger(VkClient::class.java)

    fun runHandler() {
        log.info("Running VK handler...")
        val transportClient = HttpTransportClient.getInstance()
        val vkApiClient = VkApiClient(transportClient)
        val groupActor = GroupActor(groupId, token)

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
}