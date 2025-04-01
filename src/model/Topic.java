package model;

import java.time.LocalDate;

public class Topic {
    private String name;
    private int hoursEstimated;
    private LocalDate deadLine;
    private boolean concluded;

    public Topic(String name, int hoursEstimated, LocalDate deadLine)
    {
        this.name = name;
        this.hoursEstimated = hoursEstimated;
        this.deadLine = deadLine;
        this.concluded = false;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHoursEstimated() { return hoursEstimated; }
    public void setHoursEstimated(int hoursEstimated) { this.hoursEstimated = hoursEstimated; }
    public LocalDate getDeadline() { return deadLine; }
    public void setDeadline(LocalDate deadLine) { this.deadLine = deadLine; }
    public boolean isConcluded() { return concluded; }
    public void setConcluido(boolean concluded) { this.concluded = concluded; }

}