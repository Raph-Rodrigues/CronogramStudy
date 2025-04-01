package view;

import model.Materia;
import model.Topic;
import util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MateriaDialog extends JDialog {
    // Componentes da UI
    private final JTextField txtNome = new JTextField(20);
    private final JSpinner spnDataInicio = new JSpinner(new SpinnerDateModel());
    private final JSpinner spnDataFim = new JSpinner(new SpinnerDateModel());
    private final JTable tblTopicos = new JTable();
    private final List<Topic> topicos = new ArrayList<>();

    // Estado do diálogo
    private boolean confirmed = false;
    private Materia materia;

    public MateriaDialog(JFrame parent, Materia materia) {
        super(parent, materia == null ? "Nova Matéria" : "Editar Matéria", true);
        this.materia = materia;

        if (materia != null) {
            this.topicos.addAll(materia.getTopics());
        }

        configurateComponents();
        loadExistentDatas();
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void configurateComponents() {
        // Configuração dos spinners de data
        spnDataInicio.setEditor(new JSpinner.DateEditor(spnDataInicio, "dd/MM/yyyy"));
        spnDataFim.setEditor(new JSpinner.DateEditor(spnDataFim, "dd/MM/yyyy"));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Painel de informações básicas
        mainPanel.add(createPanelInformation(), BorderLayout.NORTH);

        // Painel de tópicos
        mainPanel.add(createTopicsPanel(), BorderLayout.CENTER);

        // Painel de botões
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createPanelInformation() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("Nome da Matéria:"));
        panel.add(txtNome);
        panel.add(new JLabel("Data de Início:"));
        panel.add(spnDataInicio);
        panel.add(new JLabel("Data de Término:"));
        panel.add(spnDataFim);

        return panel;
    }

    private JPanel createTopicsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tópicos de Estudo"));

        // Configuração da tabela
        tblTopicos.setModel(new DefaultTableModel(
                new Object[]{"Tópico", "Horas", "Prazo", "Concluído"}, 0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Boolean.class : String.class;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblTopicos);

        // Botões de ação
        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> addTopic());

        JButton btnRemover = new JButton("Remover");
        btnRemover.addActionListener(e -> removeTopic());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnAdicionar);
        buttonPanel.add(btnRemover);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> confirmAction());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelAction());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(btnConfirmar);
        panel.add(btnCancelar);

        return panel;
    }

    private void loadExistentDatas() {
        if (materia != null) {
            txtNome.setText(materia.getName());
            spnDataInicio.setValue(DateUtil.toDate(materia.getStartDate()));
            spnDataFim.setValue(DateUtil.toDate(materia.getEndDate()));
            updateTableTopics();
        }
    }

    private void addTopic() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField txtNomeTopico = new JTextField();
        JSpinner spnHoras = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        JSpinner spnPrazo = new JSpinner(new SpinnerDateModel());

        spnPrazo.setEditor(new JSpinner.DateEditor(spnPrazo, "dd/MM/yyyy"));

        panel.add(new JLabel("Nome do Tópico:"));
        panel.add(txtNomeTopico);
        panel.add(new JLabel("Horas Estimadas:"));
        panel.add(spnHoras);
        panel.add(new JLabel("Prazo:"));
        panel.add(spnPrazo);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Novo Tópico",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDate prazo = DateUtil.toLocalDate((Date) spnPrazo.getValue());
                Topic novoTopico = new Topic(
                        txtNomeTopico.getText(),
                        (Integer) spnHoras.getValue(),
                        prazo
                );
                topicos.add(novoTopico);
                updateTableTopics();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Data inválida! Formato correto: dd/MM/aaaa",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeTopic() {
        int selectedRow = tblTopicos.getSelectedRow();
        if (selectedRow >= 0) {
            topicos.remove(selectedRow);
            updateTableTopics();
        }
    }

    private void updateTableTopics() {
        DefaultTableModel model = (DefaultTableModel) tblTopicos.getModel();
        model.setRowCount(0);

        for (Topic topico : topicos) {
            model.addRow(new Object[]{
                    topico.getName(),
                    topico.getHoursEstimated() + "h",
                    DateUtil.format(topico.getDeadline()),
                    topico.isConcluded()
            });
        }
    }

    private void confirmAction() {
        try {
            if (validarCampos()) {
                confirmed = true;
                dispose();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da matéria é obrigatório!");
        }

        LocalDate inicio = DateUtil.toLocalDate((Date) spnDataInicio.getValue());
        LocalDate fim = DateUtil.toLocalDate((Date) spnDataFim.getValue());

        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("A data de término deve ser posterior à data de início!");
        }

        if (topicos.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um tópico!");
        }

        return true;
    }

    private void cancelAction() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Materia getMateria() {
        LocalDate inicio = DateUtil.toLocalDate((Date) spnDataInicio.getValue());
        LocalDate fim = DateUtil.toLocalDate((Date) spnDataFim.getValue());

        Materia novaMateria = new Materia(txtNome.getText().trim(), inicio, fim);
        novaMateria.getTopics().addAll(topicos);

        if (materia != null) {
            novaMateria.setId(materia.getId());
        }

        return novaMateria;
    }
}