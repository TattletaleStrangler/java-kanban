package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.managers.historymanagers.HistoryManager;
import ru.yandex.practicum.tasktracker.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer nextId;

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
        epics.clear();
        deleteSubtasks();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            List<Integer> subtasksOfEpic = epic.getSubtasksId();
            subtasksOfEpic.clear();
        }
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Integer addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epic.getId();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        return subtask.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = epics.get(id);
        List<Integer> subtasksOfEpic = epic.getSubtasksId();

        for (Integer subtask : subtasksOfEpic) {
            subtasks.remove(subtask);
        }

        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = getSubtaskById(id);

        for (Epic epic : epics.values()) {
            epic.getSubtasksId().remove(subtask.getId());
        }

        subtasks.remove(id);
    }

    @Override
    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();

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
            subtasks.get(subtaskId).setEpicId(epic.getId());

            if (!subtasks.get(subtaskId).getStatus().equals(TaskStatus.DONE)) {
                statusIsDone = false;
            }

            if (!subtasks.get(subtaskId).getStatus().equals(TaskStatus.NEW)) {
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
}
