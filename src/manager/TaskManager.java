package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Интерфейс менеджера задач.
 */
public interface TaskManager<T extends Task> {

    /**
     * Список всех задач
     *
     * @return
     */
    List<T> getAllTasks();

    /**
     * Список всех эпиков
     *
     * @return
     */
    List<T> getAllEpics();

    /**
     * Список всех подзадач
     *
     * @return
     */
    List<T> getAllSubTasks();

    /**
     * Список всех подзадач эпика
     *
     * @return
     */
    List<T> getSubTasksByEpic(int epicId);

    /**
     * Получение задачи по идентификатору
     *
     * @return
     */
    Task getTask(int id);

    /**
     * Получение подзадачи по идентификатору
     *
     * @return
     */
    SubTask getSubTask(int id);

    /**
     * Получение эпика по идентификатору
     *
     * @return
     */
    Epic getEpic(int id);

    /**
     * Создание. Задачи Сам объект должен передаваться в качестве параметра.
     */
    void addTask(Task task);

    /**
     * Создание. Подзадачи Сам объект должен передаваться в качестве параметра.
     */
    void addSubTask(SubTask subTask);

    /**
     * Создание. Эпика Сам объект должен передаваться в качестве параметра.
     */
    void addEpic(Epic epic);

    /**
     * Обновление Задачи.
     * Новая версия объекта с верным идентификатором передаются в виде параметра.
     */
    void updateTask(Task task);

    /**
     * Обновление Подзадачи.
     * Новая версия объекта с верным идентификатором передаются в виде параметра.
     */
    void updateSubTask(SubTask subTask);

    /**
     * Обновление Эпика.
     * Новая версия объекта с верным идентификатором передаются в виде параметра.
     */
    void updateEpic(Epic epic);

    /**
     * Удаление по идентификатору задачи
     */
    void removeTask(int id);

    /**
     * Удаление по идентификатору подзадачи
     */
    void removeSubTask(int id) throws ManagerSaveException;

    /**
     * Удаление по идентификатору эпика
     */
    void removeEpic(int id);

    /**
     * Получить историю
     */
    List<T> getHistory();

    /**
     * Сортировка по приоритету
     */
    Set<Task> getPrioritizedTasks();

}
