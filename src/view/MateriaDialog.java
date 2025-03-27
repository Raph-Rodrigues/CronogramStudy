package view;

import model.Materia;
import model.Topic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MateriaDialog extends JDialog {
    private JTextField txtNome;
    private JFormattedTextField txtDataInicio;
    private JFormattedTextField txtDataFim;
    private JTable tableTopicos;
    private JButton btnAdicionarTopico;
    private JButton btnRemoverTopico;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    private boolean confirmed = false;
    private Materia materia;
    private List<Topic> topicos;

    public MateriaDialog(JFrame parent, Materia materia) {
        super(parent, materia == null ? "Nova Matéria" : "Editar Matéria", true);
        this.materia = materia;
        this.topicos = new ArrayList<>();

        if (materia != null) {
            this.topicos.addAll(materia.getTopicos());
        }

        setSize(600, 500);
        setLocationRelativeTo(parent);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        txtNome = new JTextField(20);

        // Configuração correta dos campos de data
        txtDataInicio = new JFormattedTextField(new java.text.SimpleDateFormat("dd/MM/yyyy"));
        txtDataInicio.setColumns(10);
        txtDataFim = new JFormattedTextField(new java.text.SimpleDateFormat("dd/MM/yyyy"));
        txtDataFim.setColumns(10);

        // Preenche os campos se estiver editando
        if (materia != null) {
            txtNome.setText(materia.getNome());
            txtDataInicio.setValue(Date.from(materia.getDataInicio().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            txtDataFim.setValue(Date.from(materia.getDataFim().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            txtDataInicio.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            txtDataFim.setValue(Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        // Tabela de tópicos
        tableTopicos = new JTable(new DefaultTableModel(
                new Object[]{"Tópico", "Horas Estimadas", "Prazo"}, 0
        ));

        // Botões
        btnAdicionarTopico = new JButton("Adicionar Tópico");
        btnRemoverTopico = new JButton("Remover Tópico");
        btnConfirmar = new JButton("Confirmar");
        btnCancelar = new JButton("Cancelar");

        // Configura listeners
        configurarListeners();

        // Carrega tópicos na tabela
        carregarTopicos();
    }

    private void configurarListeners() {
        btnAdicionarTopico.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(3, 2));

            JTextField txtTopico = new JTextField();
            JSpinner spinnerHoras = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            JFormattedTextField txtPrazo = new JFormattedTextField(
                    new java.text.SimpleDateFormat("dd/MM/yyyy"));
            txtPrazo.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            panel.add(new JLabel("Nome do Tópico:"));
            panel.add(txtTopico);
            panel.add(new JLabel("Horas Estimadas:"));
            panel.add(spinnerHoras);
            panel.add(new JLabel("Prazo:"));
            panel.add(txtPrazo);

            int result = JOptionPane.showConfirmDialog(
                    this, panel, "Novo Tópico",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    Date prazoDate = (Date) txtPrazo.getValue();
                    LocalDate prazo = prazoDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    Topic topico = new Topic(
                            txtTopico.getText(),
                            (int) spinnerHoras.getValue(),
                            prazo
                    );

                    topicos.add(topico);
                    carregarTopicos();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Data inválida!",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRemoverTopico.addActionListener(e -> {
            int selectedRow = tableTopicos.getSelectedRow();
            if (selectedRow >= 0) {
                topicos.remove(selectedRow);
                carregarTopicos();
            }
        });

        btnConfirmar.addActionListener(e -> {
            if (validarCampos()) {
                confirmed = true;
                dispose();
            }
        });

        btnCancelar.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }

    private void carregarTopicos() {
        DefaultTableModel model = (DefaultTableModel) tableTopicos.getModel();
        model.setRowCount(0);

        for (Topic topico : topicos) {
            model.addRow(new Object[]{
                    topico.getNome(),
                    topico.getHorasEstimadas(),
                    topico.getDeadline()
            });
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da matéria",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Date inicioDate = (Date) txtDataInicio.getValue();
            LocalDate inicio = inicioDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            Date fimDate = (Date) txtDataFim.getValue();
            LocalDate fim = fimDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (fim.isBefore(inicio)) {
                JOptionPane.showMessageDialog(this, "A data de término deve ser após a data de início",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (topicos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Adicione pelo menos um tópico de estudo",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Cria ou atualiza a matéria
            if (materia == null) {
                materia = new Materia(txtNome.getText(), inicio, fim);
            } else {
                materia.setNome(txtNome.getText());
                materia.setDataInicio(inicio);
                materia.setDataFim(fim);
            }

            materia.getTopicos().clear();
            materia.getTopicos().addAll(topicos);

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datas inválidas!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de informações da matéria
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.add(new JLabel("Nome da Matéria:"));
        infoPanel.add(txtNome);
        infoPanel.add(new JLabel("Data de Início:"));
        infoPanel.add(txtDataInicio);
        infoPanel.add(new JLabel("Data de Término:"));
        infoPanel.add(txtDataFim);

        // Painel de tópicos
        JPanel topicosPanel = new JPanel(new BorderLayout());
        topicosPanel.setBorder(BorderFactory.createTitledBorder("Tópicos de Estudo"));

        JScrollPane scrollPane = new JScrollPane(tableTopicos);
        topicosPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel topicosButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topicosButtons.add(btnAdicionarTopico);
        topicosButtons.add(btnRemoverTopico);
        topicosPanel.add(topicosButtons, BorderLayout.SOUTH);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnConfirmar);
        buttonPanel.add(btnCancelar);

        // Adiciona tudo ao painel principal
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(topicosPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Materia getMateria() {
        return materia;
    }
}