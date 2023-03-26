package de.maschmi.idea.chatgpt.service.gpt

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
data class TokenUsage(val prompt_tokens: Int, val completion_tokens: Int, val total_tokens: Int) {

}
