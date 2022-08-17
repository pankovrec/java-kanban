import manager.TaskManager;
import manager.Managers;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;

/**
 * Main класс программы.
 * В нем будем тестировать =)
 */
public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Заполняем данными программу.
        // создаем 2 задачи, эпик с тремя подзадачами, эпик без подзадач.
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.of(2022, 8, 8, 19, 00), 10);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW, LocalDateTime.of(2022, 8, 8, 19, 11), 10);
        Epic epic1 = new Epic(0, "Эпик 1", "Эпик 1", Status.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Эпик 2", Status.NEW);
        SubTask subTask1 = new SubTask(0, "подзадача 1", "Описание 1 подзадачи", Status.DONE, 3, LocalDateTime.of(2022, 8, 4, 21, 58), 10);
        SubTask subTask2 = new SubTask(0, "подзадача 2", "Описание 2 подзадачи", Status.DONE, 3, LocalDateTime.of(2022, 8, 4, 22, 30), 10);
        SubTask subTask3 = new SubTask(0, "подзадача 3", "Описание 3 подзадачи", Status.DONE, 3, LocalDateTime.of(2022, 8, 4, 20, 00), 10);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        System.out.println("Тестовый вывод.");
        System.out.println(taskManager.getSubTask(subTask1.getId()));
        System.out.println(taskManager.getSubTask(subTask2.getId()));
        System.out.println(taskManager.getSubTask(subTask3.getId()));
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println("history" + taskManager.getHistory());
        System.out.println(taskManager.getPrioritizedTasks());

    }
}