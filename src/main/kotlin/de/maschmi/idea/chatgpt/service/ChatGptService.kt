package de.maschmi.idea.chatgpt.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import de.maschmi.idea.chatgpt.configuration.ChatGptPluginSettingsState

@Service
 class ChatGptService {

    private var client: ChatGptClient = service()

    suspend fun ask(query: String): Result<String> {
        return client.ask(query)
    }
}