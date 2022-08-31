package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import model.Epic;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.*;

import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;

import java.util.List;

/**
 * HTTP
 * Менеджер задач.
 */

public class HttpTaskServer {
    private static final int PORT = 8080;
    FileBackedTasksManager taskManager;
    HttpServer httpServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServer(FileBackedTasksManager taskManager) {
        this.taskManager = taskManager;
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            httpServer.createContext("/tasks/", new AllTasksHandler());
            httpServer.createContext("/tasks/task", new TasksHandler());
            httpServer.createContext("/tasks/epic", new EpicsHandler());
            httpServer.createContext("/tasks/subtask", new SubtasksHandler());
            httpServer.createContext("/tasks/history", new HistoryHandler());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка!");
        }
    }

    public void setTasksManager(FileBackedTasksManager tasksManager) {
        this.taskManager = tasksManager;
    }

    public class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Обрабатываю /tasks/history запрос.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Клиент передает:\n" + body);

            switch (method) {
                case "GET":
                    response = getHistory(query);
                    responseCode = 200;
                    break;
                default:
                    response = "Нет обработчика для данного метода!";
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class AllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Обрабатываю /tasks/ запрос.");
            int responseCode = 0;
            String response = "";
            String method = httpExchange.getRequestMethod();

            if (method.equals("GET")) {
                response = gson.toJson(taskManager.getAllTasks()) + "\n"
                        + gson.toJson(taskManager.getAllEpics()) + "\n"
                        + gson.toJson(taskManager.getAllSubTasks());
                responseCode = 200;
            } else {
                System.out.println("Нет возможности обработать такой метод для /tasks");
                responseCode = 501;
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Обрабатываю /tasks/task запрос.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            List<String> header = httpExchange.getRequestHeaders().get("X-Add-Update-Task");
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Клиент передает:\n" + body);

            switch (method) {
                case "GET":
                    response = getTask(query);
                    responseCode = 200;
                    break;
                case "POST":
                    Task task = gson.fromJson(body, Task.class);
                    response = addOrUpdateTask(task, header);
                    responseCode = 201;
                    break;
                case "DELETE":
                    responseCode = deleteTask(query);
                    break;
                default:
                    response = "Ошибка! Нет обработчика для данного метода.";
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class EpicsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            System.out.println("Обрабатываю /tasks/epic запрос.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            List<String> header = httpExchange.getRequestHeaders().get("X-Add-Update-Epic");

            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Клиент передает:\n" + body);

            switch (method) {
                case "GET":
                    response = getEpic(query);
                    responseCode = 200;
                    break;
                case "POST":
                    Epic epic = gson.fromJson(body, Epic.class);
                    response = addOrUpdateEpic(epic, header);
                    responseCode = 201;
                    break;
                case "DELETE":
                    responseCode = deleteEpic(query);
                    break;
                default:
                    response = "Ошибка. Нет обработчика для данного метода!";
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public class SubtasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            System.out.println("Обрабатываю /tasks/subtask запрос.");
            int responseCode = 0;
            String response = "";
            String[] path = httpExchange.getRequestURI().getPath().split("/");
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();

            List<String> header = httpExchange.getRequestHeaders().get("X-Add-Update-Subtask");

            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Клиент посылает:\n" + body);

            System.out.println("SUBTASK: ");
            switch (method) {
                case "GET":
                    response = getSubtask(query);
                    responseCode = 200;
                    break;
                case "POST":
                    SubTask subtask = gson.fromJson(body, SubTask.class);
                    response = addOrUpdateSubtask(subtask, header);
                    responseCode = 201;
                    break;
                case "DELETE":
                    responseCode = deleteSubtask(query);
                    break;
                default:
                    response = "Ошибка. Нет обработчика для данного метода!";
            }
            httpExchange.sendResponseHeaders(responseCode, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public String addOrUpdateTask(Task task, List<String> header) {
        if (header.isEmpty()) {
            throw new IllegalArgumentException("Ошибка. Пустой header");
        }
        if (header.contains("add")) {
            return gson.toJson(taskManager.addTask(task));
        } else if (header.contains("update")) {
            taskManager.updateTask(task);
            return gson.toJson(task.getId());
        } else {
            throw new IllegalArgumentException("Header" + " [" + header.get(0) + "] " + "некорректный");
        }
    }

    public String addOrUpdateEpic(Epic epic, List<String> header) {
        if (header.isEmpty()) {
            throw new IllegalArgumentException("Ошибка. Пустой header");
        }
        if (header.contains("add")) {
            return gson.toJson(taskManager.addEpic(epic));
        } else if (header.contains("update")) {
            taskManager.updateEpic(epic);
            return gson.toJson(epic.getId());
        } else {
            throw new IllegalArgumentException("Header" + " [" + header.get(0) + "] " + "некорректный");
        }
    }

    public String addOrUpdateSubtask(SubTask subtask, List<String> header) {

        if (header.isEmpty()) {
            throw new IllegalArgumentException("Ошибка. Пустой header");
        }
        if (header.contains("add")) {
            return gson.toJson(taskManager.addSubTask(subtask));
        } else if (header.contains("update")) {
            taskManager.updateSubTask(subtask);
            return gson.toJson(subtask.getId());
        } else {
            throw new IllegalArgumentException("Header" + " [" + header.get(0) + "] " + "некорректный");
        }
    }

    public String getTask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            return gson.toJson(taskManager.getTask(id));

        }
        return gson.toJson(taskManager.getAllTasks());
    }

    public String getEpic(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            return gson.toJson(taskManager.getEpic(id));
        }
        return gson.toJson(taskManager.getAllEpics());
    }

    public String getSubtask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            return gson.toJson(taskManager.getSubTask(id));

        }
        return gson.toJson(taskManager.getAllSubTasks());
    }

    public String getHistory(String query) {
        return gson.toJson(taskManager.getHistory());
    }

    public int deleteTask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            taskManager.removeTask(id);
            return 204;
        }
        return 404;
    }

    public int deleteEpic(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            taskManager.removeEpic(id);
            return 204;
        }

        return 404;
    }

    public int deleteSubtask(String query) {
        if (query != null) {
            Integer id = extractIdFromQuery(query);
            taskManager.removeSubTask(id);
            return 204;

        }
        return 404;
    }


    private Integer extractIdFromQuery(String query) {
        String[] split = query.split("=");
        return Integer.parseInt(split[1]);
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }

}
