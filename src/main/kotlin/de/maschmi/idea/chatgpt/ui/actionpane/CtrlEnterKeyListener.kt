package de.maschmi.idea.chatgpt.ui.actionpane

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class CtrlEnterKeyListener(private val onCtrlEnterCallback: Runnable) : KeyAdapter() {
    override fun keyPressed(e: KeyEvent?) {
        val ctrlPressed = e?.isControlDown ?: false
        val isEnter = e?.keyCode == KeyEvent.VK_ENTER
        if (ctrlPressed && isEnter) {
            onCtrlEnterCallback.run()
        }
    }
}