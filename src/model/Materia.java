package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Materia {
    private int id;
    private String name;
    private List<Topic> topics;
    private LocalDate startDate;
    private LocalDate endDate;

    public Materia(String name, LocalDate startDate, LocalDate endDate)
    {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.topics = new ArrayList<>();
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public List<Topic> getTopics() {return topics;}
    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}
    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public double getProgress()
    {
        if (topics.isEmpty()) return 0;
        long concluded = topics.stream().filter(Topic::isConcluded).count();
        return (double) concluded / topics.size() * 100;
    }

    public String getResume()
    {
        StringBuilder resume = new StringBuilder();
        resume.append("Materia: ").append(name).append("\n");
        resume.append("Progresso: ").append(String.format("%1f%%", getProgress())).append("\n\n");
        resume.append("Tópicos:\n");
        topics.forEach(t -> resume.append("- ").append(t.getName()).append(t.isConcluded() ? " (Concluído)\n" : "\n"));
        return resume.toString();
    }

    public void addTopic(Topic topic)
    {
        topics.add(topic);
    }
}