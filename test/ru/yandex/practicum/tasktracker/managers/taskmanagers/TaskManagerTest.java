package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.tasktracker.ecxeptions.TaskTimeException;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;
import ru.yandex.practicum.tasktracker.tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.tasktracker.tasks.TaskStatus.*;

abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;

    @Test
    void getEpics() {
        Epic epic1 = new Epic("Test getEpics 1", "Test getEpics description 1");
        Epic epic2 = new Epic("Test getEpics 2", "Test getEpics description 2");
        Epic epic3 = new Epic("Test getEpics 3", "Test getEpics description 3");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);

        final List<Epic> epics = taskManager.getEpics();
        List<Epic> expectedEpics = List.of(epic1, epic2, epic3);

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(3, epics.size(), "Неверное количество эпиков.");
        assertIterableEquals(expectedEpics, epics, "Эпики не совпадают.");
    }

    @Test
    void getEpicsWithEmptyList() {
        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void getSubtasks() {
        Epic epic = new Epic("Test getSubtasks epic", "Test getSubtasks epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test getSubtasks 1", "Test getSubtasks description 1"
                , savedEpic.getId(), NEW);
        Subtask subtask2 = new Subtask("Test getSubtasks 2", "Test getSubtasks description 2"
                , savedEpic.getId(), NEW);
        Subtask subtask3 = new Subtask("Test getSubtasks 3", "Test getSubtasks description 3"
                , savedEpic.getId(), NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        final List<Subtask> subtasks = taskManager.getSubtasks();
        List<Subtask> expectedSubtasks = List.of(subtask1, subtask2, subtask3);

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач.");
        assertIterableEquals(expectedSubtasks, subtasks);
    }

    @Test
    void getSubtasksWithEmptyList() {
        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void getTasks() {
        Task task1 = new Task("Test getTasks 1", "Test getTasks description 1", NEW);
        Task task2 = new Task("Test getTasks 2", "Test getTasks description 2", NEW);
        Task task3 = new Task("Test getTasks 3", "Test getTasks description 3", NEW);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        final List<Task> tasks = taskManager.getTasks();
        List<Task> expectedTasks = List.of(task1, task2, task3);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertIterableEquals(expectedTasks, tasks, "Задачи не совпадают.");
    }

    @Test
    void getTasksWithEmptyList() {
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteEpics() {
        Epic epic1 = new Epic("Test deleteEpics 1", "Test deleteEpics description 1");
        Epic epic2 = new Epic("Test deleteEpics 2", "Test deleteEpics description 2");
        Epic epic3 = new Epic("Test deleteEpics 3", "Test deleteEpics description 3");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.addNewEpic(epic3);

        taskManager.deleteEpics();

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void deleteEpicsWithEmptyList() {
        taskManager.deleteEpics();

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @Test
    void deleteSubtasks() {
        Epic epic = new Epic("Test deleteSubtasks epic", "Test deleteSubtasks epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test deleteSubtasks 1", "Test deleteSubtasks description 1"
                , savedEpic.getId(), NEW);
        Subtask subtask2 = new Subtask("Test deleteSubtasks 2", "Test deleteSubtasks description 2"
                , savedEpic.getId(), NEW);
        Subtask subtask3 = new Subtask("Test deleteSubtasks 3", "Test deleteSubtasks description 3"
                , savedEpic.getId(), NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask1);

        taskManager.deleteSubtasks();

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void deleteSubtasksWithEmptyList() {
        taskManager.deleteSubtasks();

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void deleteTasks() {
        Task task1 = new Task("Test deleteTasks 1", "Test deleteTasks description 1", NEW);
        Task task2 = new Task("Test deleteTasks 2", "Test deleteTasks description 2", NEW);
        Task task3 = new Task("Test deleteTasks 3", "Test deleteTasks description 3", NEW);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        taskManager.deleteTasks();

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTasksWithEmptyList() {
        taskManager.deleteTasks();

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Test getEpicById", "Test getEpicById description");
        final int epicId = taskManager.addNewEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(epic.getStatus(), NEW);
    }

    @Test
    void getEpicByIdWithEmptyList() {
        int epicId = 1;
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNull(savedEpic, "Возвращен не null.");
    }

    @Test
    void getEpicByIdWithWrongId() {
        Epic epic = new Epic("Test getEpicByIdWithWrongId", "Test getEpicByIdWithWrongId description");
        final int epicId = taskManager.addNewEpic(epic);

        int wrongEpicId = epicId + 1;
        final Epic savedEpic = taskManager.getEpicById(wrongEpicId);

        assertNull(savedEpic, "Возвращен не null.");
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Test getSubtaskById epic", "Test getSubtaskById epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask = new Subtask("Test getSubtaskById subtask"
                , "Test getSubtaskById subtask description", savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void getSubtaskByIdWithEmptyList() {
        int subtaskId = 1;
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNull(savedSubtask, "Возвращен не null.");
    }

    @Test
    void getSubtaskByIdWithWrongId() {
        Epic epic = new Epic("Test getSubtaskByIdWithWrongId epic"
                , "Test getSubtaskByIdWithWrongId epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask = new Subtask("Test getSubtaskByIdWithWrongId subtask"
                , "Test getSubtaskByIdWithWrongId subtask description", savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final int wrongSubtaskId = subtaskId + 1;
        final Subtask savedSubtask = taskManager.getSubtaskById(wrongSubtaskId);

        assertNull(savedSubtask, "Возвращен не null.");
    }

    @Test
    void getTaskById() {
        Task task = new Task("Test getTaskById", "Test getTaskById description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void getTaskByIdWithEmptyList() {
        final int taskId = 1;
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNull(savedTask, "Возвращен не null.");
    }

    @Test
    void getTaskByIdWithWrongId() {
        Task task = new Task("Test getTaskById", "Test getTaskById description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final int wrongTaskId = taskId + 1;
        final Task savedTask = taskManager.getTaskById(wrongTaskId);

        assertNull(savedTask, "Возвращен не null.");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void addNewTaskWithWrongId() {
        final int wrongId = 10;
        Task task = new Task(wrongId, "Test addNewTaskWithWrongId"
                , "Test addNewTaskWithWrongId description", NEW);
        final int taskId = taskManager.addNewTask(task);
        assertNotEquals(wrongId, taskId);

        final Task savedTask = taskManager.getTaskById(wrongId);

        assertNull(savedTask, "Возвращен не null.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addNewEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(epic.getStatus(), NEW);
    }

    @Test
    void addNewEpicWithWrongId() {
        final int wrongId = 10;
        Epic epic = new Epic(wrongId, "Test addNewEpicWithWrongId"
                , "Test addNewEpicWithWrongId description", NEW);
        final int epicId = taskManager.addNewEpic(epic);
        assertNotEquals(wrongId, epicId);

        final Epic savedEpic = taskManager.getEpicById(wrongId);

        assertNull(savedEpic, "Возвращен не null.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description"
                , savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        savedEpic = taskManager.getEpicById(epicId);
        assertEquals(savedEpic.getStatus(), NEW);

        final List<Integer> subtasks = savedEpic.getSubtasksId();

        assertNotNull(subtasks, "Подзадачи эпика на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertTrue(subtasks.contains(subtask.getId()), "Подзадача не добавлена в эпик.");
    }

    @Test
    void addNewSubtaskWithWrongId() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        final int wrongId = 10;
        Subtask subtask = new Subtask(wrongId, "Test addNewSubtask", "Test addNewSubtask description"
                , savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        assertNotEquals(wrongId, subtaskId);

        final Subtask savedSubtask = taskManager.getSubtaskById(wrongId);

        assertNull(savedSubtask, "Возвращен не null.");

        savedEpic = taskManager.getEpicById(epicId);
        assertEquals(savedEpic.getStatus(), NEW);
        final List<Integer> subtasks = savedEpic.getSubtasksId();

        assertNotNull(subtasks, "Подзадачи эпика на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertFalse(subtasks.contains(wrongId), "Подзадача с неверным ID добавлена в эпик.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test updateEpic", "Test updateEpic description");
        final int epicId = taskManager.addNewEpic(epic);

        Epic updatedEpic = new Epic(epicId, "Test updateEpic updated", "Test updateEpic description updated", NEW);
        taskManager.updateEpic(updatedEpic);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask = new Subtask("Test updateEpic subtask", "Test updateEpic subtask description"
                , savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Integer> subtasks = savedEpic.getSubtasksId();

        assertNotNull(subtasks, "Подзадачи эпика на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertTrue(subtasks.contains(subtask.getId()), "Подзадача не добавлена в эпик.");

        assertEquals(updatedEpic, savedEpic, "Эпики не совпадают");
        assertEquals(updatedEpic.getStatus(), NEW);
    }

    @Test
    void updateEpicWithWrongId() {
        Epic epic = new Epic("Test updateEpic", "Test updateEpic description");
        final int epicId = taskManager.addNewEpic(epic);

        final int wrongEpicId = epicId + 1;
        Epic updatedEpic = new Epic(wrongEpicId, "Test updateEpic updated", "Test updateEpic description updated", NEW);
        taskManager.updateEpic(updatedEpic);

        final Epic savedEpicWithWrongId = taskManager.getEpicById(wrongEpicId);

        assertNull(savedEpicWithWrongId, "Возвращен не null.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test updateTask", "Test updateTask description", NEW);
        final int taskId = taskManager.addNewTask(task);

        Task updatedTask = new Task(taskId, "Test updateTask updated", "Test updateTask description updated", IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        assertEquals(updatedTask, savedTask, "Задачи не совпадают.");
    }

    @Test
    void updateTaskWithWrongId() {
        Task task = new Task("Test updateTask", "Test updateTask description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final int wrongTaskId = taskId + 1;
        Task updatedTask = new Task(wrongTaskId, "Test updateTask updated", "Test updateTask description updated", IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        final Task savedTaskWithWrongId = taskManager.getTaskById(wrongTaskId);

        assertNull(savedTaskWithWrongId, "Возвращен не null");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Test updateSubtask epic", "Test updateSubtask epic description");
        final int epicId = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask = new Subtask("Test updateSubtask subtask"
                , "Test updateSubtask subtask description", savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        Subtask updatedSubtask = new Subtask(subtaskId, "Test updateSubtask subtask"
                , "Test updateSubtask subtask description", savedEpic.getId(), DONE);
        taskManager.updateSubtask(updatedSubtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(updatedSubtask, savedSubtask, "Задачи не совпадают.");

        savedEpic = taskManager.getEpicById(epicId);
        assertEquals(savedEpic.getStatus(), DONE);
        final List<Integer> subtasks = savedEpic.getSubtasksId();

        assertNotNull(subtasks, "Подзадачи эпика на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertTrue(subtasks.contains(savedSubtask.getId()), "Подзадача не добавлена в эпик.");
    }

    @Test
    void updateSubtaskWithWrongId() {
        Epic epic = new Epic("Test updateSubtaskWithWrongId epic"
                , "Test updateSubtaskWithWrongId epic description");
        final int epicId = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask = new Subtask("Test updateSubtaskWithWrongId subtask"
                , "Test updateSubtaskWithWrongId subtask description", savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final int wrongSubtaskId = subtaskId + 1;
        Subtask updatedSubtask = new Subtask(wrongSubtaskId, "Test updateSubtaskWithWrongId subtask"
                , "Test updateSubtaskWithWrongId subtask description", savedEpic.getId(), NEW);
        taskManager.updateSubtask(updatedSubtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(wrongSubtaskId);

        assertNull(savedSubtask, "Возвращен не null.");
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Test deleteEpicById", "Test deleteEpicById description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test deleteEpicById 1", "Test deleteEpicById description 1"
                , savedEpic.getId(), NEW);
        Subtask subtask2 = new Subtask("Test deleteEpicById 2", "Test deleteEpicById description 2"
                , savedEpic.getId(), NEW);
        Subtask subtask3 = new Subtask("Test deleteEpicById 3", "Test deleteEpicById description 3"
                , savedEpic.getId(), NEW);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask1);
        final int subtask3Id = taskManager.addNewSubtask(subtask1);

        taskManager.deleteEpicById(epicId);
        Epic deletedEpic = taskManager.getEpicById(epicId);
        assertNull(deletedEpic, "Эпик не удален.");

        Subtask deletedSubtask1 = taskManager.getSubtaskById(subtask1Id);
        Subtask deletedSubtask2 = taskManager.getSubtaskById(subtask2Id);
        Subtask deletedSubtask3 = taskManager.getSubtaskById(subtask3Id);
        assertNull(deletedSubtask1, "Подзадача не удалена.");
        assertNull(deletedSubtask2, "Подзадача не удалена.");
        assertNull(deletedSubtask3, "Подзадача не удалена.");
    }

    @Test
    void deleteEpicByIdWithWrongId() {
        Epic epic = new Epic("Test deleteEpicById", "Test deleteEpicById description");
        final int epicId = taskManager.addNewEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test deleteEpicById 1", "Test deleteEpicById description 1"
                , savedEpic.getId(), NEW);
        Subtask subtask2 = new Subtask("Test deleteEpicById 2", "Test deleteEpicById description 2"
                , savedEpic.getId(), NEW);
        Subtask subtask3 = new Subtask("Test deleteEpicById 3", "Test deleteEpicById description 3"
                , savedEpic.getId(), NEW);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask1);
        final int subtask3Id = taskManager.addNewSubtask(subtask1);

        savedEpic = taskManager.getEpicById(epicId);

        final int wrongEpicId = epicId + 1;
        taskManager.deleteEpicById(wrongEpicId);
        Epic deletedEpic = taskManager.getEpicById(epicId);
        assertNotNull(deletedEpic, "Эпик удален.");
        assertEquals(savedEpic, deletedEpic, "Эпики не равны");

        Subtask deletedSubtask1 = taskManager.getSubtaskById(subtask1Id);
        Subtask deletedSubtask2 = taskManager.getSubtaskById(subtask2Id);
        Subtask deletedSubtask3 = taskManager.getSubtaskById(subtask3Id);
        assertNotNull(deletedSubtask1, "Подзадача удалена.");
        assertNotNull(deletedSubtask2, "Подзадача удалена.");
        assertNotNull(deletedSubtask3, "Подзадача удалена.");
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("Test deleteSubtaskById epic", "Test deleteSubtaskById epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test deleteSubtasks 1", "Test deleteSubtasks description 1"
                , savedEpic.getId(), NEW);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        taskManager.deleteSubtaskById(subtask1Id);
        Subtask deletedSubtask = taskManager.getSubtaskById(subtask1Id);

        assertNull(deletedSubtask, "Подзадача не удалена.");

        List<Integer> epicSubtasks = savedEpic.getSubtasksId();
        assertFalse(epicSubtasks.contains(subtask1Id), "Подзадача из эпика не удалена");
    }

    @Test
    void deleteSubtaskByIdWithWrongId() {
        Epic epic = new Epic("Test deleteSubtaskByIdWithWrongId epic"
                , "Test deleteSubtaskByIdWithWrongId epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test deleteSubtasks 1", "Test deleteSubtasks description 1"
                , savedEpic.getId(), NEW);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        Subtask savedSubtask = taskManager.getSubtaskById(subtask1Id);
        assertNotNull(savedSubtask, "Подзадача не найдена.");

        final int wrongSubtaskId = subtask1Id + 1;
        taskManager.deleteSubtaskById(wrongSubtaskId);
        Subtask deletedSubtask = taskManager.getSubtaskById(subtask1Id);

        assertNotNull(deletedSubtask, "Подзадача удалена.");
        List<Integer> epicSubtasks = savedEpic.getSubtasksId();
        assertTrue(epicSubtasks.contains(subtask1Id), "Подзадача удалена из эпика.");
        assertEquals(savedSubtask, deletedSubtask, "Подзадачи не равны.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Test deleteTaskById", "Test deleteTaskById description", NEW);
        final int taskId = taskManager.addNewTask(task);

        taskManager.deleteTaskById(taskId);
        Task deletedTask = taskManager.getTaskById(taskId);

        assertNull(deletedTask, "Задача не удалена.");
    }

    @Test
    void deleteTaskByIdWithWrongId() {
        Task task = new Task("Test deleteTaskById", "Test deleteTaskById description", NEW);
        final int taskId = taskManager.addNewTask(task);

        Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        final int wrongTaskId = taskId + 1;
        taskManager.deleteTaskById(wrongTaskId);
        Task deletedTask = taskManager.getTaskById(taskId);

        assertNotNull(deletedTask, "Задача удалена.");
        assertEquals(savedTask, deletedTask, "Задачи не равны");
    }

    @Test
    void getSubtasksByEpic() {
        Epic epic = new Epic("Test deleteEpicById", "Test deleteEpicById description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test deleteEpicById 1", "Test deleteEpicById description 1"
                , savedEpic.getId(), NEW);
        Subtask subtask2 = new Subtask("Test deleteEpicById 2", "Test deleteEpicById description 2"
                , savedEpic.getId(), NEW);
        Subtask subtask3 = new Subtask("Test deleteEpicById 3", "Test deleteEpicById description 3"
                , savedEpic.getId(), NEW);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask1);
        final int subtask3Id = taskManager.addNewSubtask(subtask1);

        Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1Id);
        assertNotNull(savedSubtask1, "Подзадача не найдена");
        Subtask savedSubtask2 = taskManager.getSubtaskById(subtask2Id);
        assertNotNull(savedSubtask2, "Подзадача не найдена");
        Subtask savedSubtask3 = taskManager.getSubtaskById(subtask3Id);
        assertNotNull(savedSubtask3, "Подзадача не найдена");

        List<Subtask> expectedSubtasks = List.of(savedSubtask1, savedSubtask2, savedSubtask3);
        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpic(savedEpic);

        assertIterableEquals(expectedSubtasks, epicSubtasks, "Подзадачи эпика не соответстуют ожидаемым");
    }

    @Test
    void getHistory() {
        Task task = new Task("Test getHistory task", "Test getHistory task description", NEW);
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        Epic epic = new Epic("Test getHistory epic", "Test getHistory epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask = new Subtask("Test getHistory subtask", "Test getHistory subtask description"
                , savedEpic.getId(), NEW);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");

        List<Task> expectedHistory = List.of(savedSubtask, savedEpic, savedTask);
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertIterableEquals(expectedHistory, history, "История не соответстует ожидаемой");
    }

    @Test
    void getHistoryWithEmptyList() {
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "История не пуста.");
    }

    @Test
    void shouldReturnStatusNewWithEmptySubtasksList() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusNewWithThreeNewSubtasks() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Test epic subtask 3", "Description of test epic subtask 3", epicId, TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusDoneWithThreeDoneSubtasks() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.DONE);
        Subtask subtask3 = new Subtask("Test epic subtask 3", "Description of test epic subtask 3", epicId, TaskStatus.DONE);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusInProgressWithOneDoneSubtaskAndOneNewSubtask() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void shouldReturnStatusInProgressWithThreeInProgressSubtasks() {
        Epic epic = new Epic("Test epic", "Description test epic");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test epic subtask 1", "Description of test epic subtask 1", epicId, TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Test epic subtask 2", "Description of test epic subtask 2", epicId, TaskStatus.IN_PROGRESS);
        Subtask subtask3 = new Subtask("Test epic subtask 3", "Description of test epic subtask 3", epicId, TaskStatus.IN_PROGRESS);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    void getPrioritizedTasksWithoutTime() {
        Task task = new Task("Test getPrioritizedTasks task", "Test getPrioritizedTasks task description", NEW);
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        Epic epic = new Epic("Test getPrioritizedTasks epic", "Test getPrioritizedTasks epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test getPrioritizedTasks subtask1",
                "Test getPrioritizedTasks subtask1 description", savedEpic.getId(), NEW);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1Id);
        assertNotNull(savedSubtask1, "Подзадача не найдена.");

        Subtask subtask2 = new Subtask("Test getPrioritizedTasks subtask2",
                "Test getPrioritizedTasks subtask2 description", savedEpic.getId(), NEW);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);
        final Subtask savedSubtask2 = taskManager.getSubtaskById(subtask2Id);
        assertNotNull(savedSubtask2, "Подзадача не найдена.");

        List<Task> expected = List.of(savedTask, savedSubtask1, savedSubtask2);
        List<Task> received = taskManager.getPrioritizedTasks();
        assertNotNull(received, "Список задач в порядке приоритета не возвращается.");
        assertEquals(expected.size(), received.size(), "Размер списка задач в порядке приоритета не совпадает с ожидаемым.");
        assertIterableEquals(expected, received, "Задачи в списке отличаются от ожидаемых.");
    }

    @Test
    void getPrioritizedTasks() {
        Task task = new Task("Test getPrioritizedTasks task", "Test getPrioritizedTasks task description", NEW);
        LocalDateTime taskStartTime = LocalDateTime.now();
        task.setStartTime(taskStartTime);
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");

        Epic epic = new Epic("Test getPrioritizedTasks epic", "Test getPrioritizedTasks epic description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");

        Subtask subtask1 = new Subtask("Test getPrioritizedTasks subtask1",
                "Test getPrioritizedTasks subtask1 description", savedEpic.getId(), NEW);
        LocalDateTime subtask1StartTime = taskStartTime.plusHours(2);
        subtask1.setStartTime(subtask1StartTime);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1Id);
        assertNotNull(savedSubtask1, "Подзадача не найдена.");

        Subtask subtask2 = new Subtask("Test getPrioritizedTasks subtask2",
                "Test getPrioritizedTasks subtask2 description", savedEpic.getId(), NEW);
        LocalDateTime subtask2StartTime = taskStartTime.plusHours(1);
        subtask2.setStartTime(subtask2StartTime);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);
        final Subtask savedSubtask2 = taskManager.getSubtaskById(subtask2Id);
        assertNotNull(savedSubtask2, "Подзадача не найдена.");

        List<Task> expected = List.of(savedTask, savedSubtask2, savedSubtask1);
        List<Task> received = taskManager.getPrioritizedTasks();
        assertNotNull(received, "Список задач в порядке приоритета не возвращается.");
        assertEquals(expected.size(), received.size(), "Размер списка задач не совпадает с ожидаемым.");
        assertIterableEquals(expected, received, "Задачи в списке отличаются от ожидаемых.");
    }

    @Test
    void getPrioritizedTasksWithEmptyList() {
        List<Task> received = taskManager.getPrioritizedTasks();
        assertNotNull(received, "Список задач в порядке приоритета не возвращается.");
        assertEquals(0, received.size(), "Размер списка задач не совпадает с ожидаемым.");
    }

    @Test
    void shouldReturnTaskTimeException() {
        Task task1 = new Task("Task1", "Task1 description", NEW);
        LocalDateTime task1StartTime = LocalDateTime.now();
        task1.setStartTime(task1StartTime);
        int task1Id = taskManager.addNewTask(task1);

        Task task2 = new Task("Task2", "Task2 description", NEW);
        LocalDateTime task2StartTime = task1StartTime;
        task2.setStartTime(task2StartTime);

        final TaskTimeException exception = assertThrows(
                TaskTimeException.class,
                () -> taskManager.addNewTask(task2)
        );

        String expectedMessage = "Задача пересекается с уже существующей.";
        assertEquals(expectedMessage, exception.getMessage(), "Исключение не выброшено.");
    }
}