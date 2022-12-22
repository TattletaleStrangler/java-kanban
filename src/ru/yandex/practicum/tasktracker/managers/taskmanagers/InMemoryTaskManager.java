package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.managers.historymanagers.HistoryManager;
import ru.yandex.practicum.tasktracker.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Integer nextId;

    public InMemoryTaskManager() {
        nextId = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteEpics() {
        removeTasksFromHistory(epics);
        epics.clear();
        deleteSubtasks();
    }

    @Override
    public void deleteSubtasks() {
        removeTasksFromHistory(subtasks);
        subtasks.clear();

        for (Epic epic : epics.values()) {
            List<Integer> subtasksOfEpic = epic.getSubtasksId();
            subtasksOfEpic.clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteTasks() {
        removeTasksFromHistory(tasks);
        tasks.clear();
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);

        if (epic == null) {
            return null;
        }

        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);

        if (subtask == null) {
            return null;
        }

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);

        if (task == null) {
            return null;
        }

        historyManager.add(task);
        return task;
    }

    @Override
    public Integer addNewTask(Task task) {
        if (task == null) {
            return 0;
        }

        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }

        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epic.getId();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (subtask == null) {
            return 0;
        }

        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            return 0;
        }

        Integer subtaskId = generateId();
        subtask.setId(subtaskId);
        epic.addSubtask(subtaskId);
        subtasks.put(subtaskId, subtask);
        updateEpicStatus(epic.getId());
        return subtaskId;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }

        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = epics.remove(id);

        if (epic == null) {
            return;
        }

        List<Integer> subtasksOfEpic = epic.getSubtasksId();

        for (Integer subtaskId : subtasksOfEpic) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }

        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = subtasks.remove(id);

        if (subtask == null) {
            return;
        }

        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasksId().remove(subtask.getId());
        updateEpicStatus(epic.getId());
        historyManager.remove(id);
    }

    @Override
    public void deleteTaskById(Integer id) {
        Task task = tasks.remove(id);

        if (task == null) {
            return;
        }

        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();

        if (epic == null) {
            return subtasksOfEpic;
        }

        for (Integer subtaskId : epic.getSubtasksId()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
        }

        return subtasksOfEpic;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Integer epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtasksOfEpic = epic.getSubtasksId();
        boolean statusIsDone = true;
        boolean statusIsNew = true;

        for (Integer subtaskId : subtasksOfEpic) {
            Subtask subtask = subtasks.get(subtaskId);
            subtask.setEpicId(epic.getId());
            TaskStatus subtaskStatus = subtask.getStatus();

            if (!subtaskStatus.equals(TaskStatus.DONE)) {
                statusIsDone = false;
            }

            if (!subtaskStatus.equals(TaskStatus.NEW)) {
                statusIsNew = false;
            }
        }

        if (statusIsDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (statusIsNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private Integer generateId() {
        return nextId++;
    }

    private void removeTasksFromHistory (Map<Integer, ? extends Task> taskMap) {
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
        }
    }
}
