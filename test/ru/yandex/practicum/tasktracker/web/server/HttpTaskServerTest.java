package ru.yandex.practicum.tasktracker.web.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.tasktracker.tasks.TaskStatus.NEW;

class HttpTaskServerTest {
    public final String serverUrl = "http://localhost:8080";
    public final Gson gson = new Gson();
    public final HttpClient httpClient = HttpClient.newHttpClient();
    public KVServer server;
    public HttpTaskServer taskServer;
    public Task task;
    public Task task2;
    public Task task3;
    public Epic epic;
    public Epic epic2;
    public Epic epic3;
    public Subtask subtask1;
    public Subtask subtask2;
    public Subtask subtask3;

    @BeforeEach
    void beforeEach() throws IOException {
        server = new KVServer();
        server.start();
        taskServer = new HttpTaskServer();
        task = new Task("TestTask", "TestTask description", NEW);
        task2 = new Task("TestTask2", "TestTask description2", NEW);
        task3 = new Task("TestTask3", "TestTask description3", NEW);
        epic = new Epic("TestEpic", "TestEpic description");
        epic2 = new Epic("TestEpic2", "TestEpic description 2");
        epic3 = new Epic("TestEpic3", "TestEpic description 3");
        subtask1 = new Subtask("TestSubtasks 1", "TestSubtasks description 1", 0, NEW);
        subtask2 = new Subtask("TestSubtasks 2", "TestSubtasks description 2", 0, NEW);
        subtask3 = new Subtask("TestSubtasks 3", "TestSubtasks description 3", 0, NEW);
    }

    @AfterEach
    void afterEach() {
        server.stop();
        taskServer.stop();
    }

    @Test
    void addNewTask() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");
        String json = gson.toJson(task);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int expectedId = 1;
        task.setId(expectedId);
        String expected = gson.toJson(task);

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + expectedId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void addNewTaskWithWrongId() throws IOException, InterruptedException {
        final int wrongId = 10;
        task.setId(wrongId);

        URI uri = URI.create(serverUrl + "/tasks/task");
        String json = gson.toJson(task);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Ответ сервера не соответствует ожидаемому.");

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера не соответствует ожидаемому.");
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");

        post(gson.toJson(task), uri);
        post(gson.toJson(task2), uri);
        post(gson.toJson(task3), uri);

        task.setId(1);
        task2.setId(2);
        task3.setId(3);

        List<Task> tasks = List.of(task, task2, task3);
        String expected = gson.toJson(tasks);

        URI url = URI.create(serverUrl + "/tasks/task/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Списки задач не совпадают.");
    }

    @Test
    void getTasksWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI url = URI.create(serverUrl + "/tasks/task/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void deleteTasks() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");

        post(gson.toJson(task), uri);
        post(gson.toJson(task2), uri);
        post(gson.toJson(task3), uri);

        delete(uri);

        String expected = "[]";

        URI url = URI.create(serverUrl + "/tasks/task/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void deleteTasksWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI uri = URI.create(serverUrl + "/tasks/task/");
        delete(uri);

        HttpResponse<String> response = get(uri);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");
        String json = gson.toJson(task);
        post(json, uri);

        final int expectedId = 1;
        task.setId(expectedId);
        String expected = gson.toJson(task);

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + expectedId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void getTaskByIdWithEmptyList() throws IOException, InterruptedException {
        final int wrongId = 1;

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера отличается от ожидаемого.");
    }

    @Test
    void getTaskByIdWithWrongId() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");

        post(gson.toJson(task), uri);
        post(gson.toJson(task2), uri);
        post(gson.toJson(task3), uri);


        final int wrongId = 100;

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера отличается от ожидаемого.");
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        final URI uri = URI.create(serverUrl + "/tasks/task");

        post(gson.toJson(task), uri);
        post(gson.toJson(task2), uri);
        post(gson.toJson(task3), uri);

        int deletedId = 2;
        final URI url = URI.create(serverUrl + "/tasks/task?id=" + deletedId);
        delete(url);

        task.setId(1);
        task2.setId(2);
        task3.setId(3);

        List<Task> tasks = List.of(task, task3);
        String expected = gson.toJson(tasks);

        HttpResponse<String> responseForCheck = get(uri);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void deleteTaskByIdWithWrongId() throws IOException, InterruptedException {
        final URI uri = URI.create(serverUrl + "/tasks/task");

        post(gson.toJson(task), uri);
        post(gson.toJson(task2), uri);
        post(gson.toJson(task3), uri);

        int wrongId = 100;
        final URI url = URI.create(serverUrl + "/tasks/task?id=" + wrongId);
        delete(url);

        task.setId(1);
        task2.setId(2);
        task3.setId(3);

        List<Task> tasks = List.of(task, task2, task3);
        String expected = gson.toJson(tasks);

        HttpResponse<String> responseForCheck = get(uri);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");
        String json = gson.toJson(task);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int id = 1;
        task.setId(id);
        task.setName("newTask");
        task.setDescription("newTaskDescription");
        String newTask = gson.toJson(task);

        HttpResponse<String> newResponse = post(newTask, uri);
        int newStatus = newResponse.statusCode();
        assertEquals(200, newStatus, "Код ответа сервера не соответствует ожидаемому.");

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + id);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(newTask, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void updateTaskWithWrongId() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");
        String json = gson.toJson(task);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int wrongId = 100;
        task.setId(wrongId);
        task.setName("newTask");
        task.setDescription("newTaskDescription");
        String newTask = gson.toJson(task);

        HttpResponse<String> newResponse = post(newTask, uri);
        int newStatus = newResponse.statusCode();
        assertEquals(200, newStatus, "Код ответа сервера не соответствует ожидаемому.");

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        String expected = "Задача с id = " + wrongId + " не найдена";
        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Возвращен не пустой массив.");
    }

    @Test
    void addNewEpic() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/epic");
        String json = gson.toJson(epic);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int expectedId = 1;
        epic.setId(expectedId);
        String expected = gson.toJson(epic);

        URI url = URI.create(serverUrl + "/tasks/epic/?id=" + expectedId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicWithWrongId() throws IOException, InterruptedException {
        final int wrongId = 10;
        epic.setId(wrongId);

        URI uri = URI.create(serverUrl + "/tasks/epic");
        String json = gson.toJson(epic);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Ответ сервера не соответствует ожидаемому.");

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/epic/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера не соответствует ожидаемому.");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/epic");

        post(gson.toJson(epic), uri);
        post(gson.toJson(epic2), uri);
        post(gson.toJson(epic3), uri);

        epic.setId(1);
        epic2.setId(2);
        epic3.setId(3);

        List<Task> epics = List.of(epic, epic2, epic3);
        String expected = gson.toJson(epics);

        URI url = URI.create(serverUrl + "/tasks/epic/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Списки задач не совпадают.");
    }

    @Test
    void getEpicsWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI url = URI.create(serverUrl + "/tasks/epic/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void deleteEpics() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/epic");

        post(gson.toJson(epic), uri);
        post(gson.toJson(epic2), uri);
        post(gson.toJson(epic3), uri);

        delete(uri);

        String expected = "[]";

        URI url = URI.create(serverUrl + "/tasks/epic/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void deleteEpicsWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI uri = URI.create(serverUrl + "/tasks/epic/");
        delete(uri);

        HttpResponse<String> response = get(uri);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/epic");
        String json = gson.toJson(epic);
        post(json, uri);

        final int expectedId = 1;
        epic.setId(expectedId);
        String expected = gson.toJson(epic);

        URI url = URI.create(serverUrl + "/tasks/epic/?id=" + expectedId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void getEpicByIdWithEmptyList() throws IOException, InterruptedException {
        final int wrongId = 1;

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/epic/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера отличается от ожидаемого.");
    }

    @Test
    void getEpicByIdWithWrongId() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/epic");

        post(gson.toJson(epic), uri);
        post(gson.toJson(epic2), uri);
        post(gson.toJson(epic3), uri);

        final int wrongId = 100;

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/epic/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера отличается от ожидаемого.");
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        final URI uri = URI.create(serverUrl + "/tasks/epic");

        post(gson.toJson(epic), uri);
        post(gson.toJson(epic2), uri);
        post(gson.toJson(epic3), uri);

        int deletedId = 2;
        final URI url = URI.create(serverUrl + "/tasks/epic?id=" + deletedId);
        delete(url);

        epic.setId(1);
        epic2.setId(2);
        epic3.setId(3);

        List<Task> epics = List.of(epic, epic3);
        String expected = gson.toJson(epics);

        HttpResponse<String> responseForCheck = get(uri);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void deleteEpicByIdWithWrongId() throws IOException, InterruptedException {
        final URI uri = URI.create(serverUrl + "/tasks/epic");

        post(gson.toJson(epic), uri);
        post(gson.toJson(epic2), uri);
        post(gson.toJson(epic3), uri);

        int wrongId = 100;
        final URI url = URI.create(serverUrl + "/tasks/epic?id=" + wrongId);
        delete(url);

        epic.setId(1);
        epic2.setId(2);
        epic3.setId(3);

        List<Task> epics = List.of(epic, epic2, epic3);
        String expected = gson.toJson(epics);

        HttpResponse<String> responseForCheck = get(uri);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/epic");
        String json = gson.toJson(epic);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int id = 1;
        epic.setId(id);
        epic.setName("newEpic");
        epic.setDescription("newEpicDescription");
        String newTask = gson.toJson(epic);

        HttpResponse<String> newResponse = post(newTask, uri);
        int newStatus = newResponse.statusCode();
        assertEquals(200, newStatus, "Код ответа сервера не соответствует ожидаемому.");

        URI url = URI.create(serverUrl + "/tasks/epic/?id=" + id);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(newTask, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void updateEpicWithWrongId() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/epic");
        String json = gson.toJson(epic);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int wrongId = 100;
        epic.setId(wrongId);
        epic.setName("newTask");
        epic.setDescription("newTaskDescription");
        String newTask = gson.toJson(epic);

        HttpResponse<String> newResponse = post(newTask, uri);
        int newStatus = newResponse.statusCode();
        assertEquals(200, newStatus, "Код ответа сервера не соответствует ожидаемому.");

        URI url = URI.create(serverUrl + "/tasks/epic/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        String expected = "Задача с id = " + wrongId + " не найдена";
        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Возвращен не пустой массив.");
    }

    @Test
    void addNewSubtask() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(1);
        String json = gson.toJson(subtask1);
        HttpResponse<String> responseSubtask = post(json, uriSubtask);
        int statusSubtask = responseSubtask.statusCode();
        assertEquals(200, statusSubtask, "Код ответа сервера не соответствует ожидаемому.");

        int expectedId = 2;
        subtask1.setId(expectedId);
        String expected = gson.toJson(subtask1);

        URI url = URI.create(serverUrl + "/tasks/subtask/?id=" + expectedId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskWithWrongId() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        final int wrongId = 10;
        subtask1.setId(wrongId);
        subtask1.setEpicId(1);

        URI uri = URI.create(serverUrl + "/tasks/subtask");
        String json = gson.toJson(subtask1);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Ответ сервера не соответствует ожидаемому.");

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/subtask/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера не соответствует ожидаемому.");
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uri = URI.create(serverUrl + "/tasks/subtask");

        subtask1.setEpicId(1);
        subtask2.setEpicId(1);
        subtask3.setEpicId(1);

        post(gson.toJson(subtask1), uri);
        post(gson.toJson(subtask2), uri);
        post(gson.toJson(subtask3), uri);

        subtask1.setId(2);
        subtask2.setId(3);
        subtask3.setId(4);

        List<Subtask> tasks = List.of(subtask1, subtask2, subtask3);
        String expected = gson.toJson(tasks);

        URI url = URI.create(serverUrl + "/tasks/subtask/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Списки задач не совпадают.");
    }

    @Test
    void getSubtasksWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI url = URI.create(serverUrl + "/tasks/subtask/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void deleteSubtasks() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        subtask1.setEpicId(1);
        subtask2.setEpicId(1);
        subtask3.setEpicId(1);

        URI uri = URI.create(serverUrl + "/tasks/subtask");

        post(gson.toJson(subtask1), uri);
        post(gson.toJson(subtask2), uri);
        post(gson.toJson(subtask3), uri);

        delete(uri);

        String expected = "[]";

        URI url = URI.create(serverUrl + "/tasks/subtask/");
        HttpResponse<String> response = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void deleteSubtasksWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI uri = URI.create(serverUrl + "/tasks/subtask/");
        delete(uri);

        HttpResponse<String> response = get(uri);

        assertEquals(200, response.statusCode());
        assertEquals(expected, response.body(), "Возвращен не пустой массив.");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        subtask1.setEpicId(1);

        URI uri = URI.create(serverUrl + "/tasks/subtask");
        String json = gson.toJson(subtask1);
        post(json, uri);

        final int expectedId = 2;
        subtask1.setId(expectedId);
        String expected = gson.toJson(subtask1);

        URI url = URI.create(serverUrl + "/tasks/subtask/?id=" + expectedId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void getSubtaskByIdWithEmptyList() throws IOException, InterruptedException {
        final int wrongId = 1;

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/subtask/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера отличается от ожидаемого.");
    }

    @Test
    void getSubtaskByIdWithWrongId() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        subtask1.setEpicId(1);
        subtask2.setEpicId(1);
        subtask3.setEpicId(1);

        URI uri = URI.create(serverUrl + "/tasks/subtask");

        post(gson.toJson(subtask1), uri);
        post(gson.toJson(subtask2), uri);
        post(gson.toJson(subtask3), uri);

        final int wrongId = 100;

        String expected = "Задача с id = " + wrongId + " не найдена";

        URI url = URI.create(serverUrl + "/tasks/subtask/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Ответ сервера отличается от ожидаемого.");
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        final URI uri = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(1);
        subtask2.setEpicId(1);
        subtask3.setEpicId(1);

        post(gson.toJson(subtask1), uri);
        post(gson.toJson(subtask2), uri);
        post(gson.toJson(subtask3), uri);

        int deletedId = 3;
        final URI url = URI.create(serverUrl + "/tasks/subtask?id=" + deletedId);
        delete(url);

        subtask1.setId(2);
        subtask2.setId(3);
        subtask3.setId(4);

        List<Subtask> tasks = List.of(subtask1, subtask3);
        String expected = gson.toJson(tasks);

        HttpResponse<String> responseForCheck = get(uri);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void deleteSubtaskByIdWithWrongId() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        final URI uri = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(1);
        subtask2.setEpicId(1);
        subtask3.setEpicId(1);

        post(gson.toJson(subtask1), uri);
        post(gson.toJson(subtask2), uri);
        post(gson.toJson(subtask3), uri);

        int wrongId = 100;
        final URI url = URI.create(serverUrl + "/tasks/subtask?id=" + wrongId);
        delete(url);

        subtask1.setId(2);
        subtask2.setId(3);
        subtask3.setId(4);

        List<Subtask> tasks = List.of(subtask1, subtask2, subtask3);
        String expected = gson.toJson(tasks);

        HttpResponse<String> responseForCheck = get(uri);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uri = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(1);
        String json = gson.toJson(subtask1);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int id = 2;
        subtask1.setId(id);
        subtask1.setName("newTask");
        subtask1.setDescription("newTaskDescription");
        String newTask = gson.toJson(subtask1);

        HttpResponse<String> newResponse = post(newTask, uri);
        int newStatus = newResponse.statusCode();
        assertEquals(200, newStatus, "Код ответа сервера не соответствует ожидаемому.");

        URI url = URI.create(serverUrl + "/tasks/subtask/?id=" + id);
        HttpResponse<String> responseForCheck = get(url);

        assertEquals(200, response.statusCode());
        assertEquals(newTask, responseForCheck.body(), "Задачи не совпадают.");
    }

    @Test
    void updateSubaskWithWrongId() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uri = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(1);
        String json = gson.toJson(subtask1);
        HttpResponse<String> response = post(json, uri);

        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        int wrongId = 100;
        subtask1.setId(wrongId);
        subtask1.setName("newTask");
        subtask1.setDescription("newTaskDescription");
        String newTask = gson.toJson(subtask1);

        HttpResponse<String> newResponse = post(newTask, uri);
        int newStatus = newResponse.statusCode();
        assertEquals(200, newStatus, "Код ответа сервера не соответствует ожидаемому.");

        URI url = URI.create(serverUrl + "/tasks/task/?id=" + wrongId);
        HttpResponse<String> responseForCheck = get(url);

        String expected = "Задача с id = " + wrongId + " не найдена";
        assertEquals(404, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Возвращен не пустой массив.");
    }

    @Test
    void getPrioritizedTasksWithoutTime() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");
        String json = gson.toJson(task);
        HttpResponse<String> response = post(json, uri);
        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask1 = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(2);
        String jsonSubtask1 = gson.toJson(subtask1);
        HttpResponse<String> responseSubtask1 = post(jsonSubtask1, uriSubtask1);
        int statusSubtask1 = responseSubtask1.statusCode();
        assertEquals(200, statusSubtask1, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask2 = URI.create(serverUrl + "/tasks/subtask");
        subtask2.setEpicId(2);
        String jsonSubtask2 = gson.toJson(subtask2);
        HttpResponse<String> responseSubtask2 = post(jsonSubtask2, uriSubtask2);
        int statusSubtask2 = responseSubtask2.statusCode();
        assertEquals(200, statusSubtask2, "Код ответа сервера не соответствует ожидаемому.");

        task.setId(1);
        epic.setId(2);
        subtask1.setId(3);
        subtask2.setId(4);

        List<Task> expectedList = List.of(task, subtask1, subtask2);
        String expected = gson.toJson(expectedList);

        URI uriPrioritized = URI.create(serverUrl + "/tasks/");
        HttpResponse<String> responseForCheck = get(uriPrioritized);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");
        LocalDateTime taskStartTime = LocalDateTime.now();
        task.setStartTime(taskStartTime);
        String json = gson.toJson(task);
        HttpResponse<String> response = post(json, uri);
        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask1 = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(2);
        LocalDateTime subtask1StartTime = taskStartTime.plusHours(2);
        subtask1.setStartTime(subtask1StartTime);
        String jsonSubtask1 = gson.toJson(subtask1);
        HttpResponse<String> responseSubtask1 = post(jsonSubtask1, uriSubtask1);
        int statusSubtask1 = responseSubtask1.statusCode();
        assertEquals(200, statusSubtask1, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask2 = URI.create(serverUrl + "/tasks/subtask");
        subtask2.setEpicId(2);
        LocalDateTime subtask2StartTime = taskStartTime.plusHours(1);
        subtask2.setStartTime(subtask2StartTime);
        String jsonSubtask2 = gson.toJson(subtask2);
        HttpResponse<String> responseSubtask2 = post(jsonSubtask2, uriSubtask2);
        int statusSubtask2 = responseSubtask2.statusCode();
        assertEquals(200, statusSubtask2, "Код ответа сервера не соответствует ожидаемому.");

        task.setId(1);
        epic.setId(2);
        subtask1.setId(3);
        subtask2.setId(4);

        List<Task> expectedList = List.of(task, subtask2, subtask1);
        String expected = gson.toJson(expectedList);

        URI uriPrioritized = URI.create(serverUrl + "/tasks/");
        HttpResponse<String> responseForCheck = get(uriPrioritized);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void getPrioritizedTasksWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI uriPrioritized = URI.create(serverUrl + "/tasks/");
        HttpResponse<String> responseForCheck = get(uriPrioritized);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void getSubtasksByEpic() throws IOException, InterruptedException {
        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask1 = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(1);
        String jsonSubtask1 = gson.toJson(subtask1);
        HttpResponse<String> responseSubtask1 = post(jsonSubtask1, uriSubtask1);
        int statusSubtask1 = responseSubtask1.statusCode();
        assertEquals(200, statusSubtask1, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask2 = URI.create(serverUrl + "/tasks/subtask");
        subtask2.setEpicId(1);
        String jsonSubtask2 = gson.toJson(subtask2);
        HttpResponse<String> responseSubtask2 = post(jsonSubtask2, uriSubtask2);
        int statusSubtask2 = responseSubtask2.statusCode();
        assertEquals(200, statusSubtask2, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask3 = URI.create(serverUrl + "/tasks/subtask");
        subtask3.setEpicId(1);
        String jsonSubtask3 = gson.toJson(subtask3);
        HttpResponse<String> responseSubtask3 = post(jsonSubtask3, uriSubtask3);
        int statusSubtask3 = responseSubtask3.statusCode();
        assertEquals(200, statusSubtask3, "Код ответа сервера не соответствует ожидаемому.");

        subtask1.setId(2);
        subtask2.setId(3);
        subtask3.setId(4);

        List<Subtask> expectedSubtasks = List.of(subtask1, subtask2, subtask3);
        String expected = gson.toJson(expectedSubtasks);

        URI uri = URI.create(serverUrl + "/tasks/subtask/epic/?id=1");
        HttpResponse<String> responseForCheck = get(uri);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + "/tasks/task");
        String json = gson.toJson(task);
        HttpResponse<String> response = post(json, uri);
        int status = response.statusCode();
        assertEquals(200, status, "Код ответа сервера не соответствует ожидаемому.");

        URI uriEpic = URI.create(serverUrl + "/tasks/epic");
        String jsonEpic = gson.toJson(epic);
        HttpResponse<String> responseEpic = post(jsonEpic, uriEpic);
        int statusEpic = responseEpic.statusCode();
        assertEquals(200, statusEpic, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask1 = URI.create(serverUrl + "/tasks/subtask");
        subtask1.setEpicId(2);
        String jsonSubtask1 = gson.toJson(subtask1);
        HttpResponse<String> responseSubtask1 = post(jsonSubtask1, uriSubtask1);
        int statusSubtask1 = responseSubtask1.statusCode();
        assertEquals(200, statusSubtask1, "Код ответа сервера не соответствует ожидаемому.");

        URI uriSubtask2 = URI.create(serverUrl + "/tasks/subtask");
        subtask2.setEpicId(2);
        String jsonSubtask2 = gson.toJson(subtask2);
        HttpResponse<String> responseSubtask2 = post(jsonSubtask2, uriSubtask2);
        int statusSubtask2 = responseSubtask2.statusCode();
        assertEquals(200, statusSubtask2, "Код ответа сервера не соответствует ожидаемому.");

        task.setId(1);
        epic.setId(2);
        epic.addSubtask(3);
        epic.addSubtask(4);
        subtask1.setId(3);
        subtask2.setId(4);

        get(URI.create(serverUrl + "/tasks/subtask/?id=" + 4));
        get(URI.create(serverUrl + "/tasks/subtask/?id=" + 3));
        get(URI.create(serverUrl + "/tasks/task/?id=" + 1));
        get(URI.create(serverUrl + "/tasks/epic/?id=" + 2));

        List<Task> expectedHistory = List.of(epic, task, subtask1, subtask2);
        String expected = gson.toJson(expectedHistory);

        URI uriHistory = URI.create(serverUrl + "/tasks/history");
        HttpResponse<String> responseForCheck = get(uriHistory);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Списки задач не совпадают.");
    }

    @Test
    void getHistoryWithEmptyList() throws IOException, InterruptedException {
        String expected = "[]";

        URI uriHistory = URI.create(serverUrl + "/tasks/history");
        HttpResponse<String> responseForCheck = get(uriHistory);

        assertEquals(200, responseForCheck.statusCode());
        assertEquals(expected, responseForCheck.body(), "Возвращен не пустой массив.");
    }

    private HttpResponse<String> post(String json, URI uri) throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}