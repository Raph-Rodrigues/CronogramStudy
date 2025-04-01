package view;

import controller.CronogramController;
import model.Materia;
import model.Topic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {
    private CronogramController controller;
    private JTable subjectTable;

    public MainFrame()
    {
        super("Cronograma de Estudos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Tela de loading
        LoadScreen loading = new LoadScreen(this);
        loading.setVisible(true);

        // Conexão com banco em background
        new SwingWorker<Connection, Void>()
        {
            @Override
            protected Connection doInBackground() throws Exception {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:cronograma.db");
                controller = new CronogramController(conn);
                controller.initDatabase();
                return conn;
            }

            @Override
            protected void done() {
                loading.dispose();
                initUI();
                loadSubjects();
                setVisible(true);
            }
        }.execute();
    }

    private void initUI()
    {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Tabela de matérias
        subjectTable = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Matéria", "Início", "Término", "Progresso"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(subjectTable);

        // Botões
        JButton btnNova = new JButton("Nova Matéria");
        btnNova.addActionListener(e -> showMateriaDialog(null));

        JButton btnResumo = new JButton("Ver Resumo");
        btnResumo.addActionListener(e -> showResumoDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnNova);
        buttonPanel.add(btnResumo);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void showMateriaDialog(Materia materia)
    {
        MateriaDialog dialog = new MateriaDialog(this, materia);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                controller.salvarMateria(dialog.getMateria());
                loadSubjects();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
            }
        }
    }

    private void showResumoDialog()
    {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) subjectTable.getValueAt(selectedRow, 0);
            try {
                Materia materia = controller.buscarMateriaPorId(id);
                new SummaryDialog(this, materia.getResume()).setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar resumo");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma matéria");
        }
    }

    private void loadSubjects() {
        DefaultTableModel model = (DefaultTableModel) subjectTable.getModel();
        model.setRowCount(0);

        try {
            controller.listarTodasMaterias().forEach(m -> {
                model.addRow(new Object[]{
                        m.getId(),
                        m.getName(),
                        m.getStartDate(),
                        m.getEndDate(),
                        String.format("%.1f%%", m.getProgress())
                });
            });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar matérias");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}