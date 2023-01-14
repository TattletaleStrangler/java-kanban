package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import ru.yandex.practicum.tasktracker.ecxeptions.ManagerRestoreException;
import ru.yandex.practicum.tasktracker.ecxeptions.ManagerSaveException;
import ru.yandex.practicum.tasktracker.ecxeptions.TaskReadException;
import ru.yandex.practicum.tasktracker.managers.historymanagers.HistoryManager;
import ru.yandex.practicum.tasktracker.tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File backupFile;

    private final String title = "id,type,name,status,description,epic,duration,startTime,";

    public FileBackedTasksManager(File file, Boolean restore) {
        backupFile = file;

        if (restore) {
            restore();
        }

        save();
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file, true);
        return manager;
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Integer addNewTask(Task task) {
        Integer result = super.addNewTask(task);
        save();
        return result;
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        Integer result = super.addNewEpic(epic);
        save();
        return result;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Integer result = super.addNewSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return super.getSubtasksByEpic(epic);
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> tasks = manager.getHistory();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            builder.append(tasks.get(i).getId());
            if (i != tasks.size() - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    private static List<Integer> historyFromString(String value) throws IOException {
        try {
            List<Integer> history = new ArrayList<>();

            if (value == null) {
                return history;
            }

            String[] tasksId = value.split(",");

            for (String id : tasksId) {
                history.add(Integer.parseInt(id));
            }

            return history;
        } catch (NumberFormatException e) {
            String message = e.getMessage();
            String causeOfProblem = message.substring(message.lastIndexOf(":") + 2);
            throw new IOException("ошибка преобразования id " + causeOfProblem + " в строке '" + value + "'");
        }
    }

    private Task fromString(String value) throws TaskReadException {
        try {
            String[] taskData = value.split(",", -1);
            int id = Integer.parseInt(taskData[0]);
            TaskType type = TaskType.valueOf(taskData[1]);
            String name = taskData[2];
            TaskStatus status = TaskStatus.valueOf(taskData[3]);
            String description = taskData[4];

            Duration duration = Duration.ZERO;
            if (taskData[6].length() != 0) {
                duration = Duration.parse(taskData[6]);
            }

            LocalDateTime startTime = null;
            if (taskData[7].length() != 0) {
                startTime = LocalDateTime.parse(taskData[7], Task.DATE_TIME_FORMATTER);
            }

            LocalDateTime endTime;
            Task result;

            switch (type) {
                case TASK:
                    result = new Task(id, name, description, status);
                    break;

                case EPIC:
                    Epic epic = new Epic(id, name, description, status);
                    if (taskData[7].length() != 0) {
                        endTime = LocalDateTime.parse(taskData[7], Task.DATE_TIME_FORMATTER);
                        epic.setEndTime(endTime);
                    }
                    result = epic;
                    break;

                case SUBTASK:
                    int idEpic = Integer.parseInt(taskData[5]);
                    result = new Subtask(id, name, description, idEpic, status);
                    break;

                default:
                    throw new TaskReadException("Работа с типом задач " + type + " не поддерживается.");
            }

            result.setStartTime(startTime);
            result.setDuration(duration);

            return result;
        } catch (NumberFormatException e) {
            String message = e.getMessage();
            String causeOfProblem = message.substring(message.lastIndexOf(":") + 2);
            throw new TaskReadException("ошибка преобразования id " + causeOfProblem + " в строке '" + value + "'");
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            String causeOfProblem = message.substring(message.lastIndexOf(".") + 1);
            throw new TaskReadException("обнаружен несуществующий тип задачи или статус: '" + causeOfProblem +
                    "' в строке '" + value + "'");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TaskReadException("нарушено форматирование или отсутствуют данные в строке '" + value + "'");
        } catch (Exception e) {
            throw new TaskReadException(e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile))) {
            writer.write(title + "\n");

            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(tasks.values());
            allTasks.addAll(epics.values());
            allTasks.addAll(subtasks.values());

            for (Task task : allTasks) {
                writer.write(task.toString() + "\n");
            }

            writer.write("\n");

            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла: " + e.getMessage());
        }
    }

    private void restore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(backupFile))) {
            int maxId = 0;
            reader.readLine();

            while (reader.ready()) {
                String line = reader.readLine();

                if ("".equals(line)) {
                    break;
                }

                Task task = fromString(line);

                if (task == null) {
                    throw new IOException("Ошибка чтения задачи: " + line);
                }

                int id = task.getId();

                if (task instanceof Subtask) {
                    priorityOrderTasks.add(task);
                    subtasks.put(id, (Subtask) task);
                } else if (task instanceof Epic) {
                    epics.put(id, (Epic) task);
                } else {
                    priorityOrderTasks.add(task);
                    tasks.put(id, task);
                }

                if (id > maxId) {
                    maxId = id;
                }
            }

            nextId = maxId + 1;

            String history = reader.readLine();

            historyRestore(history);
            restoreSubtasksListOfEpics();
            updateEpics();
        } catch (IOException | TaskReadException e) {
            throw new ManagerRestoreException("Ошибка восстановления из файла: " + e.getMessage());
        }
    }

    private void historyRestore(String history) throws IOException {
        List<Integer> idHistory = historyFromString(history);
        Collections.reverse(idHistory);

        Map<Integer, Task> allTasks = new HashMap<>();
        allTasks.putAll(tasks);
        allTasks.putAll(epics);
        allTasks.putAll(subtasks);

        for (Integer id : idHistory) {
            if (allTasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else {
                throw new IOException("история содержит несуществующую задачу с id = " + id);
            }
        }
    }

    private void restoreSubtasksListOfEpics() {
        for (Subtask subtask : subtasks.values()) {
            epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
        }
    }

    private void updateEpics() {
        for(Epic epic : epics.values()) {
            updateEpic(epic.getId());
        }
    }
}
