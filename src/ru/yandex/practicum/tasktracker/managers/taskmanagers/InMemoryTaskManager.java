package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import ru.yandex.practicum.tasktracker.ecxeptions.TaskTimeException;
import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.managers.historymanagers.HistoryManager;
import ru.yandex.practicum.tasktracker.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final TreeSet<Task> priorityOrderTasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<LocalDateTime, Boolean> controlIntervals = new HashMap<>(COUNT_CONTROL_INTERVAL_IN_YEAR);
    protected Integer nextId;
    private static final int CONTROL_INTERVAL_IN_MINUTES = 15;
    private static final int COUNT_CONTROL_INTERVAL_IN_YEAR = 365 * 24 * 60 / CONTROL_INTERVAL_IN_MINUTES;

    public InMemoryTaskManager() {
        nextId = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        priorityOrderTasks = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() != null && task2.getEndTime() != null) {
                return task1.getStartTime().compareTo(task2.getStartTime());
            }

            return task1.getId().compareTo(task2.getId());
        });
        fillTimeIntervals();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(priorityOrderTasks);
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

        removeTasksFromHistory(subtasks);
        subtasks.values().forEach(priorityOrderTasks::remove);
        subtasks.values().forEach(this::vacateTimeIntervalsFromTask);
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        removeTasksFromHistory(subtasks);

        subtasks.values().forEach(priorityOrderTasks::remove);
        subtasks.values().forEach(this::vacateTimeIntervalsFromTask);
        subtasks.clear();

        for (Epic epic : epics.values()) {
            List<Integer> subtasksOfEpic = epic.getSubtasksId();
            subtasksOfEpic.clear();
            updateEpic(epic.getId());
        }
    }

    @Override
    public void deleteTasks() {
        removeTasksFromHistory(tasks);

        tasks.values().forEach(priorityOrderTasks::remove);
        tasks.values().forEach(this::vacateTimeIntervalsFromTask);
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

        validateTaskTime(task);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        priorityOrderTasks.add(task);
        occupyTimeIntervalsByTask(task);
        return task.getId();
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        if (epic == null) {
            return 0;
        }

        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpic(epic.getId());
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

        validateTaskTime(subtask);

        Integer subtaskId = generateId();
        subtask.setId(subtaskId);
        epic.addSubtask(subtaskId);
        subtasks.put(subtaskId, subtask);
        updateEpic(epic.getId());
        priorityOrderTasks.add(subtask);
        occupyTimeIntervalsByTask(subtask);
        return subtaskId;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }

        epics.put(epic.getId(), epic);
        updateEpic(epic.getId());
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }

        Task oldTask = tasks.get(task.getId());
        vacateTimeIntervalsFromTask(oldTask);

        try {
            validateTaskTime(task);
        } catch (TaskTimeException e) {
            occupyTimeIntervalsByTask(oldTask);
            throw new TaskTimeException(e.getMessage());
        }

        priorityOrderTasks.remove(oldTask);
        priorityOrderTasks.add(task);
        occupyTimeIntervalsByTask(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        vacateTimeIntervalsFromTask(subtask);
        validateTaskTime(subtask);

        try {
            validateTaskTime(subtask);
        } catch (TaskTimeException e) {
            occupyTimeIntervalsByTask(oldSubtask);
            throw new TaskTimeException(e.getMessage());
        }

        priorityOrderTasks.remove(oldSubtask);
        priorityOrderTasks.add(subtask);
        occupyTimeIntervalsByTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpic(subtask.getEpicId());
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = epics.remove(id);

        if (epic == null) {
            return;
        }

        List<Integer> subtasksOfEpic = epic.getSubtasksId();

        for (Integer subtaskId : subtasksOfEpic) {
            Subtask subtask = subtasks.get(subtaskId);
            priorityOrderTasks.remove(subtask);
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            vacateTimeIntervalsFromTask(subtask);
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
        updateEpic(epic.getId());
        historyManager.remove(id);
        priorityOrderTasks.remove(subtask);
        vacateTimeIntervalsFromTask(subtask);
    }

    @Override
    public void deleteTaskById(Integer id) {
        Task task = tasks.remove(id);

        if (task == null) {
            return;
        }

        historyManager.remove(id);
        priorityOrderTasks.remove(task);
        vacateTimeIntervalsFromTask(task);
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

    protected void updateEpic (Integer epicId) {
        Epic epic = epics.get(epicId);
        updateEpicStatus(epic);
        calcEpicTimes(epic);
    }

    private void updateEpicStatus(Epic epic) {
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

        if (statusIsNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (statusIsDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void calcEpicTimes(Epic epic) {
        List<Integer> subtasksOfEpic = epic.getSubtasksId();

        if (subtasksOfEpic.size() == 0) {
            return;
        }

        LocalDateTime startTime;
        LocalDateTime endTime;
        final Duration duration;

        startTime = subtasksOfEpic.stream()
                .map(subtasks::get)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        endTime = subtasksOfEpic.stream()
                .map(subtasks::get)
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        duration = subtasksOfEpic.stream()
                .map(subtasks::get)
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    private Integer generateId() {
        return nextId++;
    }

    private void removeTasksFromHistory(Map<Integer, ? extends Task> taskMap) {
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
        }
    }

    private void validateTaskTime(Task task) {
        if (task.getStartTime() == null) {
            return;
        }

        LocalDateTime startTime = getNearestQuarterOfHour(task.getStartTime());
        LocalDateTime endTime = getNearestQuarterOfHour(task.getEndTime());
        LocalDateTime timeForCheck = startTime;

        while (!timeForCheck.isAfter(endTime)) {
            if (!controlIntervals.get(timeForCheck)) {
                throw new TaskTimeException("Задача пересекается с уже существующей.");
            }

            timeForCheck = timeForCheck.plusMinutes(CONTROL_INTERVAL_IN_MINUTES);
        }

        LocalDateTime timeForOccupy = startTime;
        occupyTimeIntervals(timeForOccupy, endTime);
    }

    private void fillTimeIntervals() {
        LocalDateTime currentTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = currentTime.plusYears(1);
        vacateTimeIntervals(currentTime, endTime);
    }

    private void occupyTimeIntervalsByTask(Task task) {
        if (task.getStartTime() == null) {
            return;
        }

        LocalDateTime startTime = getNearestQuarterOfHour(task.getStartTime());
        LocalDateTime endTime = getNearestQuarterOfHour(task.getEndTime());
        occupyTimeIntervals(startTime, endTime);
    }

    private void vacateTimeIntervalsFromTask(Task task) {
        if (task.getStartTime() == null) {
            return;
        }

        LocalDateTime startTime = getNearestQuarterOfHour(task.getStartTime());
        LocalDateTime endTime = getNearestQuarterOfHour(task.getEndTime());
        vacateTimeIntervals(startTime, endTime);
    }

    private LocalDateTime getNearestQuarterOfHour(LocalDateTime dateTime) {
        int fullFifteenMinutesStart = dateTime.getMinute() / CONTROL_INTERVAL_IN_MINUTES;
        return dateTime.withMinute(fullFifteenMinutesStart * CONTROL_INTERVAL_IN_MINUTES)
                .withSecond(0).withNano(0);
    }

    private void vacateTimeIntervals(LocalDateTime startTime, LocalDateTime endTime) {
        while (!startTime.isAfter(endTime)) {
            controlIntervals.put(startTime, true);
            startTime = startTime.plusMinutes(CONTROL_INTERVAL_IN_MINUTES);
        }
    }

    private void occupyTimeIntervals(LocalDateTime startTime, LocalDateTime endTime) {
        while (!startTime.isAfter(endTime)) {
            controlIntervals.put(startTime, false);
            startTime = startTime.plusMinutes(CONTROL_INTERVAL_IN_MINUTES);
        }
    }
}
