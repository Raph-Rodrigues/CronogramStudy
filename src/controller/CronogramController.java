package controller;

import dao.MateriaDAO;
import model.Materia;
import java.sql.SQLException;
import java.util.List;

public class CronogramController {
    private MateriaDAO materiaDAO;

    public CronogramController() {
        this.materiaDAO = new MateriaDAO();
    }

    public void salvarMateria(Materia materia) throws SQLException {
        materiaDAO.salvarMateria(materia);
    }

    public void atualizarMateria(Materia materia) throws SQLException {
        materiaDAO.salvarMateria(materia);
    }

    public void removerMateria(int id) throws SQLException {
        Materia materia = buscarMateriaPorId(id);
        if (materia != null) {
            materiaDAO.removerMateria(materia); // Chama o método atualizado
        }
    }

    public List<Materia> listarTodasMaterias() throws SQLException {
        return materiaDAO.carregarTodasMaterias();
    }

    public Materia buscarMateriaPorId(int id) throws SQLException {
        List<Materia> materias = materiaDAO.carregarTodasMaterias();
        for (Materia m : materias) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public void close() {
        materiaDAO.close();
    }
}