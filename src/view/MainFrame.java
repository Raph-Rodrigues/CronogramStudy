package view;

import controller.CronogramController;
import model.Materia;
import model.Topic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainFrame extends JFrame {
    private CronogramController controller;
    private JTable tabelaMaterias;
    private JProgressBar progressoGeral;
    private JButton btnNovaMateria;
    private JButton btnEditarMateria;
    private JButton btnRemoverMateria;
    private JButton btnVerTopicos;

    public MainFrame() {
        super("Cronograma de Estudos - Ciência da Computação");
        this.controller = new CronogramController();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
        carregarMaterias();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Configuração da tabela de matérias
        tabelaMaterias = new JTable(new DefaultTableModel(
                new Object[]{"ID", "Matéria", "Início", "Término", "Progresso"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        });
        tabelaMaterias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabelaMaterias);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovaMateria = new JButton("Nova Matéria");
        btnEditarMateria = new JButton("Editar");
        btnRemoverMateria = new JButton("Remover");
        btnVerTopicos = new JButton("Ver Tópicos");

        buttonPanel.add(btnNovaMateria);
        buttonPanel.add(btnEditarMateria);
        buttonPanel.add(btnRemoverMateria);
        buttonPanel.add(btnVerTopicos);

        // Painel de progresso
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressoGeral = new JProgressBar(0, 100);
        progressPanel.add(new JLabel("Progresso Geral:"), BorderLayout.WEST);
        progressPanel.add(progressoGeral, BorderLayout.CENTER);

        // Layout principal
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);

        add(mainPanel);
        configurarListeners();
    }

    private void configurarListeners() {
        // Listener para nova matéria
        btnNovaMateria.addActionListener(e -> {
            MateriaDialog dialog = new MateriaDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                try {
                    controller.salvarMateria(dialog.getMateria());
                    carregarMaterias();
                } catch (SQLException ex) {
                    showError("Erro de banco de dados: " + ex.getMessage());
                } catch (Exception ex) {
                    showError("Erro inesperado: " + ex.getMessage());
                }
            }
        });

        // Listener para editar matéria
        btnEditarMateria.addActionListener(e -> {
            int selectedRow = tabelaMaterias.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tabelaMaterias.getValueAt(selectedRow, 0);
                try {
                    Materia materia = controller.buscarMateriaPorId(id);
                    MateriaDialog dialog = new MateriaDialog(this, materia);
                    dialog.setVisible(true);
                    if (dialog.isConfirmed()) {
                        controller.atualizarMateria(dialog.getMateria());
                        carregarMaterias();
                    }
                } catch (SQLException ex) {
                    showError("Erro ao editar matéria: " + ex.getMessage());
                } catch (Exception ex) {
                    showError("Erro inesperado: " + ex.getMessage());
                }
            } else {
                showWarning("Selecione uma matéria para editar");
            }
        });

        // Listener para remover matéria (com verificação de reset de IDs)
        btnRemoverMateria.addActionListener(e -> {
            int selectedRow = tabelaMaterias.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Tem certeza que deseja remover esta matéria?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tabelaMaterias.getValueAt(selectedRow, 0);
                    try {
                        controller.removerMateria(id);
                        carregarMaterias();

                        // Feedback visual quando a tabela fica vazia
                        if (tabelaMaterias.getRowCount() == 0) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Todos os registros foram removidos.\nOs IDs serão reiniciados.",
                                    "Informação",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        showError("Erro ao remover matéria: " + ex.getMessage());
                    } catch (Exception ex) {
                        showError("Erro inesperado: " + ex.getMessage());
                    }
                }
            } else {
                showWarning("Selecione uma matéria para remover");
            }
        });

        // Listener para visualizar tópicos
        btnVerTopicos.addActionListener(e -> {
            int selectedRow = tabelaMaterias.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tabelaMaterias.getValueAt(selectedRow, 0);
                try {
                    Materia materia = controller.buscarMateriaPorId(id);
                    exibirTopicosDialog(materia);
                } catch (SQLException ex) {
                    showError("Erro ao carregar tópicos: " + ex.getMessage());
                } catch (Exception ex) {
                    showError("Erro inesperado: " + ex.getMessage());
                }
            } else {
                showWarning("Selecione uma matéria para ver os tópicos");
            }
        });
    }

    private void carregarMaterias() {
        DefaultTableModel model = (DefaultTableModel) tabelaMaterias.getModel();
        model.setRowCount(0);

        try {
            List<Materia> materias = controller.listarTodasMaterias();
            double progressoTotal = 0;

            for (Materia materia : materias) {
                model.addRow(new Object[]{
                        materia.getId(),
                        materia.getNome(),
                        materia.getDataInicio(),
                        materia.getDataFim(),
                        String.format("%.1f%%", materia.getProgresso())
                });
                progressoTotal += materia.getProgresso();
            }

            // Atualiza progresso geral
            if (!materias.isEmpty()) {
                progressoGeral.setValue((int) (progressoTotal / materias.size()));
            } else {
                progressoGeral.setValue(0);
            }
        } catch (SQLException ex) {
            showError("Erro ao carregar matérias: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Erro inesperado: " + ex.getMessage());
        }
    }

    private void exibirTopicosDialog(Materia materia) {
        JDialog dialog = new JDialog(this, "Tópicos de " + materia.getNome(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Tópico", "Horas", "Prazo", "Concluído"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Boolean.class : String.class;
            }
        };

        JTable table = new JTable(model);
        for (Topic topico : materia.getTopicos()) {
            model.addRow(new Object[]{
                    topico.getNome(),
                    topico.getHorasEstimadas() + "h",
                    topico.getDeadline(),
                    topico.isConcluido()
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        JButton btnSalvar = new JButton("Salvar Alterações");

        btnSalvar.addActionListener(e -> {
            for (int i = 0; i < model.getRowCount(); i++) {
                boolean concluido = (boolean) model.getValueAt(i, 3);
                materia.getTopicos().get(i).setConcluido(concluido);
            }

            try {
                controller.atualizarMateria(materia);
                carregarMaterias();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Erro ao salvar alterações: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Erro inesperado: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnSalvar, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Métodos auxiliares para exibição de mensagens
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}