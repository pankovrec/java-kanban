package manager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVClient kvTaskClient;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HTTPTaskManager() {
        super();
        this.kvTaskClient = new KVClient("http://localhost:8078/");
    }

    public void load() {

        String jsonTasks = kvTaskClient.load("task");
        ArrayList<Task> tasks = gson.fromJson(jsonTasks, new TypeToken<ArrayList<Task>>() {
        }.getType());
        if (!jsonTasks.isEmpty()) {
            for (Task task : tasks) {
                addTask(task);
            }
        }
        String jsonEpics = kvTaskClient.load("epic");
        ArrayList<Epic> epics = gson.fromJson(jsonEpics, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        if (!jsonEpics.isEmpty()) {
            for (Epic epic : epics) {
                addEpic(epic);
            }
        }
        String jsonSubTasks = kvTaskClient.load("subtask");
        ArrayList<SubTask> subTasks = gson.fromJson(jsonSubTasks, new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        if (!jsonSubTasks.isEmpty()) {
            for (SubTask subTask : subTasks) {
                addSubTask(subTask);
            }
        }
        String jsonHistory = kvTaskClient.load("history");
        if (!jsonHistory.isEmpty()) {
            ArrayList<String> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<String>>() {
            }.getType());
            for (int i = history.size() - 1; i >= 0; i--) {
                getTask(Integer.parseInt(history.get(i)));
            }
        }
    }

    @Override
    void save() {
        String jsonTasks = gson.toJson(tasks.entrySet());
        kvTaskClient.put("task", jsonTasks);
        String jsonEpics = gson.toJson(epics.entrySet());
        kvTaskClient.put("epic", jsonEpics);
        String jsonSubtasks = gson.toJson(subTasks.entrySet());
        kvTaskClient.put("subtask", jsonSubtasks);
        String jsonHistory = gson.toJson(inMemoryHistoryManager.getHistory());
        kvTaskClient.put("history", jsonHistory);
    }
}