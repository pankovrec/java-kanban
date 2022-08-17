package manager;

import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;

import java.io.File;

class FileBackedTasksManagerTest extends ManagerTest<FileBackedTasksManager> {
    @BeforeEach
    void initFileBackedTasksManager() {
        manager = new FileBackedTasksManager();
    }

    @Test
    void saveToFileAndLoadFromFile() {
        super.init();
        File tasksDB = new File("task.csv");
        Task taskT = manager.getTask(1);
        manager.save();
        manager.loadFromFile(tasksDB);
        assertEquals(taskT, manager.getTask(1), "задачи разные");

    }
}