package de.maschmi.idea.chatgpt.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import de.maschmi.idea.chatgpt.chatgpt.ChatGptClient
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptMessage
import de.maschmi.idea.chatgpt.configuration.ChatGptPluginSettingsState
import java.net.URI

@Service
 class ChatGptService {

    private val completionUrl = URI("https://api.openai.com/v1/chat/completions")
    private var client: ChatGptClient = ChatGptClient(service<ChatGptPluginSettingsState>().apiKey, completionUrl)

    suspend fun ask(chat: MutableList<GptMessage>): Result<GptMessage> {
        return client.ask(chat)
    }
}