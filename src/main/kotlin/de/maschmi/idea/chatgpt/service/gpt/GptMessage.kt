package de.maschmi.idea.chatgpt.service.gpt

import kotlinx.serialization.Serializable

@Serializable
data class GptMessage(val role: GptRole, val content: String) {
}