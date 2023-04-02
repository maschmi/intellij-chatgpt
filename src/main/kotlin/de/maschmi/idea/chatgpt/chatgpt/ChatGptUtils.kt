package de.maschmi.idea.chatgpt.chatgpt

import de.maschmi.idea.chatgpt.chatgpt.gpt.GptMessage
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptResponse
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptRole

fun GptResponse.getAnswer(): Result<GptMessage> {
    val answer = this.choices.firstOrNull()?.message?.content ?: ""
    return if (answer.isNotBlank()) {
        Result.success(GptMessage(GptRole.ASSISTANT, answer))
    } else {
        Result.failure(GptClientException("No answer found!"))
    }
}