package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends ManagerTest<InMemoryTaskManager> {
    @BeforeEach
    @Override
    void initFileBackedManager() {
        super.initFileBackedManager();
    }

    @Test
    void testInMemoryTaskManager() throws IOException, InterruptedException {
        manager = new InMemoryTaskManager();

        assertEquals(0, manager.getAllTasks().size(), "Задач нет");
        assertEquals(0, manager.getAllEpics().size(), "Эпиков нет");
        assertEquals(0, manager.getAllSubTasks().size(), "Подзадач нет");
        assertEquals(0, manager.getHistory().size(), "Истории нет");
    }


    @Test
    void testFileBackedTasksManager() throws IOException, InterruptedException {
        manager = new FileBackedTasksManager();
        assertEquals(0, manager.getAllTasks().size(), "Задач нет");
        assertEquals(0, manager.getAllEpics().size(), "Эпиков нет");
        assertEquals(0, manager.getAllSubTasks().size(), "Подзадач нет");
        assertEquals(0, manager.getHistory().size(), "Истории нет");
    }

}