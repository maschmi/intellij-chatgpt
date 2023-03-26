package de.maschmi.idea.chatgpt.configuration

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.xmlb.XmlSerializerUtil

@Service
@State(
    name = "org.intellij.sdk.settings.AppSettingsState",
    storages = [Storage("ChatGptInterfacePlugin.xml")]
)
class ChatGptPluginSettingsState : PersistentStateComponent<ChatGptPluginSettingsState> {
    var apiKey: String
        get() = loadApiKey()

        set(value) {
            val pwSafe = service<PasswordSafe>()
            try {
                pwSafe.setPassword(getCredentialsAttributes(), value)
            } catch (ex: Exception) {
                logger<ChatGptPluginSettingsState>().debug("Hups")
            }
        }

    private fun loadApiKey(): String {
        return try {
            service<PasswordSafe>().get(getCredentialsAttributes())?.password.toString()
        } catch (ex: Exception) {
            ""
        }
    }

    var saveApiKey: Boolean = false

    companion object {
        fun getInstance(): ChatGptPluginSettingsState = service()
    }

    override fun getState(): ChatGptPluginSettingsState {
        return getInstance()
    }

    override fun loadState(state: ChatGptPluginSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    private fun getCredentialsAttributes(): CredentialAttributes {
        return CredentialAttributes("ChatGptPluginSettingsState")
    }

}
