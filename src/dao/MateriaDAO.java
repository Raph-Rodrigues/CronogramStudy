package dao;

import model.Materia;
import model.Topic;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MateriaDAO {
    private Connection connection;

    public MateriaDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:cronograma.db");
            criarTabelas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void criarTabelas() throws SQLException {
        String sqlMaterias = "CREATE TABLE IF NOT EXISTS materias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "data_inicio TEXT, " +
                "data_fim TEXT)";

        String sqlTopicos = "CREATE TABLE IF NOT EXISTS topicos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "materia_id INTEGER, " +
                "nome TEXT NOT NULL, " +
                "horas_estimadas INTEGER, " +
                "deadline TEXT, " +
                "concluido INTEGER, " +
                "FOREIGN KEY(materia_id) REFERENCES materias(id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlMaterias);
            stmt.execute(sqlTopicos);
        }
    }

    // Novo método para resetar sequência de IDs
    public void resetarSequenciaIds() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='materias'");
        }
    }

    // Método atualizado para verificar tabela vazia
    private boolean tabelaEstaVazia() throws SQLException {
        String sql = "SELECT COUNT(*) FROM materias";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1) == 0;
        }
    }

    // Método de remoção atualizado
    public void removerMateria(Materia materia) throws SQLException {
        // Remove tópicos primeiro
        String sqlDeleteTopicos = "DELETE FROM topicos WHERE materia_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlDeleteTopicos)) {
            stmt.setInt(1, materia.getId());
            stmt.executeUpdate();
        }

        // Remove matéria
        String sqlDeleteMateria = "DELETE FROM materias WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlDeleteMateria)) {
            stmt.setInt(1, materia.getId());
            stmt.executeUpdate();
        }

        // Verifica se deve resetar IDs
        if (tabelaEstaVazia()) {
            resetarSequenciaIds();
        }
    }

    // Os demais métodos permanecem iguais (salvarMateria, carregarTodasMaterias, etc.)
    public void salvarMateria(Materia materia) throws SQLException {
        String sql = "INSERT INTO materias (nome, data_inicio, data_fim) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, materia.getNome());
            stmt.setString(2, materia.getDataInicio().toString());
            stmt.setString(3, materia.getDataFim().toString());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    materia.setId(rs.getInt(1));
                }
            }
        }

        salvarTopicos(materia);
    }

    private void salvarTopicos(Materia materia) throws SQLException {
        String sqlDelete = "DELETE FROM topicos WHERE materia_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlDelete)) {
            stmt.setInt(1, materia.getId());
            stmt.executeUpdate();
        }

        String sqlInsert = "INSERT INTO topicos (materia_id, nome, horas_estimadas, deadline, concluido) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
            for (Topic topico : materia.getTopicos()) {
                stmt.setInt(1, materia.getId());
                stmt.setString(2, topico.getNome());
                stmt.setInt(3, topico.getHorasEstimadas());
                stmt.setString(4, topico.getDeadline().toString());
                stmt.setInt(5, topico.isConcluido() ? 1 : 0);
                stmt.executeUpdate();
            }
        }
    }

    public List<Materia> carregarTodasMaterias() throws SQLException {
        List<Materia> materias = new ArrayList<>();
        String sql = "SELECT * FROM materias";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Materia materia = new Materia(
                        rs.getString("nome"),
                        LocalDate.parse(rs.getString("data_inicio")),
                        LocalDate.parse(rs.getString("data_fim"))
                );
                materia.setId(rs.getInt("id"));
                materias.add(materia);
            }
        }

        for (Materia materia : materias) {
            carregarTopicos(materia);
        }

        return materias;
    }

    private void carregarTopicos(Materia materia) throws SQLException {
        String sql = "SELECT * FROM topicos WHERE materia_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materia.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Topic topico = new Topic(
                        rs.getString("nome"),
                        rs.getInt("horas_estimadas"),
                        LocalDate.parse(rs.getString("deadline"))
                );
                topico.setConcluido(rs.getInt("concluido") == 1);
                materia.adicionarTopico(topico);
            }
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}