package model;

import java.time.LocalDateTime;

/**
 * Класс подзадач.
 */
public class SubTask extends Task {
    private static int epicId;

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, Status status, int epicId, LocalDateTime startTime, int duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public static int getEpicId() {
        return epicId;
    }

    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }

    @Override
    public String toString() {
        return "model.SubTask{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status='" + this.getStatus() + '\'' +
                ", epicId=" + this.getEpicId() +
                ", startTime=" + this.getStartTime() +
                ", duration=" + this.getDuration() +
                ", finishTime=" + this.getFinishTime() +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

}