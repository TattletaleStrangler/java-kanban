package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.tasktracker.ecxeptions.ManagerRestoreException;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.web.client.KVTaskClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HttpTaskManager(String serverURL) {
        super(null, false);
        kvTaskClient = new KVTaskClient(serverURL);
    }

    public HttpTaskManager(String serverURL, boolean restore) {
        super(null, false);
        kvTaskClient = new KVTaskClient(serverURL);

        if (restore) {
            restore();
        }
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(tasks.values());
        String jsonSubtasks = gson.toJson(subtasks.values());
        String jsonEpics = gson.toJson(epics.values());
        List<Integer> historyOfId = historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        String jsonHistory = gson.toJson(historyOfId);

        kvTaskClient.put("tasks", jsonTasks);
        kvTaskClient.put("subtasks", jsonSubtasks);
        kvTaskClient.put("epics", jsonEpics);
        kvTaskClient.put("history", jsonHistory);
    }

    @Override
    protected void restore() {
        try {
            String jsonTask = kvTaskClient.load("tasks");
            String jsonSubtask = kvTaskClient.load("subtasks");
            String jsonEpic = kvTaskClient.load("epics");
            String jsonHistory = kvTaskClient.load("history");

            List<Task> tasksFromServer = gson.fromJson(jsonTask, new TypeToken<ArrayList<Task>>() {
            }.getType());
            putTasks(tasks, tasksFromServer);
            priorityOrderTasks.addAll(tasksFromServer);

            List<Subtask> subtasksFromServer = gson.fromJson(jsonSubtask, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            putTasks(subtasks, subtasksFromServer);
            priorityOrderTasks.addAll(subtasksFromServer);

            List<Epic> epicsFromServer = gson.fromJson(jsonEpic, new TypeToken<ArrayList<Epic>>() {
            }.getType());
            putTasks(epics, epicsFromServer);

            List<Integer> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Integer>>() {
            }.getType());
            historyRestore(history);

            updateEpics();
        } catch (JsonParseException | IOException e) {
            throw new ManagerRestoreException("Ошибка восстановления из файла: " + e.getMessage());
        }
    }

    private <T extends Task> void putTasks(Map<Integer, T> tasksMap, List<T> list) {
        if (list != null) {
            list.forEach(task -> tasksMap.put(task.getId(), task));
        }
    }

    protected void historyRestore(List<Integer> history) throws IOException {
        Collections.reverse(history);

        Map<Integer, Task> allTasks = new HashMap<>();
        allTasks.putAll(tasks);
        allTasks.putAll(epics);
        allTasks.putAll(subtasks);

        for (Integer id : history) {
            if (allTasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else {
                throw new IOException("история содержит несуществующую задачу с id = " + id);
            }
        }
    }
}