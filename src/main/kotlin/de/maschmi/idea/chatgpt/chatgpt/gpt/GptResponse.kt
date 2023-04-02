package de.maschmi.idea.chatgpt.chatgpt.gpt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GptResponse(
    val id: String,
    val created: Long,
    @SerialName("object") val obj: String,
    val model: String,
    val usage: TokenUsage,
    val choices: List<GptResponseChoice>
) {
}

@Serializable
data class GptResponseChoice(
    val message: GptMessage,
    @SerialName("finish_reason") val finishReason: String,
    val index: Int
) {}

@Serializable
data class TokenUsage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int
) {

}
