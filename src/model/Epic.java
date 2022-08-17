package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Класс эпиков.
 */
public class Epic extends Task {
    private ArrayList<Integer> subTasks;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status, LocalDateTime.now(), 0);
        this.subTasks = new ArrayList<>();
    }


    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<Integer> subTasks) {
        this.subTasks = subTasks;
    }

    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                ", subTasks=" + this.getSubTasks() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", finishTime=" + getFinishTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }
}