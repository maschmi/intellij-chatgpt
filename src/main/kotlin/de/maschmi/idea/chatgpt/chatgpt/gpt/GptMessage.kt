package de.maschmi.idea.chatgpt.chatgpt.gpt

import kotlinx.serialization.Serializable

@Serializable
data class GptMessage(val role: GptRole, val content: String) {
}