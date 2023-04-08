package de.maschmi.idea.chatgpt.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindow
import de.maschmi.idea.chatgpt.chatgpt.getAnswer
import de.maschmi.idea.chatgpt.chatgpt.gpt.GptRole
import de.maschmi.idea.chatgpt.service.ChatGptService
import de.maschmi.idea.chatgpt.service.storage.ChatMessage
import de.maschmi.idea.chatgpt.service.storage.ConversationStorageService
import de.maschmi.idea.chatgpt.ui.actionpane.ActionPane
import de.maschmi.idea.chatgpt.ui.actionpane.DetailsRow
import de.maschmi.idea.chatgpt.ui.conversationpane.ConversationPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.datetime.Instant
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.JPanel
import kotlin.coroutines.CoroutineContext

typealias ActionPaneCallback = (ActionEvent, ActionPane) -> Unit
typealias OutputPaneCallback = (ActionEvent, ConversationPane) -> Unit

class ChatWindow(
    private val chatService: ChatGptService,
    private val toolWindow: ToolWindow,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {


    private val actionPane: ActionPane
    private val conversationPane: ConversationPane
    private val chat: MutableList<ChatMessage>

    val panel: JPanel = JPanel(BorderLayout())

    init {
        // Create a JPanel to hold our UI components

        val sendCallback: ActionPaneCallback = { _, ui ->
            sendCallbackImpl(ui)
        }
        val sendByKeyPressed = { ui: ActionPane -> sendCallbackImpl(ui) }
        val clearContextCallback: OutputPaneCallback = { _, ui ->
            run {
                println("Reset pressed")
                chat.clear()
                ui.clearOutput()
                storageService().clear()
            }
        }

        this.conversationPane =
            ConversationPane(clearContextCallback)

        this.actionPane = ActionPane(sendCallback, sendByKeyPressed)
        panel.add(conversationPane.outputPanel, BorderLayout.CENTER)
        panel.add(actionPane.actionPanel, BorderLayout.SOUTH)


        val conversation = storageService().loadConversation()
        this.chat = conversation.toMutableList()
        this.chat.forEach {
            when (it.role) {
                GptRole.USER -> conversationPane.addQuestion(it.content)
                GptRole.SYSTEM -> conversationPane.addQuestion(it.content)
                GptRole.ASSISTANT -> conversationPane.addAnswer(it.content)
            }
        }
    }

    private fun storageService() = toolWindow.project.service<ConversationStorageService>()

    private fun sendCallbackImpl(ui: ActionPane) {
        run {
            val input = ui.text.trimEnd('\n')
            if (input.isNotEmpty()) {
                runQuery(input)
            }
        }
    }

    private fun runQuery(input: String) {
        if (input.isNotEmpty()) {
            conversationPane.addQuestion(input)


            GlobalScope.launch(Dispatchers.Swing) {
                conversationPane.displayLoadingIndicator()
                val userMessage = ChatMessage(GptRole.USER, input)
                chat.add(userMessage)
                actionPane.disableInput()
                val response = chatService.ask(chat.map { it.asGptMessage() }.toList())
                conversationPane.removeLoadingIndicator()

                if (response.isFailure) {
                    chat.remove(userMessage)
                    conversationPane.addError(response.exceptionOrNull()?.message)
                } else {
                    val res = response.getOrThrow()
                    val tokenUsage = res.usage
                    actionPane.clearInput()
                    actionPane.updateQueryDetails(DetailsRow("Model", res.model))
                    actionPane.updateQueryDetails(DetailsRow("Tokens used", tokenUsage.totalTokens.toString()))

                    if (res.getAnswer().isFailure) {
                        conversationPane.addError("Answer was empty!")
                    } else {
                        val answerMessage = ChatMessage(
                            Instant.fromEpochMilliseconds(res.created),
                            res.getAnswer().getOrThrow().role,
                            res.getAnswer().getOrThrow().content
                        )
                        chat.add(answerMessage)
                        conversationPane.addAnswer(answerMessage.content)
                    }
                }
                actionPane.enableInput()
                launch(ioDispatcher) {
                    storageService().storeConversation(chat)
                }
            }
        }
    }

}
