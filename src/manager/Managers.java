package manager;

public class Managers {

     public static TaskManager getDefault() {
         return new HTTPTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedTasksManager() {
         return new FileBackedTasksManager();
    }
}