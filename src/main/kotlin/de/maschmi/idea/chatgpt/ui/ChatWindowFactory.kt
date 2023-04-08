package de.maschmi.idea.chatgpt.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import de.maschmi.idea.chatgpt.service.ChatGptService

class ChatWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Create a JPanel to hold our UI components
        val content = createToolWindow(toolWindow)
        toolWindow.contentManager.addContent(content)
    }

    private fun createToolWindow(toolWindow: ToolWindow): Content {
        val askService = ChatGptService.getInstance()

        val window = ChatWindow(askService, toolWindow)
        // Add the panel to the tool window content
        val contentFactory = ContentFactory.getInstance()
        return contentFactory.createContent(window.panel, "", false)
    }
}
