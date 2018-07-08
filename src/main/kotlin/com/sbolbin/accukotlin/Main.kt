package com.sbolbin.accukotlin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.*
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse
import com.vk.api.sdk.queries.users.UserField
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.slf4j.LoggerFactory
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

fun main(args: Array<String>) {
    //VKSender.send("c83b4b20ce833819e8", text)
    ///https://github.com/VKCOM/vk-java-sdk/blob/master/examples/hello-bot/src/main/java/com/vk/api/examples/hellobot/RequestHandler.java

    val userId = 2009141
    val text = forecastToMessageText(AccuClient.getForecast())
    VKSender.sendViaBot(userId, text)

//    val server = Server(8080)
//    server.handler = JettyRequestHandler()
//    server.start()
//    server.join()
}

fun forecastToMessageText(forecast: DailyForecast): String {
    return "Погода в Выксе  ${forecast.date}: <br>" +
        "температура: от ${forecast.temperature.minimum.value}${forecast.temperature.minimum.unit} " +
        "до ${forecast.temperature.maximum.value}${forecast.temperature.maximum.unit}, <br>" +
        "днем: ${forecast.day.iconPhrase.toLowerCase()}, <br>" +
        "ночью: ${forecast.night.iconPhrase.toLowerCase()}"
}

class JettyRequestHandler : AbstractHandler() {
    private val log = LoggerFactory.getLogger(JettyRequestHandler::class.java)

    override fun handle(target: String?,
                        baseRequest: Request,
                        request: HttpServletRequest,
                        response: HttpServletResponse) {

        response.contentType = "text/html;charset=utf-8";
        response.status = HttpServletResponse.SC_OK;
        val text = forecastToMessageText(AccuClient.getForecast())
        response.writer.write(text)
        baseRequest.isHandled = true;
    }
}

object AccuClient {
    private val log = LoggerFactory.getLogger(AccuClient::class.java)

    private val appKey = "4JSADiDxAxBQIyRK51tzkhl1ch9wW9Ny"
    private val cityKey = "289481"
    private val mapper = configureObjectMapper()
    private val client = HttpClients.createDefault()

    fun getForecast(): DailyForecast {
        val uri = URIBuilder("http://dataservice.accuweather.com/forecasts/v1/daily/1day/$cityKey")
                .setParameter("apikey", appKey)
                .setParameter("metric", "true")
                .setParameter("language", "ru-ru")
                .build()

        val response = client.execute(HttpGet(uri))

        response.use {
            val statusCode = response.statusLine.statusCode
            if (statusCode == 200) {
                val responseBodyAsString = EntityUtils.toString(response.entity)
                val dailyPayload = mapper.readValue<DailyPayload>(responseBodyAsString)
                return dailyPayload.dailyForecasts[0]
            } else {
                log.error("Accuweather responds with ${response.statusLine}")
                throw RuntimeException()
            }
        }
    }

    private fun configureObjectMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.propertyNamingStrategy = PropertyNamingStrategy.UPPER_CAMEL_CASE
        return mapper
    }
}

object VKSender {
    private val log = LoggerFactory.getLogger(VKSender::class.java)

    private val APP_ID = 6625713
    private val CLIENT_SECRET = "CgarrfGr5ssmW28oDOKK"
    private val REDIRECT_URI = "http://exzaim.ru/callback.php"

    fun send(code: String, text: String) {
        val transportClient = HttpTransportClient.getInstance()
        val vk = VkApiClient(transportClient)

        val random = Random()
        //https://oauth.vk.com/authorize?client_id=6625713&display=page&redirect_uri=http://exzaim.ru/callback&scope=friends&response_type=code&v=5.80
        val authResponse = vk.oauth()
                .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code)
                .execute()

        val actor = UserActor(authResponse.userId, authResponse.accessToken)

        val response: GetFieldsResponse = vk
                .friends()
                .get(actor, UserField.SEX, UserField.SCREEN_NAME, UserField.NICKNAME, UserField.PERSONAL)
                .execute()

        val papa = response.items.find {
            it -> it.firstName.equals("николай", ignoreCase = true) && it.lastName.equals("болбин", ignoreCase = true)
        }

        try {
            vk.messages()
                    .send(actor)
                    .userId(papa?.id)
                    .randomId(random.nextInt())
                    .message(text)
                    .execute()
        } catch (ex: Exception) {
            log.error(ex.message, ex)
        }
    }

    fun getMembers(): List<Int> {
        val transportClient = HttpTransportClient.getInstance()
        val vkApiClient = VkApiClient(transportClient)
        val groupActor = initGroupActor(vkApiClient)

        val groupResponse = vkApiClient
                .groups()
                .getMembers(groupActor)
                .groupId(groupActor.groupId.toString())
                .execute()

        return groupResponse.items
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
        val groupId = 168559262 //Integer.parseInt(properties.getProperty("groupId"))
        val token = "28545509c2e94d9d668f0e467abbf1841d9ed4650381ea2fe702815b311fdb9f01d022e525eaea9e154b7" //properties.getProperty("token")
        val actor = GroupActor(groupId, token)
        return actor
    }
}