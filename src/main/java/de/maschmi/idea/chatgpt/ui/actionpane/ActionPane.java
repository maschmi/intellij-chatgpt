package de.maschmi.idea.chatgpt.ui.actionpane;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

public class ActionPane {

    private JTextArea inputArea;
    private JPanel actionPanel;
    private JButton sendBtn;
    private JTable queryDetailsTable;

    public ActionPane(BiConsumer<ActionEvent, ActionPane> sendCallback) {
        sendBtn.addActionListener(e -> sendCallback.accept(e, this));
        var detailsModel = new DefaultTableModel();
        detailsModel.addColumn("Key");
        detailsModel.addColumn("Value");
        queryDetailsTable.setModel(detailsModel);

    }

    public String getText() {
        return inputArea.getText();
    }

    public JPanel getActionPanel() {
        return actionPanel;
    }

    public void updateQueryDetails(DetailsRow detailsRow) {
        var model = (DefaultTableModel) queryDetailsTable.getModel();
        var exitingRow = model.getDataVector()
                .stream()
                .filter(v -> v.elementAt(0).equals(detailsRow.getKey()))
                .findFirst();
        exitingRow.ifPresentOrElse(
                row -> row.setElementAt(detailsRow.getValue(), 1),
                () -> model.addRow(new Object[]{detailsRow.getKey(), detailsRow.getValue()})
        );
        queryDetailsTable.repaint();
    }


}
