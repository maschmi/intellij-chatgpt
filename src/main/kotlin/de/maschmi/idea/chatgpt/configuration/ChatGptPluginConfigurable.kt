package de.maschmi.idea.chatgpt.configuration

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.*

class ChatGptPluginConfigurable : Configurable {
    private lateinit var apiKeyField: JTextField
    private lateinit var apiKeyLabel: JLabel
    private lateinit var saveApiKeyCheckBox: JCheckBox
    private lateinit var panel: JPanel

    private val myPluginSettings: ChatGptPluginSettingsState = service()

    override fun getDisplayName(): String = "My Plugin Settings"

    override fun createComponent(): JComponent {
        panel = JPanel()
        apiKeyLabel = JLabel("ChatGPT API Key:")
        apiKeyField = JTextField(myPluginSettings.apiKey, 20)
        saveApiKeyCheckBox = JCheckBox("Save API Key", myPluginSettings.saveApiKey)

        panel.add(apiKeyLabel)
        panel.add(apiKeyField)
        panel.add(saveApiKeyCheckBox)

        return panel
    }

    override fun isModified(): Boolean =
        apiKeyField.text != myPluginSettings.apiKey || saveApiKeyCheckBox.isSelected != myPluginSettings.saveApiKey

    override fun apply() {
        if (apiKeyField.text.isNotEmpty()) {
            myPluginSettings.apiKey = apiKeyField.text
        }
        myPluginSettings.saveApiKey = saveApiKeyCheckBox.isSelected
    }

    override fun reset() {
        apiKeyField.text = myPluginSettings.apiKey
        saveApiKeyCheckBox.isSelected = myPluginSettings.saveApiKey
    }

}