package de.maschmi.idea.chatgpt.ui

import com.intellij.ui.components.JBScrollPane
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptMessage
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptRole
import de.maschmi.idea.chatgpt.service.ChatGptService
import de.maschmi.idea.chatgpt.ui.actionpane.ActionPane
import de.maschmi.idea.chatgpt.ui.actionpane.OutputPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.ScrollPaneConstants
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument
typealias ActionPaneCallback = (ActionEvent, ActionPane) -> Unit
typealias OutputPaneCallback = (ActionEvent, OutputPane) -> Unit

class ChatWindow(private val chatService: ChatGptService) {


    private val actionPane: ActionPane
    private val outputPane: OutputPane
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

        this.outputPane = OutputPane(clearContextCallback)
        this.actionPane = ActionPane(sendCallback)
        panel.add(outputPane.outputPanel, BorderLayout.CENTER)
        panel.add(actionPane.actionPanel, BorderLayout.SOUTH)
    }

    private fun runQuery(input: String) {
        if (input.isNotEmpty()) {
            outputPane.addQuestion(input)

            GlobalScope.launch(Dispatchers.Swing) {
                outputPane.displayLoadingIndicator()
                val userMessage = GptMessage(GptRole.USER, input)
                chat.add(userMessage)
                val response = chatService.ask(chat)
                outputPane.removeLoadingIndicator()

                if (response.isFailure) {
                    chat.remove(userMessage)
                    outputPane.addError(response.exceptionOrNull()?.message)
                } else {
                    val message = response.getOrThrow()
                    outputPane.addAnswer(message.content)
                }
            }
        }
    }

}
