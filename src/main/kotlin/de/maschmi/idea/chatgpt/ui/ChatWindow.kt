package de.maschmi.idea.chatgpt.ui

import de.maschmi.idea.chatgpt.chatgpt.getAnswer
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptMessage
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptRole
import de.maschmi.idea.chatgpt.service.ChatGptService
import de.maschmi.idea.chatgpt.ui.actionpane.ActionPane
import de.maschmi.idea.chatgpt.ui.actionpane.DetailsRow
import de.maschmi.idea.chatgpt.ui.conversationpane.ConversationPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.JPanel

typealias ActionPaneCallback = (ActionEvent, ActionPane) -> Unit
typealias OutputPaneCallback = (ActionEvent, ConversationPane) -> Unit

class ChatWindow(private val chatService: ChatGptService) {


    private val actionPane: ActionPane
    private val conversationPane: ConversationPane
    private val chat = mutableListOf<GptMessage>()

    val panel: JPanel


    init {
        // Create a JPanel to hold our UI components
        this.panel = JPanel(BorderLayout())

        val sendCallback: ActionPaneCallback = { _, ui ->
            run {
                val input = ui.text.trimEnd('\n')
                if (input.isNotEmpty()) {
                    runQuery(input)
                }
            }
        }
        val clearContextCallback: OutputPaneCallback = { _, ui ->
            run {
                println("Reset pressed")
                chat.clear()
                ui.clearOutput()
            }
        }

        this.conversationPane =
            ConversationPane(clearContextCallback)
        this.actionPane = ActionPane(sendCallback)
        panel.add(conversationPane.outputPanel, BorderLayout.CENTER)
        panel.add(actionPane.actionPanel, BorderLayout.SOUTH)
    }

    private fun runQuery(input: String) {
        if (input.isNotEmpty()) {
            conversationPane.addQuestion(input)

            GlobalScope.launch(Dispatchers.Swing) {
                conversationPane.displayLoadingIndicator()
                val userMessage = GptMessage(GptRole.USER, input)
                chat.add(userMessage)
                val response = chatService.ask(chat)
                conversationPane.removeLoadingIndicator()

                if (response.isFailure) {
                    chat.remove(userMessage)
                    conversationPane.addError(response.exceptionOrNull()?.message)
                } else {
                    val res = response.getOrThrow()
                    val tokenUsage = res.usage
                    actionPane.updateQueryDetails(DetailsRow("Model", res.model))
                    actionPane.updateQueryDetails(DetailsRow("Tokens used", tokenUsage.totalTokens.toString()))

                    if (res.getAnswer().isFailure) {
                        conversationPane.addError("Answer was empty!")
                    } else {
                        conversationPane.addAnswer(res.getAnswer().getOrNull()?.content)
                    }
                }
            }
        }
    }

}
