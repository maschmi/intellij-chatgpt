package de.maschmi.idea.chatgpt.chatgpt

import com.intellij.openapi.diagnostic.logger
import de.maschmi.idea.chatgpt.chatgpt.gpt.*
import kotlinx.coroutines.future.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ChatGptClient(apiKey: String, private val apiUrl: URI) {

    private val apiBearer: String = "Bearer $apiKey"

    private val client: HttpClient = HttpClient.newHttpClient()

    suspend fun ask(query: String): Result<GptResponse> {
        return ask(listOf(GptMessage(GptRole.USER, query)))
    }

    suspend fun ask(chat: List<GptMessage>): Result<GptResponse> {
        val request = buildRequestRequest(chat)
        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .await()
        return if (response.statusCode() >= 400) {
            logger<ChatGptClient>().warn("Could not communicate: " + response.statusCode())
            Result.failure(GptClientException("Could not communicate:  " + response.statusCode()))
        } else {
            val content = Json.decodeFromString<GptResponse>(response.body().toString())
            return Result.success(content)
        }
    }


    private fun buildRequestRequest(
        conversation: List<GptMessage>,
        model: GptModel = GptModel.GTP_3_5_TURBO
    ): HttpRequest {
        val requestBody = GptRequest(model, conversation)
        logger<ChatGptClient>().debug("Post body: $requestBody")
        return HttpRequest
            .newBuilder()
            .setHeader("Authorization", apiBearer)
            .setHeader("Content-Type", "application/json")
            .uri(apiUrl)
            .POST(HttpRequest.BodyPublishers.ofString(Json.encodeToString(requestBody)))
            .build()
    }


}