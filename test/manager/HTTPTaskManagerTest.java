package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest {
    static KVServer kvServer;
    FileBackedTasksManager httpTaskManager;
    HttpTaskServer httpTaskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeAll
    public static void startKvServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterAll
    public static void stopServers() {
        kvServer.stop();
    }

    @BeforeEach
    public void startServers() {
        this.httpTaskManager = new HTTPTaskManager();
        this.httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.start();
    }

    @AfterEach
    public void stopHttpTaskServer() {
        this.httpTaskServer.stop();
    }

    @Test
    public void testTaskGetMethod() throws IOException, InterruptedException {
        Task task = new Task(
                0, "Task",
                "desc Task",
                Status.DONE,
                LocalDateTime.of(2022, 8, 30, 23, 00), 10);
        String jsonTask = gson.toJson(task);
        HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .header("X-Add-Update-Task", "add")
                .POST(bodyTask)
                .build();
        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .header("X-Add-Update-Task", "add")
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Task taskFromJson = gson.fromJson(responseGet.body(), Task.class);

        assertAll(
                () -> Assertions.assertEquals(task.getDescription(), taskFromJson.getDescription()),
                () -> Assertions.assertEquals(200, responseGet.statusCode())
        );
    }

    @Test
    public void testTaskPostMethod() throws IOException, InterruptedException {
        Task task = new Task(
                0, "Task",
                "desc Task",
                Status.DONE,
                LocalDateTime.of(2022, 8, 30, 23, 00), 10);
        String jsonTask = gson.toJson(task);
        HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .header("X-Add-Update-Task", "add")
                .POST(bodyTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertAll(
                () -> Assertions.assertEquals(task.getDescription(), httpTaskManager.getTask(1).getDescription()),
                () -> Assertions.assertEquals(201, response.statusCode())
        );
    }

    @Test
    public void testTaskDeleteMethod() throws IOException, InterruptedException {
        Task task = new Task(
                0, "Task",
                "desc Task",
                Status.DONE,
                LocalDateTime.of(2022, 8, 30, 23, 00), 10);
        String jsonTask = gson.toJson(task);
        HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseDelete.statusCode());
        assertAll(
                () -> Assertions.assertTrue(httpTaskManager.getAllTasks().isEmpty()),
                () -> Assertions.assertEquals(204, responseDelete.statusCode())
        );
    }

    @Test
    public void testEpicGetMethod() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0, "Epic",
                "desc Epic", Status.NEW);
        String jsonEpic = gson.toJson(epic);
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .header("X-Add-Update-Epic", "add")
                .POST(bodyEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        Epic epicFromJson = gson.fromJson(responseGet.body(), Epic.class);
        assertAll(
                () -> Assertions.assertEquals(epic.getDescription(), epicFromJson.getDescription()),
                () -> Assertions.assertEquals(200, responseGet.statusCode())
        );
    }

    @Test
    public void testEpicPostMethod() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0, "Epic",
                "desc Epic", Status.NEW);
        String jsonEpic = gson.toJson(epic);
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .header("X-Add-Update-Epic", "add")
                .POST(bodyEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        assertAll(
                () -> Assertions.assertEquals(epic.getDescription(), httpTaskManager.getEpic(1).getDescription()),
                () -> Assertions.assertEquals(201, response.statusCode())
        );

    }

    @Test
    public void testEpicDeleteMethod() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0, "Epic",
                "desc Epic", Status.NEW);
        String jsonEpic = gson.toJson(epic);
        HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertAll(
                () -> Assertions.assertEquals(204, responseDelete.statusCode())
        );
    }

    @Test
    public void testSubtaskGetMethod() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0, "Epic",
                "desc Epic", Status.IN_PROGRESS);
        httpTaskManager.addEpic(epic);
        SubTask subtask = new SubTask(
                0, "Subtask",
                "Desc Subtask",
                Status.DONE,
                epic.getId(),
                LocalDateTime.of(2022, 8, 30, 23, 30), 10);

        String jsonSubtask = gson.toJson(subtask);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .header("X-Add-Update-Subtask", "add")
                .POST(bodySubtask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        SubTask subtaskFromJson = gson.fromJson(responseGet.body(), SubTask.class);

        assertAll(
                () -> Assertions.assertEquals(subtask.getName(), subtaskFromJson.getName()),
                () -> Assertions.assertEquals(200, responseGet.statusCode())
        );
    }

    @Test
    public void testSubtaskPostMethod() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0, "Epic",
                "desc Epic", Status.IN_PROGRESS);
        httpTaskManager.addEpic(epic);
        SubTask subtask = new SubTask(
                0, "Subtask",
                "Desc Subtask",
                Status.DONE,
                epic.getId(),
                LocalDateTime.of(2022, 8, 30, 23, 30), 10);

        String jsonSubtask = gson.toJson(subtask);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .header("X-Add-Update-Subtask", "add")
                .POST(bodySubtask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        assertAll(
                () -> Assertions.assertEquals(subtask.getName(), httpTaskManager.getSubTask(2).getName()),
                () -> Assertions.assertEquals(201, response.statusCode())
        );
    }

    @Test
    public void testSubtaskDeleteMethod() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0, "Epic",
                "desc Epic", Status.IN_PROGRESS);
        httpTaskManager.addEpic(epic);
        SubTask subtask = new SubTask(
                0, "Subtask",
                "Desc Subtask",
                Status.DONE,
                epic.getId(),
                LocalDateTime.of(2022, 8, 30, 23, 30), 10);

        String jsonSubtask = gson.toJson(subtask);
        HttpRequest.BodyPublisher bodySubtask = HttpRequest.BodyPublishers.ofString(jsonSubtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .header("X-Add-Update-Subtask", "add")
                .POST(bodySubtask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(requestPost, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());

        assertAll(
                () -> Assertions.assertTrue(httpTaskManager.getAllSubTasks().isEmpty()),
                () -> Assertions.assertEquals(204, responseDelete.statusCode())
        );
    }

    @Test
    void testGetHistoryMethod() {
        Epic epic = new Epic(
                0, "Epic",
                "desc Epic", Status.IN_PROGRESS);
        httpTaskManager.addEpic(epic);
        SubTask subtask = new SubTask(
                0, "Subtask",
                "Desc Subtask",
                Status.DONE,
                epic.getId(),
                LocalDateTime.of(2022, 8, 30, 23, 30), 10);
        httpTaskManager.addSubTask(subtask);
        Task task = new Task(
                0, "task",
                "Desc task",
                Status.NEW,
                LocalDateTime.of(2022, 8, 31, 22, 00), 10);
        httpTaskManager.addTask(task);
        httpTaskManager.getSubTask(subtask.getId());
        httpTaskManager.getTask(task.getId());
        httpTaskManager.getEpic(epic.getId());

        List<Task> testHistory = httpTaskManager.getHistory();
        for (int i = testHistory.size(); i >= 0; i--) {
            if (i == 0) {
                assertEquals("Subtask", testHistory.get(i).getName());
                assertEquals("Desc Subtask", testHistory.get(i).getDescription());

            } else if (i == 1) {
                assertEquals("task", testHistory.get(i).getName());
                assertEquals("Desc task", testHistory.get(i).getDescription());
            } else if (i == 2) {
                assertEquals("Epic", testHistory.get(i).getName());
                assertEquals("desc Epic", testHistory.get(i).getDescription());
            }
        }
    }

}
