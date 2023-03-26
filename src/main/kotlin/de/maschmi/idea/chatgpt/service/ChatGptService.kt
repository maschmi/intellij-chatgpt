package de.maschmi.idea.chatgpt.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.lilittlecat.chatgpt.offical.ChatGPT
import de.maschmi.idea.chatgpt.configuration.ChatGptPluginSettingsState

@Service
 class ChatGptService {

    private var client: ChatGPT

    init {
        val apiKey = service<ChatGptPluginSettingsState>().apiKey
        client = ChatGPT(apiKey)
    }

    fun ask(query: String) : String {
        val answer = client.ask(query)
        return answer
    }
}