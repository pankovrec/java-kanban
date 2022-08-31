package manager;

import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

class FileBackedTasksManagerTest extends ManagerTest<FileBackedTasksManager> {
    @BeforeEach
    void initFileBackedTasksManager() {
        manager = new FileBackedTasksManager();
    }

    @Test
    void saveToFileAndLoadFromFile() throws IOException, InterruptedException {
        super.init();
        File tasksDB = new File("task.csv");
        Task taskT = manager.getTask(1);
        manager.save();
        manager.loadFromFile(tasksDB);
        assertEquals(taskT, manager.getTask(1), "задачи разные");

    }
}