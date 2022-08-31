package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class ManagerTest<T extends TaskManager> {
    protected T manager;

    Task task;
    Epic epic;
    SubTask subTask;
    SubTask subTask1;
    SubTask subTask2;

    void initInMemoryManager() {
        manager = (T) new InMemoryTaskManager();
    }

    void initFileBackedManager() {
       // Managers.getDefault();
        Managers.getFileBackedTasksManager();
    }

    void initHttpTaskManager() throws IOException, InterruptedException {
        Managers.getDefault();
    }

    void init() throws IOException, InterruptedException {

        task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 8, 6, 11, 50), 15);
        epic = new Epic(0, "Эпик 1", "Описание эпика 1", Status.NEW);
        subTask = new SubTask(0, "Подзадача 1", "Описание подзадачи 1", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 6, 17, 40), 10);
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubTask(subTask);
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        final List<Task> taskList = manager.getAllTasks();
        assertNotNull(taskList);
        assertEquals(1, taskList.size(), "Одна задача");
        assertEquals(task, taskList.get(0));
    }

    @Test
    void getAllTasksIfTasksIsEmpty() throws IOException, InterruptedException {
        initInMemoryManager();
        final List<Task> taskList = manager.getAllTasks();
        assertNotNull(" ");
        assertEquals(0, taskList.size(), "нет задач");
    }


    @Test
    void getAllEpics() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        final List<Epic> epicList = manager.getAllEpics();
        assertNotNull(epicList);
        assertEquals(1, epicList.size(), "Один эпик");
        assertEquals(epic, epicList.get(0));
    }

    @Test
    void getAllEpicsIfEpicsIsEmpty() throws IOException, InterruptedException {
        initInMemoryManager();
        final List<Epic> epicList = manager.getAllEpics();
        assertNotNull(" ");
        assertEquals(0, epicList.size(), "нет эпиков");
    }

    @Test
    void getAllSubTasks() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        final List<SubTask> subTaskList = manager.getAllSubTasks();
        assertNotNull(subTaskList);
        assertEquals(1, subTaskList.size(), "Одна подзадача");
        assertEquals(subTask, subTaskList.get(0));
    }

    @Test
    void getAllSubTasksIfSubTasksIsEmpty() throws IOException, InterruptedException {
        initInMemoryManager();
        final List<SubTask> subTaskList = manager.getAllSubTasks();
        assertNotNull(" ");
        assertEquals(0, subTaskList.size(), "нет подзадач");
    }

    @Test
    void getSubTasksByEpic() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        final List<SubTask> subTaskByEpicList = manager.getSubTasksByEpic(epic.getId());
        assertNotNull(subTaskByEpicList);
        assertEquals(1, subTaskByEpicList.size(), "Одна подзадача");
        assertEquals(subTask, subTaskByEpicList.get(0));
    }

    @Test
    void getSubTasksByEpicIfEpicEmpty() throws IOException, InterruptedException {
        initInMemoryManager();
        assertEquals(0, manager.getSubTasksByEpic(1).size());
    }

    @Test
    void getTask() throws IOException, InterruptedException {
        initInMemoryManager();
        init();

        manager.getTask(task.getId());
        assertNotNull(task.getId());
        assertEquals(1, task.getId(), "Задача 1");
    }

    @Test
    void getTaskIfTasksIsEmpty() {
        initInMemoryManager();
        assertNull(task);
    }

    @Test
    void getUncreatedTask() {
        initInMemoryManager();
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws IOException, InterruptedException {
                        manager.getTask(100);
                    }
                });
        assertEquals("Такой задачи нет", ex.getMessage());
    }

    @Test
    void getSubTask() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        manager.getSubTask(subTask.getId());
        assertNotNull(subTask.getId());
        assertEquals(3, subTask.getId(), "Подзадача 1");
    }

    @Test
    void getSubTaskIfSubTasksIsEmpty() {
        initInMemoryManager();
        assertNull(subTask);
    }


    @Test
    void getEpic() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        manager.getEpic(epic.getId());

        assertNotNull(epic.getId());
        assertEquals(2, epic.getId(), "Эпик 1");
    }

    @Test
    void getEpicIfEpicsIsEmpty() {
        initInMemoryManager();
        assertNull(epic);
    }

    @Test
    void addTaskWithoutIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        initFileBackedManager();
        init();
        task = new Task(0, "addtasktest", "description", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 8, 3, 17, 48), 15);
        manager.addTask(task);
        assertNotNull(task);
        assertEquals("description", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals("2022-08-03T17:48", task.getStartTime().toString());
        assertEquals(15, task.getDuration());
    }

    @Test
    void findTaskIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        System.out.println(manager.getAllTasks());
        Task task1 = new Task(0, "t1", "descrt1", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 8, 6, 11, 52), 10);
//        manager.addTask(task1);
        System.out.println(manager.getAllTasks());
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws IOException, InterruptedException {
                        manager.addTask(task1);
                    }
                });

        assertEquals("Ошибка! задача t1 пересекается по времени с Задача 1", ex.getMessage());
    }

    @Test
    void findSubTaskAndTaskIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        subTask1 = new SubTask(0, "st1", "dsc1", Status.NEW, 2,
                LocalDateTime.of(2022, 8, 6, 17, 42), 10);
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws IOException, InterruptedException {
                        manager.addSubTask(subTask1);
                    }
                });
        assertEquals("Ошибка! задача st1 пересекается по времени с Подзадача 1", ex.getMessage());
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        initInMemoryManager();
        epic = new Epic(0, "addepictest", "descriptionepic", Status.IN_PROGRESS);
        manager.addEpic(epic);
        assertNotNull(epic);
        assertEquals("descriptionepic", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void addSubTaskWithoutIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        subTask = new SubTask(0, "addsubtasktest", "descriptionstask", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 3, 19, 42), 7);
        manager.addSubTask(subTask);
        assertNotNull(subTask);
        assertEquals("descriptionstask", subTask.getDescription());
        assertEquals(Status.DONE, subTask.getStatus());
        assertEquals("2022-08-03T19:42", subTask.getStartTime().toString());
        assertEquals(7, subTask.getDuration());
    }

    @Test
    void addSubTaskWithIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        System.out.println(manager.getAllSubTasks());
        subTask1 = new SubTask(0, "st1", "descrSt1", Status.NEW, 2,
                LocalDateTime.of(2022, 8, 6, 17, 44), 10);
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws IOException, InterruptedException {
                        manager.addTask(subTask1);
                    }
                });
        assertEquals("Ошибка! задача st1 пересекается по времени с Подзадача 1", ex.getMessage());
    }

    @Test
    void updateTaskWithoutIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        task = new Task(0, "updateTaskTest", "oldDescriptionUpdateTaskTest", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 8, 3, 20, 50), 5);
        manager.addTask(task);
        task = new Task(0, "updateTaskTestNEW", "DescriptionUpdateTaskTestNEW", Status.DONE,
                LocalDateTime.of(2022, 8, 3, 21, 51), 4);
        manager.updateTask(task);
        assertNotNull(task);
        assertEquals("updateTaskTestNEW", task.getName());
        assertEquals("DescriptionUpdateTaskTestNEW", task.getDescription());
        assertEquals(Status.DONE, task.getStatus());
        assertEquals(4, task.getDuration());
    }

    @Test
    void updateTaskWithIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        task = new Task(0, "updateTaskTest", "oldDescriptionUpdateTaskTest", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 8, 3, 21, 51), 5);
        manager.addTask(task);
        task = new Task(0, "updateTaskTestNEW", "DescriptionUpdateTaskTestNEW", Status.DONE,
                LocalDateTime.of(2022, 8, 3, 21, 51), 4);
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws IOException, InterruptedException {
                        manager.updateTask(task);
                    }
                });
        assertEquals("Ошибка! задача updateTaskTestNEW пересекается по времени с updateTaskTest",
                ex.getMessage());
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        epic = new Epic(2, "updateEpicTestNEW", "DescriptionUpdateEpicTestNEW", Status.IN_PROGRESS);
        manager.updateEpic(epic);
        assertNotNull(epic);
        assertEquals("updateEpicTestNEW", epic.getName());
        assertEquals("DescriptionUpdateEpicTestNEW", epic.getDescription());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void updateSubTaskWithoutIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        assertEquals(10, subTask.getDuration());
        subTask = new SubTask(3, "updateSubTaskTestNEW",
                "DescriptionUpdateSubTaskTestNEW", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 03, 21, 10), 16);
        manager.updateSubTask(subTask);
        assertNotNull(subTask);
        assertEquals("updateSubTaskTestNEW", subTask.getName());
        assertEquals("DescriptionUpdateSubTaskTestNEW", subTask.getDescription());
        assertEquals(Status.DONE, subTask.getStatus());
        assertEquals(16, subTask.getDuration());
    }

    @Test
    void updateSubTaskWithIntersection() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        System.out.println(manager.getAllSubTasks());
        SubTask subTask1 = new SubTask(0, "st1", "dst1", Status.NEW, epic.getId(),
                LocalDateTime.of(2022, 8, 6, 17, 39), 10);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws IOException, InterruptedException {
                        manager.updateSubTask(subTask1);
                    }
                });
        assertEquals("Ошибка! задача st1 пересекается по времени с Подзадача 1", ex.getMessage());
    }

    @Test
    void removeTask() throws IOException, InterruptedException {
        initInMemoryManager();
        initFileBackedManager();
        init();

        manager.removeTask(task.getId());

        assertNotNull(task.getId());
        assertEquals(1, task.getId(), "Задача 1");
    }

    @Test
    void removeTaskIfTaskNotExist() {
        initInMemoryManager();
        assertNull(task);
    }


    @Test
    void removeSubTask() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        manager.removeSubTask(subTask.getId());

        assertNotNull(subTask.getId());
        assertEquals(3, subTask.getId(), "Подзадача 1");
    }

    @Test
    void removeSubTaskIfSubTaskNotExist() {
        initInMemoryManager();
        assertNull(subTask);
    }

    @Test
    void removeEpic() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        manager.removeEpic(epic.getId());
        assertNotNull(epic.getId());
        assertEquals(2, epic.getId(), "Эпик 1");
    }

    @Test
    void removeEpicIfEpicNotExist() {
        initInMemoryManager();
        assertNull(epic);
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        manager.getEpic(epic.getId());
        manager.getSubTask(subTask.getId());
        manager.getTask(task.getId());
        List<Task> testHistory = manager.getHistory();
        for (int i = testHistory.size(); i >= 0; i--) {
            if (i == 0) {
                assertEquals("Эпик 1", testHistory.get(i).getName());
                assertEquals("Описание эпика 1", testHistory.get(i).getDescription());

            } else if (i == 1) {
                assertEquals("Подзадача 1", testHistory.get(i).getName());
                assertEquals("Описание подзадачи 1", testHistory.get(i).getDescription());
            } else if (i == 2) {
                assertEquals("Задача 1", testHistory.get(i).getName());
                assertEquals("Описание задачи 1", testHistory.get(i).getDescription());
            }
        }
    }

    @Test
    void checkDuplicatesInHistory() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        manager.getEpic(epic.getId());
        manager.getTask(task.getId());
        manager.getSubTask(subTask.getId());
        manager.getEpic(epic.getId());
        manager.getTask(task.getId());
        List<Task> testHistory = manager.getHistory();
        for (int i = testHistory.size(); i >= 0; i--) {
            if (i == 0) {
                assertEquals("Подзадача 1", testHistory.get(i).getName());
                assertEquals("Описание подзадачи 1", testHistory.get(i).getDescription());

            } else if (i == 1) {
                assertEquals("Эпик 1", testHistory.get(i).getName());
                assertEquals("Описание эпика 1", testHistory.get(i).getDescription());
            } else if (i == 2) {
                assertEquals("Задача 1", testHistory.get(i).getName());
                assertEquals("Описание задачи 1", testHistory.get(i).getDescription());
            }
        }
    }

    @Test
    void DeleteElementFromHistoryCheckCustomLinkedList() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        manager.getEpic(epic.getId());
        manager.getTask(task.getId());
        manager.getSubTask(subTask.getId());
        manager.removeTask(task.getId());
        List<Task> testHistory = manager.getHistory();
        for (int i = testHistory.size(); i >= 0; i--) {
            if (i == 0) {
                assertEquals("Эпик 1", testHistory.get(i).getName());
                assertEquals("Описание эпика 1", testHistory.get(i).getDescription());

            } else if (i == 1) {
                assertEquals("Подзадача 1", testHistory.get(i).getName());
                assertEquals("Описание подзадачи 1", testHistory.get(i).getDescription());
            }
        }
    }


    @Test
    void getHistoryIfHistoryNotExist() throws IOException, InterruptedException {
        initInMemoryManager();
        List<Task> testHistory = manager.getHistory();

        assertEquals(testHistory.size(), 0);
    }

    @Test
    void epicWithEmptySubtasks() throws IOException, InterruptedException {
        initInMemoryManager();
        epic = new Epic(0, "e1", "descE1", Status.DONE);
        manager.addEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());

    }

    @Test
    void epicWithNewSubtasks() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        epic = new Epic(0, "e1", "descE1", Status.IN_PROGRESS);
        manager.addEpic(epic);
        subTask = new SubTask(0, "stask1", "descrSt1", Status.NEW, 4,
                LocalDateTime.of(2022, 8, 6, 15, 02), 16);
        manager.addSubTask(subTask);
        assertEquals(Status.NEW, epic.getStatus());

    }

    @Test
    void epicWithInProgressNewSubtasks() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        epic = new Epic(0, "e1", "descE1", Status.NEW);
        manager.addEpic(epic);
        subTask = new SubTask(0, "stask1", "descrSt1", Status.IN_PROGRESS, 4,
                LocalDateTime.of(2022, 8, 06, 14, 50), 10);
        manager.addSubTask(subTask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void epicWithDoneSubtasks() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        epic = new Epic(0, "e1", "descE1", Status.NEW);
        manager.addEpic(epic);
        subTask = new SubTask(0, "stask1", "descrSt1", Status.DONE, 4,
                LocalDateTime.of(2022, 8, 6, 14, 55), 10);
        manager.addSubTask(subTask);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void epicWithNewAndDoneSubtasks() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        subTask = new SubTask(0, "stask1", "descrSt1", Status.NEW, 2,
                LocalDateTime.of(2022, 8, 6, 15, 00), 11);
        manager.addSubTask(subTask);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void calculateEpicStartTime() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        subTask1 = new SubTask(0, "stask1", "descrST1", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 6, 23, 34), 10);
        manager.addSubTask(subTask1);
        assertEquals(epic.getStartTime(), LocalDateTime.of(2022, 8, 6, 17, 40));
    }

    @Test
    void calculateEpicDuration() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        subTask1 = new SubTask(0, "stask1", "descrST1", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 6, 23, 34), 10);
        manager.addSubTask(subTask1);
        subTask2 = new SubTask(0, "stask1", "descrST1", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 6, 23, 45), 10);
        manager.addSubTask(subTask2);
        assertEquals(epic.getDuration(), 30);
    }


    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        initInMemoryManager();
        init();
        subTask1 = new SubTask(0, "stask1", "descrST1", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 6, 23, 34), 10);
        manager.addSubTask(subTask1);
        subTask2 = new SubTask(0, "stask1", "descrST1", Status.DONE, 2,
                LocalDateTime.of(2022, 8, 6, 23, 45), 10);
        manager.addSubTask(subTask2);
        assertEquals(epic.getDuration(), 30);
        task = new Task(0, "t1", "t1 desc", Status.NEW,
                LocalDateTime.of(2022, 8, 6, 23, 20), 15);
     //   System.out.println(manager.getPrioritizedTasks());
        Set<Task> prioTasks = manager.getPrioritizedTasks();
        Integer i = 0;
        for (Task task : prioTasks) {
            if (i == 1) {
                assertEquals(LocalDateTime.of(2022, 8, 06, 11, 50),
                        task.getStartTime());
                assertEquals("Задача 1", task.getName());
            } else if (i == 2) {
                assertEquals(LocalDateTime.of(2022, 8, 06, 17, 40),
                        task.getStartTime());
                assertEquals("Подзадача 1", task.getName());
            } else if (i == 3) {
                assertEquals(LocalDateTime.of(2022, 8, 06, 23, 34),
                        task.getStartTime());
                assertEquals("stask1", task.getName());
            } else if (i == 4) {
                assertEquals(LocalDateTime.of(2022, 8, 06, 23, 45),
                        task.getStartTime());
                assertEquals("stask1", task.getName());
            }
        }
    }
}