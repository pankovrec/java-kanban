package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends ManagerTest<InMemoryTaskManager> {
    @BeforeEach
    @Override
    void initFileBackedManager() {
        super.initFileBackedManager();
    }

    @Test
    void testInMemoryTaskManager() {
        manager = new InMemoryTaskManager();

        assertEquals(0, manager.getAllTasks().size(), "Задач нет");
        assertEquals(0, manager.getAllEpics().size(), "Эпиков нет");
        assertEquals(0, manager.getAllSubTasks().size(), "Подзадач нет");
        assertEquals(0, manager.getHistory().size(), "Истории нет");
    }


    @Test
    void testFileBackedTasksManager() {
        manager = new FileBackedTasksManager();
        assertEquals(0, manager.getAllTasks().size(), "Задач нет");
        assertEquals(0, manager.getAllEpics().size(), "Эпиков нет");
        assertEquals(0, manager.getAllSubTasks().size(), "Подзадач нет");
        assertEquals(0, manager.getHistory().size(), "Истории нет");
    }

}