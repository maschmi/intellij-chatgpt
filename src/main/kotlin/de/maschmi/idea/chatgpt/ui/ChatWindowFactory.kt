package de.maschmi.idea.chatgpt.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import de.maschmi.idea.chatgpt.service.ChatGptService
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class ChatWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Create a JPanel to hold our UI components
        val content = createToolWindow()
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolWindow(): Content {
        val askService = service<ChatGptService>()

        val window = ChatWindow(askService)
        // Add the panel to the tool window content
        val contentFactory = ContentFactory.SERVICE.getInstance()
        return contentFactory.createContent(window.panel, "", false)
    }
}
