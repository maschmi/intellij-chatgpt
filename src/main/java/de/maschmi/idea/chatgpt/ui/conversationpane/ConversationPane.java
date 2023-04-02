package de.maschmi.idea.chatgpt.ui.conversationpane;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

public class ConversationPane {
    private JButton clearContextBtn;
    private JTextPane outputTextPane;
    private JPanel outputPanel;

    private final SimpleAttributeSet questionStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet answerStyle = new SimpleAttributeSet();

    private final SimpleAttributeSet loadingStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet errorStyle = new SimpleAttributeSet();

    private static final String LOADING_PLACEHOLDER = "Loading...";

    public ConversationPane(BiConsumer<ActionEvent, ConversationPane> clearCallback) {
        clearContextBtn.addActionListener(e -> clearCallback.accept(e, this));

        StyleConstants.setBold(questionStyle, true);
        StyleConstants.setItalic(loadingStyle, true);

        StyleConstants.setSpaceBelow(answerStyle, StyleConstants.getFontSize(questionStyle) * 1.5f);

        StyleConstants.setBackground(errorStyle, JBColor.RED);
        StyleConstants.setForeground(errorStyle, JBColor.WHITE);
        StyleConstants.setSpaceBelow(errorStyle, StyleConstants.getFontSize(errorStyle) * 1.5f);
    }

    public JPanel getOutputPanel() {
        return outputPanel;
    }

    public void displayLoadingIndicator() throws BadLocationException {
        var doc = outputTextPane.getStyledDocument();
        doc.insertString(doc.getLength(), LOADING_PLACEHOLDER, loadingStyle);
    }

    public void removeLoadingIndicator() throws BadLocationException {
        var doc = outputTextPane.getStyledDocument();
        doc.remove(doc.getLength() - LOADING_PLACEHOLDER.length(), LOADING_PLACEHOLDER.length());
    }

    public void addQuestion(String text) throws BadLocationException {
        var doc = outputTextPane.getStyledDocument();
        doc.insertString(doc.getLength(), text + "\n", questionStyle);
    }

    public void addAnswer(String text) throws BadLocationException {
        var doc = outputTextPane.getStyledDocument();
        doc.insertString(doc.getLength(), text + "\n", answerStyle);
    }

    public void addError(String text) throws BadLocationException {
        var doc = outputTextPane.getStyledDocument();
        doc.insertString(doc.getLength(), "Error: " + text + "\n", errorStyle);
    }

    public void clearOutput() throws BadLocationException {
        var doc = this.outputTextPane.getStyledDocument();
        doc.remove(0, doc.getLength());
    }
}
