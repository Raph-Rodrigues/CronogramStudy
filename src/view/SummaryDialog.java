package view;

import javax.swing.*;
import java.awt.*;

public class SummaryDialog extends JDialog {
    public SummaryDialog(JFrame parent, String content) {
        super(parent, "Resumo", true);

        JTextArea txtResumo = new JTextArea(content);
        txtResumo.setEditable(false);

        add(new JScrollPane(txtResumo));
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}