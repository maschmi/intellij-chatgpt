package de.maschmi.idea.chatgpt.configuration

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable


import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class ChatGptPluginConfigurable : Configurable {
    private lateinit var apiKeyField: JTextField
    private lateinit var apiKeyLabel: JLabel
    private lateinit var panel: JPanel

    private val myPluginSettings: ChatGptPluginSettingsState = service()

    override fun getDisplayName(): String = "My Plugin Settings"

    override fun createComponent(): JComponent {
        panel = JPanel()
        apiKeyLabel = JLabel("ChatGPT API Key:")
        apiKeyField = JTextField(myPluginSettings.apiKey, 20)

        panel.add(apiKeyLabel)
        panel.add(apiKeyField)

        return panel
    }

    override fun isModified(): Boolean =
        apiKeyField.text != myPluginSettings.apiKey

    override fun apply() {
        if (apiKeyField.text.isNotEmpty()) {
            myPluginSettings.apiKey = apiKeyField.text
        }
    }

    override fun reset() {
        apiKeyField.text = myPluginSettings.apiKey
    }

}