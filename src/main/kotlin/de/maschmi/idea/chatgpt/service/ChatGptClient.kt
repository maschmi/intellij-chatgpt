package de.maschmi.idea.chatgpt.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import de.maschmi.idea.chatgpt.configuration.ChatGptPluginSettingsState
import de.maschmi.idea.chatgpt.service.gpt.*
import kotlinx.coroutines.future.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class ChatGptClient {

    private val apiBearer: String =  "Bearer " + service<ChatGptPluginSettingsState>().apiKey

    private val client: HttpClient = HttpClient.newHttpClient()
    private val completionUrl = URI("https://api.openai.com/v1/chat/completions")

    suspend fun ask(query: String): Result<String> {
        var request = buildSingleRequest(listOf( GptMessage(GptRole.USER, query)))
        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .await()
        return if (response.statusCode() >= 400) {
            logger<ChatGptClient>().warn("Could not communicate: " + response.statusCode())
            Result.failure(GptClientException("Could not communicate:  " + response.statusCode()))
        } else {
            val content = Json.decodeFromString<GptResponse>(response.body().toString())
            val answer = content.choices.firstOrNull()?.message?.content ?: ""

            if (answer.isNotBlank()) {
                Result.success(answer)
            } else {
                Result.failure(GptClientException("No answer for you!"))
            }
        }


    }

    private fun buildSingleRequest(conversation: List<GptMessage>, model: GptModel = GptModel.GTP_3_5_TURBO): HttpRequest {
        val requestBody = GptRequest(model, conversation)
        logger<ChatGptClient>().debug("Post body: $requestBody")
        return HttpRequest
            .newBuilder()
            .setHeader("Authorization", apiBearer)
            .setHeader("Content-Type", "application/json")
            .uri(completionUrl)
            .POST(HttpRequest.BodyPublishers.ofString(Json.encodeToString(requestBody)))
            .build()
    }


}