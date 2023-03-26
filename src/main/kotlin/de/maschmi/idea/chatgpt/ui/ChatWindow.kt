package de.maschmi.idea.chatgpt.ui

import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import de.maschmi.idea.chatgpt.service.ChatGptService
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*


class ChatWindow(val chatService: ChatGptService) {

    val panel: JPanel

    init {
        // Create a JPanel to hold our UI components
        panel = JPanel(BorderLayout())

        val conversationArea = JPanel()
        conversationArea.layout = BoxLayout(conversationArea, BoxLayout.Y_AXIS)
        val scrollPane = JBScrollPane(conversationArea)

        panel.add(scrollPane, BorderLayout.CENTER)
        val labels = mutableListOf<JTextArea>()
        // Create a label to display some text
        val inputField = JTextArea()
        inputField.lineWrap = true
        panel.add(inputField, BorderLayout.SOUTH)

        inputField.addKeyListener(object: KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    val text = inputField.text.trim()
                    if (text.isNotEmpty()) {
                        val questionLabel = addQuestion(text)


                        labels.add(questionLabel)

                        GlobalScope.launch(Dispatchers.Swing) {
                            val response = chatService.ask(text)
                            val answerLabel = if (response.isFailure) {
                                addAnswer("Error: " + response.exceptionOrNull()?.message)
                            } else {
                                addAnswer(response.getOrDefault(""))
                            }
                            labels.forEach {
                                it.preferredSize = it.preferredSize
                            }

                            conversationArea.add(questionLabel)
                            conversationArea.add(answerLabel)
                            scrollPane.verticalScrollBar.value = scrollPane.verticalScrollBar.maximum
                            inputField.text = ""
                            inputField.caretPosition = 0
                            // this is a dirty hack, as it resets the carret position correctly, while setting it 0
                            // leaves it inside row 1

                            panel.revalidate()
                            panel.repaint()
                        }

                    }
                }
            }


        })
    }

    private fun addAnswer(answer: String): JTextArea {
        return createLabel(answer)
    }
    private fun addQuestion(text: String): JTextArea {
        return createLabel(text)
    }

    private fun createLabel(text: String): JTextArea {
        val newLabel = JTextArea(text)
        newLabel.isEnabled = false
        newLabel.lineWrap = true
        newLabel.wrapStyleWord = true

        newLabel.margin = JBUI.insets(10)
        newLabel.border = BorderFactory.createLineBorder(UIManager.getColor("control"))

        //trick to force Swing to recalculate the preferred size
        newLabel.preferredSize = newLabel.preferredSize
        return newLabel
    }


}