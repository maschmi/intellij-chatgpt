package de.maschmi.idea.chatgpt.ui

import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptMessage
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptRole
import de.maschmi.idea.chatgpt.service.ChatGptService
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument


class ChatWindow(val chatService: ChatGptService) {

    val chat = mutableListOf<GptMessage>()
    val panel: JPanel

    init {
        // Create a JPanel to hold our UI components
        this.panel = JPanel(BorderLayout())

        val conversationArea = JPanel()
        conversationArea.layout = BoxLayout(conversationArea, BoxLayout.Y_AXIS)
        val outputArea = JTextPane()
        outputArea.isEditable = false
        outputArea.autoscrolls = true

        conversationArea.add(outputArea)
        val scrollPane = JBScrollPane(conversationArea)
        scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER

        val questionFormat = SimpleAttributeSet()
        StyleConstants.setBold(questionFormat, true)

        panel.add(scrollPane, BorderLayout.CENTER)
        // Create a label to display some text
        val inputField = JTextArea()

        inputField.margin = JBUI.insets(5)
        inputField.lineWrap = true
        val actionPanel = JPanel()
        actionPanel.layout = GridLayout(2,1,1,1)
        actionPanel.add(inputField)


        val resetBtn = JButton()
        resetBtn.text = "Reset"
        resetBtn.addActionListener {
            chat.clear();
            val doc = outputArea.styledDocument
            doc.remove(0, doc.length)
        }
        actionPanel.add(resetBtn)
        panel.add(actionPanel, BorderLayout.SOUTH)


        inputField.addKeyListener(object: KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    inputField.text = inputField.text.trimEnd('\n')
                    val text = inputField.text.trim()
                    if (text.isNotEmpty()) {
                        val doc = outputArea.styledDocument
                        doc.insertString(doc.length, text, questionFormat)
                        addNewLine(doc)

                        GlobalScope.launch(Dispatchers.Swing) {
                            val loading = "Loading...";
                            doc.insertString(doc.length, "Loading...", null)
                            val userMessage = GptMessage(GptRole.USER, text)
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
                            inputField.text = ""
                            inputField.caretPosition = 0
                            inputField.preferredSize = inputField.preferredSize

                            panel.revalidate()
                            panel.repaint()
                        }

                    }
                }
            }

            private fun addNewLine(doc: StyledDocument) {
                doc.insertString(doc.length, "\n", null)
            }


        })
    }



}