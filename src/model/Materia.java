package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Materia {
    private int id;
    private String nome;
    private List<Topic> topicos;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double progresso;

    public Materia(String nome, LocalDate dataInicio, LocalDate dataFim) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.topicos = new ArrayList<>();
        this.progresso = 0;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Topic> getTopicos() { return topicos; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public double getProgresso() {
        if (topicos.isEmpty()) return 0;
        long concluidos = topicos.stream().filter(Topic::isConcluido).count();
        return (double) concluidos / topicos.size() * 100;
    }

    public void adicionarTopico(Topic topico) {
        topicos.add(topico);
    }

    @Override
    public String toString() {
        return nome + " (" + progresso + "% concluído)";
    }
}