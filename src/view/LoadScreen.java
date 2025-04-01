package view;

import javax.swing.*;
import java.awt.*;

public class LoadScreen extends JDialog {
    private final JProgressBar progressBar = new JProgressBar();

    public LoadScreen(JFrame parent)
    {
        super(parent, "", true);
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setUndecorated(true);

        progressBar.setIndeterminate(true);
        progressBar.setString("Carregando...");
        progressBar.setStringPainted(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.CENTER);
        add(panel);
    }

    public void updateProgress(int value, String message) {
        progressBar.setValue(value);
        progressBar.setString(message);
    }
}
