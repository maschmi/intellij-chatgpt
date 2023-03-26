package de.maschmi.idea.chatgpt.service.gpt

import kotlinx.serialization.Serializable

@Serializable
data class GptRequest(val model: GptModel, val messages: List<GptMessage>) {
}