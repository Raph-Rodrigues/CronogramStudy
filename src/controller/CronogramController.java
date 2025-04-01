package controller;

import dao.MateriaDAO;
import model.Materia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CronogramController {
    private final MateriaDAO materiaDAO;

    public CronogramController(Connection connection)
    {
        this.materiaDAO = new MateriaDAO(connection);
    }

    public void initDatabase() throws SQLException {
        materiaDAO.createTables();
    }

    public void salvarMateria(Materia materia) throws SQLException {
        materiaDAO.saveSubject(materia);
    }

    public List<Materia> listarTodasMaterias() throws SQLException {
        return materiaDAO.loadAllSubjects();
    }

    public Materia buscarMateriaPorId(int id) throws SQLException {
        return materiaDAO.loadAllSubjects().stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void removerMateria(int id) throws SQLException {
        materiaDAO.deleteSubject(id);
    }
}