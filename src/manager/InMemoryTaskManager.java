package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * В памяти
 * Менеджер задач.
 */

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    protected int id = 0;
    private final Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {

        if ((task1.getStartTime() != null) && (task2.getStartTime() != null)) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() == null) {
            return 1;
        } else if (null == task2.getStartTime()) {
            return -1;
        } else {
            return 0;
        }

    });


    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        HashMap<Integer, SubTask> subTasksByEpic = new HashMap<>();

        try {
            for (int idSubTask : epics.get(epicId).getSubTasks()) {
                subTasksByEpic.put(idSubTask, subTasks.get(idSubTask));
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно получить подзадачи. Эпика не существует, либо подзадач нет.");
        }

        return new ArrayList<>(subTasksByEpic.values());
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {


            final Task task = tasks.get(id);
            inMemoryHistoryManager.add(task);
            return task;

        } else throw new IllegalArgumentException("Такой задачи нет");
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subTasks.containsKey(id)) {
            final SubTask subTask = subTasks.get(id);
            inMemoryHistoryManager.add(subTask);
            return subTask;
        } else throw new IllegalArgumentException("Такой подзадачи нет");
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            final Epic epic = epics.get(id);
            inMemoryHistoryManager.add(epic);
            return epic;
        } else throw new IllegalArgumentException("Такого эпика нет");
    }

    @Override
    public void addTask(Task task) {
        task.setId(++id);
        findCrossTimeIntersection(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.addAll(tasks.values());
    }

    @Override
    public void addSubTask(SubTask subTask) {
        try {
            subTask.setId(++id);
            findCrossTimeIntersection(subTask);
            subTasks.put(subTask.getId(), subTask);
            addSubTaskToEpic(epics.get(subTask.getEpicId()), subTask.getId());
            epicStatusChanger(epics.get(subTask.getEpicId()));
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно добавить подзадачу.");
        }
        prioritizedTasks.addAll(subTasks.values());

    }


    private void addSubTaskToEpic(Epic epic, int idSubTask) {
        try {
            if (epic != null) {
                ArrayList<Integer> idSubTasks = epic.getSubTasks();
                idSubTasks.add(idSubTask);

                epic.setSubTasks(idSubTasks);
                epic.setStartTime(calculateEpicStartTime(epic));
                epic.setDuration(calculateEpicDuration(epic));
                epic.setFinishTime(calculateEpicFinishTime(epic));
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно добавить  подзадачу. Эпика не существует");
        }
        prioritizedTasks.addAll(subTasks.values());
    }


    @Override
    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(epic.getId(), epic);
        epicStatusChanger(epic);
    }

    @Override
    public void updateTask(Task task) {
        try {
            findCrossTimeIntersection(task);
            tasks.put(task.getId(), task);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно обновить задачу, такой задачи нет");
        }
        prioritizedTasks.addAll(tasks.values());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        try {
            findCrossTimeIntersection(subTask);
            subTasks.put(subTask.getId(), subTask);
            epicStatusChanger(epics.get(subTask.getEpicId()));
            Epic epic = epics.get(SubTask.getEpicId());
            epic.setStartTime(calculateEpicStartTime(epic));
            epic.setDuration(calculateEpicDuration(epic));
            epic.setFinishTime(calculateEpicFinishTime(epic));
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно обновить подзадачу, такой подзадачи нет");
        }
        prioritizedTasks.addAll(subTasks.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        try {
            epic.setSubTasks(epics.get(epic.getId()).getSubTasks());
            epic.setStatus(epics.get(epic.getId()).getStatus());
            epics.put(epic.getId(), epic);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно обновить эпик, такого эпика нет");
        }

    }

    @Override
    public void removeTask(int id) {
        try {
            inMemoryHistoryManager.remove(id);
            tasks.remove(id);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно удалить задачу. Такой задачи нет");
        }
        prioritizedTasks.addAll(tasks.values());
    }

    @Override
    public void removeSubTask(int id) {
        try {
            int checkEpicId = getSubTask(id).getEpicId();
            int sizeOfSubTasks = (getSubTasksByEpic(checkEpicId).size());
            if ((sizeOfSubTasks - 1) <= 0) {
                inMemoryHistoryManager.remove(id);
                subTasks.remove(id);
                Epic epic = getEpic(checkEpicId);
                epicStatusChanger(epic);
                epic.setStartTime(calculateEpicStartTime(epic));
                epic.setDuration(calculateEpicDuration(epic));
                epic.setFinishTime(calculateEpicFinishTime(epic));
                ArrayList<Integer> subTasks;
                subTasks = new ArrayList<>();
                getEpic(checkEpicId).setSubTasks(subTasks);
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно удалить подзадачу. Такой подзадачи нет.");
        }
        prioritizedTasks.addAll(subTasks.values());
    }


    @Override
    public void removeEpic(int id) {
        try {
            if (epics.containsKey(id)) {
                if (epics.get(id).getSubTasks().size() > 0) {
                    for (int subTaskId : epics.get(id).getSubTasks()) {
                        inMemoryHistoryManager.remove(subTaskId);
                        subTasks.remove(subTaskId);
                    }
                    inMemoryHistoryManager.remove(id);
                    epics.remove(id);
                }
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Невозможно удалить эпик. Такого эпика нет.");
        }
        prioritizedTasks.addAll(subTasks.values());
    }


    @Override
    public List<Task> getHistory() {

        return inMemoryHistoryManager.getHistory();

    }

    /**
     * Системный. Для управления статусами.
     */
    private void epicStatusChanger(Epic epic) {
        int counterStatusNew = 0;
        int counterStatusDone = 0;
        if (epic.getSubTasks().size() == 0) {
            epic.setStatus(Status.NEW);
        } else {
            for (int subTaskId : epic.getSubTasks()) {
                if ((subTasks.get(subTaskId).getStatus().equals(Status.NEW))) {
                    counterStatusNew++;
                } else if (subTasks.get(subTaskId).getStatus().equals(Status.DONE)) {
                    counterStatusDone++;
                }
                if (epic.getSubTasks().size() == counterStatusNew) {
                    epic.setStatus(Status.NEW);
                } else if (epic.getSubTasks().size() == counterStatusDone) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }

    /**
     * Системный. Для расчета времени начала эпика.
     */
    private LocalDateTime calculateEpicStartTime(Epic epic) {
        Comparator<SubTask> comparator = new Comparator<SubTask>() {
            @Override
            public int compare(SubTask o1, SubTask o2) {
                return (int) (o1.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli()
                        - o2.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli());
            }
        };
        Set<SubTask> subTasks = new TreeSet<>(comparator);
        if (epic.getSubTasks().isEmpty()) {
            return LocalDateTime.now();
        } else {
            subTasks.addAll(getSubTasksByEpic(epic.getId()));
            return ((TreeSet<SubTask>) subTasks).first().getStartTime();
        }
    }

    /**
     * Системный. Для расчета времени окончания эпика.
     */
    private LocalDateTime calculateEpicFinishTime(Epic epic) {
        Comparator<SubTask> comparator = new Comparator<SubTask>() {
            @Override
            public int compare(SubTask o1, SubTask o2) {
                return (int) (o1.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli()
                        - o2.getStartTime().toInstant(ZoneOffset.UTC).toEpochMilli());
            }
        };
        Set<SubTask> subTasks = new TreeSet<>(comparator);
        if (epic.getSubTasks().isEmpty()) {
            return LocalDateTime.now();
        } else {
            subTasks.addAll(getSubTasksByEpic(epic.getId()));
            return ((TreeSet<SubTask>) subTasks).last().getFinishTime();
        }
    }

    /**
     * Системный. Для расчета продолжительности эпика.
     */
    private Integer calculateEpicDuration(Epic epic) {
        for (Integer subTask : epic.getSubTasks()) {
            if (subTasks.get(subTask).getStartTime() == null) {
                return null;
            }
        }
        int subTaskDurationSum = 0;
        if (epic.getSubTasks().isEmpty()) {
            return subTaskDurationSum;
        }
        for (Integer subTask : epic.getSubTasks()) {
            subTaskDurationSum = subTasks.get(subTask).getDuration() + subTaskDurationSum;
        }
        return subTaskDurationSum;
    }

    /**
     * Системный. Для сортировки задач по приоритету.
     */
    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    /**
     * Системный. Для нахождения пересечений.
     */
    private void findCrossTimeIntersection(Task newTask) {
        LocalDateTime endTimeOfNewTask = newTask.getStartTime().plusMinutes(newTask.getDuration());
        for (Task task : prioritizedTasks) {
            if (!task.getClass().getName().equals(epics)) {
                LocalDateTime endTimeOfTask = task.getStartTime().plusMinutes(task.getDuration());
                if (newTask.getStartTime().isAfter(task.getStartTime())
                        && newTask.getStartTime().isBefore(endTimeOfTask)) {
                    throw new IllegalArgumentException("Ошибка! задача " + newTask.getName() +
                            " пересекается по времени" + " с " + task.getName());
                }
                if (endTimeOfNewTask.isAfter(task.getStartTime())
                        && endTimeOfNewTask.isBefore(endTimeOfTask)) {
                    throw new IllegalArgumentException("Ошибка! задача " + newTask.getName() +
                            " пересекается по времени" + " с " + task.getName());
                }
            }
        }
    }
}