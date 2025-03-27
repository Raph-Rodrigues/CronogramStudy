package model;

import java.time.LocalDate;

public class Topic {
    private String nome;
    private int horasEstimadas;
    private LocalDate deadline;
    private boolean concluido;

    public Topic(String nome, int horasEstimadas, LocalDate deadline) {
        this.nome = nome;
        this.horasEstimadas = horasEstimadas;
        this.deadline = deadline;
        this.concluido = false;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(int horasEstimadas) { this.horasEstimadas = horasEstimadas; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

    @Override
    public String toString() {
        return nome + " - " + horasEstimadas + "h (" + (concluido ? "Concluído" : "Pendente") + ")";
    }
}