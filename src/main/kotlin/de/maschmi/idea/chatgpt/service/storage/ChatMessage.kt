package de.maschmi.idea.chatgpt.service.storage

import de.maschmi.idea.chatgpt.chatgpt.gpt.GptMessage
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptRole
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(val timestamp: Instant, val role: GptRole, val content: String) {
    constructor(gptMessage: GptMessage) : this(Clock.System.now(), gptMessage.role, gptMessage.content) {}
    constructor(role: GptRole, content: String) : this(Clock.System.now(), role, content) {}

    fun asGptMessage(): GptMessage = GptMessage(role, content)

}