package manager;

import model.Task;

import java.util.List;

/**
 * Интерфейс истории.
 */
public interface HistoryManager {

    /**
     * Получить историю
     */
    List<Task> getHistory();

    /**
     * Добавить запись
     * в историю
     */
    void add(Task task);

    /**
     * Удалить
     * запись из истории
     */
    void remove(int id);
}