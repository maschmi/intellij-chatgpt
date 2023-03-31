package de.maschmi.idea.chatgpt.ui.actionpane;

import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ActionPane {

    private JButton resetBtn;
    private JTextArea inputArea;
    private JPanel actionPanel;
    private JButton sendBtn;

    public ActionPane(BiConsumer<ActionEvent, ActionPane> sendCallback, BiConsumer<ActionEvent, ActionPane> resetCallback) {
        sendBtn.addActionListener(e -> sendCallback.accept(e, this));
        resetBtn.addActionListener(e -> resetCallback.accept(e, this));
    }

    public String getText() {
        return inputArea.getText();
    }

    public JPanel getActionPanel() {
        return actionPanel;
    }


}
