package de.maschmi.idea.chatgpt.ui

import com.intellij.ui.components.JBScrollPane
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptMessage
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptRole
import de.maschmi.idea.chatgpt.service.ChatGptService
import de.maschmi.idea.chatgpt.ui.actionpane.ActionPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.ScrollPaneConstants
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument
typealias UiCallback = (ActionEvent, ActionPane) -> Unit

class ChatWindow(private val chatService: ChatGptService) {

    private val questionFormat: SimpleAttributeSet
    private val scrollPane: JBScrollPane
    private val outputArea: JTextPane

    private val chat = mutableListOf<GptMessage>()
    val panel: JPanel


    init {
        // Create a JPanel to hold our UI components
        this.panel = JPanel(BorderLayout())

        this.outputArea = JTextPane()
        outputArea.isEditable = false
        outputArea.autoscrolls = false

        this.scrollPane = JBScrollPane(outputArea)
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER

        this.questionFormat = SimpleAttributeSet()
        StyleConstants.setBold(questionFormat, true)

        panel.add(scrollPane, BorderLayout.CENTER)
        // Create a label to display some text

        val sendCallback: UiCallback = { _, ui ->
            run {
                val input = ui.text.trimEnd('\n')
                if (input.isNotEmpty()) {
                    runQuery(input)
                }
            }
        }
        val resetCallback: UiCallback = {_, _ ->
            run {
                println("Reset pressed")
                chat.clear()
                val doc = outputArea.styledDocument
                doc.remove(0, doc.length)
            }
        }


        val actionPane = ActionPane(sendCallback, resetCallback)

        panel.add(actionPane.actionPanel, BorderLayout.SOUTH)
    }

    private fun runQuery(input: String) {
        if (input.isNotEmpty()) {
            val doc = outputArea.styledDocument
            doc.insertString(doc.length, input, questionFormat)
            addNewLine(doc)

            GlobalScope.launch(Dispatchers.Swing) {
                val loading = "Loading...";
                doc.insertString(doc.length, "Loading...", null)
                val userMessage = GptMessage(GptRole.USER, input)
                chat.add(userMessage)
                val response = chatService.ask(chat)
                doc.remove(doc.length - loading.length, loading.length)
                if (response.isFailure) {
                    chat.remove(userMessage)
                    doc.insertString(doc.length, "Error: " + response.exceptionOrNull()?.message, null)
                } else {
                    val message = response.getOrThrow()
                    chat.add(message)
                    doc.insertString(doc.length, message.content, null)
                }
                addNewLine(doc)
                addNewLine(doc)
                scrollPane.verticalScrollBar.value = scrollPane.verticalScrollBar.maximum
            }
        }
    }

    private fun addNewLine(doc: StyledDocument) {
        doc.insertString(doc.length, "\n", null)
    }
}
