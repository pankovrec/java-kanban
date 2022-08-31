package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager() {
        this(new File("task.csv"), false);
    }

    public FileBackedTasksManager(File file) {
        this(file, true);
    }

    public FileBackedTasksManager(File file, boolean load) {
        this.file = file;
        if (load) {
            load();
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager manager = new FileBackedTasksManager(file, true);
        manager.load();
        return manager;
    }

    // метод для упаковки задач в строки
    private String toString(Task task) {
        TaskTypes typeTask = task.getType();
        switch (typeTask) {
            case TASK:
            case EPIC:
                return task.getId() +
                        "," + typeTask +
                        "," + task.getName() +
                        "," + task.getStatus() +
                        "," + task.getDescription() +
                        "," + task.getStartTime() +
                        "," + task.getDuration();

            case SUBTASK:
                return task.getId() +
                        "," + typeTask +
                        "," + task.getName() +
                        "," + task.getStatus() +
                        "," + task.getDescription() +
                        "," + SubTask.getEpicId() +
                        "," + task.getStartTime() +
                        "," + task.getDuration();
            default:
                return null;
        }
    }

    // Метод для восстановления задач из строк
    private Task fromString(String value) {
        final String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        TaskTypes taskType = TaskTypes.valueOf(values[1]);
        String name = values[2];

        switch (taskType) {
            case TASK:
                return new Task(id, name, values[4], Status.valueOf(values[3]));
            case EPIC:
                return new Epic(id, name, values[4], Status.valueOf(values[3]));
            case SUBTASK:
                int epicId = Integer.parseInt(values[5]);
                return new SubTask(id, name, values[4], Status.valueOf(values[3]), epicId);
            default:
                return null;
        }
    }

    // Метод для упаковки истории в строки
    static String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        return sb.toString();
    }

    // Метод для восстановления истории из строк
    static List<Integer> historyFromString(String value) {
        final String[] id = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String v : id) {
            history.add(Integer.valueOf(v));
        }
        return history;
    }

    // Метод для сохранения в файл
    void save() {
        try (final BufferedWriter writer = new BufferedWriter((new FileWriter(file, UTF_8)))) {
            writer.write("id,type,name,status,description,epicId,startTime,duration");
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            writer.newLine();
            writer.append(toString(inMemoryHistoryManager));
        } catch (ManagerSaveException | IOException e) {
            throw new ManagerSaveException((ManagerSaveException) e);
        }
    }

    // Метод для загрузки из файла
    private void load() {
        int maxId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                final Task task = fromString(line);
                final int id = task.getId();
                if (task.getType() == TaskTypes.TASK) {
                    tasks.put(id, task);
                } else if (task.getType() == TaskTypes.EPIC) {
                    epics.put(id, (Epic) task);
                } else if (task.getType() == TaskTypes.SUBTASK) {
                    subTasks.put(id, (SubTask) task);
                    Epic epic = epics.get(((SubTask) task).getEpicId());
                    epic.getSubTasks().add(id);
                }
                if (maxId < id) {
                    maxId = id;
                }
            }
            String line = reader.readLine();
            for (int id : historyFromString(line)) {
                if (tasks.containsKey(id)) {
                    Task task = tasks.get(id);
                    inMemoryHistoryManager.add(task);
                } else if (epics.containsKey(id)) {
                    Epic epic = epics.get(id);
                    inMemoryHistoryManager.add(epic);
                } else if (subTasks.containsKey(id)) {
                    SubTask subTask = subTasks.get(id);
                    inMemoryHistoryManager.add(subTask);
                } else {
                    System.out.println("Нечего выводить");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error load from file: " + file.getName(), e);
        }
        id = maxId;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
                save();
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        save();
        return super.getAllEpics();
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks(){
        save();
        return super.getAllSubTasks();
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        save();
        return super.getSubTasksByEpic(epicId);
    }

    @Override
    public Task getTask(int id) {
        save();
        return super.getTask(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        save();
        return super.getSubTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        save();
        return super.getEpic(id);
    }

    @Override
    public Object addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public Object addSubTask(SubTask subTask)  {
        super.addSubTask(subTask);
        save();
        return subTask.getId();
    }

    @Override
    public Object addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(int id)  {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id)  {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id)  {
        super.removeEpic(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();
    }

    //  Для тестов
    public static void main(String[] args) throws IOException, InterruptedException {
        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("task.csv"));

        fileBackedTasksManager.getHistory().forEach(System.out::println); //пробуем востановить задачи из истории
    }

}
