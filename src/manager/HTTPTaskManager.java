package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class HTTPTaskManager extends FileBackedTasksManager {

    KVClient kvTaskClient;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HTTPTaskManager() {
        super();
        this.kvTaskClient = new KVClient("http://localhost:8078/");
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