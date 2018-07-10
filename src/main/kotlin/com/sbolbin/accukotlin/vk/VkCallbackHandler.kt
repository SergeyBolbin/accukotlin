package com.sbolbin.accukotlin.vk

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.objects.messages.Message
import org.slf4j.LoggerFactory
import java.util.*

class VkCallbackHandler(client: VkApiClient?, val actor: GroupActor?) : CallbackApiLongPoll(client, actor) {

    val log = LoggerFactory.getLogger(VkCallbackHandler::class.java)
    val random = Random()

    override fun messageNew(groupId: Int?, message: Message?) {
        log.info("New : ${message}")
        client.messages()
                .send(actor)
                .message("Thank You!")
                .userId(message?.userId)
                .randomId(random.nextInt())
                .execute()
    }
}