package ru.yandex.practicum.tasktracker.web.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.managers.Managers;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.TaskManager;
import ru.yandex.practicum.tasktracker.tasks.Epic;
import ru.yandex.practicum.tasktracker.tasks.Subtask;
import ru.yandex.practicum.tasktracker.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private static final Gson gson = new Gson();
    private static TaskManager manager;

    public HttpTaskServer() throws IOException {
        manager = Managers.getDefault();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new PrioritizedTasksHandler());
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.start();
    }

    public void stop() {
        System.out.println("Останавливаем сервер на порту " + PORT);
        httpServer.stop(1);
    }

    static class PrioritizedTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            if (uri.getPath().split("/").length == 2 && "GET".equals(exchange.getRequestMethod())) {
                List<Task> tasks = manager.getPrioritizedTasks();
                String response = gson.toJson(tasks);
                writeResponse(exchange, response, 200);
            } else {
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Endpoint endpoint = getEndpoint(uri.getPath(), uri.getQuery(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_HISTORY: {
                    List<Task> tasks = manager.getHistory();
                    String response = gson.toJson(tasks);
                    writeResponse(exchange, response, 200);
                    break;
                }
                case UNKNOWN: {
                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }
    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Endpoint endpoint = getEndpoint(uri.getPath(), uri.getQuery(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_TASK_BY_ID: {
                    handleGetTaskById(exchange);
                    break;
                }
                case POST_TASK: {
                    handleSaveTask(exchange);
                    break;
                }
                case DELETE_TASKS: {
                    handleDeleteTasks(exchange);
                    break;
                }
                case DELETE_TASK_BY_ID: {
                    handleDeleteTaskById(exchange);
                    break;
                }
                case UNKNOWN: {
                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            List<Task> tasks = manager.getTasks();
            String response = gson.toJson(tasks);
            writeResponse(exchange, response, 200);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            Task task = manager.getTaskById(taskId);

            if (task == null) {
                writeResponse(exchange, "Задача с id = " + taskId + " не найдена", 404);
                return;
            }

            String response = gson.toJson(task);
            writeResponse(exchange, response, 200);
        }

        private void handleSaveTask(HttpExchange exchange) throws IOException {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                if (body.length() == 0) {
                    String response = "Задача не была сохранена, так как тело запроса пустое";
                    writeResponse(exchange, response, 400);
                    return;
                }

                Task task = gson.fromJson(body, Task.class);
                String response;

                if (task.getId() != null && task.getId() != 0) {
                    manager.updateTask(task);
                    response = "Задача с id=" + task.getId() + ", если она существовала, обновлена";
                } else {
                    manager.addNewTask(task);
                    response = "Задача добавлена, id=" + task.getId();
                }

                writeResponse(exchange, response, 200);
            } catch (JsonSyntaxException e) {
                String response = "Задача не была сохранена, так как тело запроса не соответствует формату json";
                writeResponse(exchange, response, 400);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            manager.deleteTasks();
            writeResponse(exchange, "Задачи удалены", 200);
        }

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            manager.deleteTaskById(taskId);
            String response = "Задача с id = " + taskId + " удалена";
            writeResponse(exchange, response, 200);
        }
    }

    static class SubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Endpoint endpoint = getEndpoint(uri.getPath(), uri.getQuery(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_TASK_BY_ID: {
                    handleGetTaskById(exchange);
                    break;
                }
                case POST_TASK: {
                    handleSaveTask(exchange);
                    break;
                }
                case DELETE_TASKS: {
                    handleDeleteTasks(exchange);
                    break;
                }
                case DELETE_TASK_BY_ID: {
                    handleDeleteTaskById(exchange);
                    break;
                }
                case GET_SUBTASK_BY_EPIC_ID: {
                    handleGetSubtaskByEpicId(exchange);
                    break;
                }
                case UNKNOWN: {
                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            List<Subtask> tasks = manager.getSubtasks();
            String response = gson.toJson(tasks);
            writeResponse(exchange, response, 200);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            Subtask task = manager.getSubtaskById(taskId);

            if (task == null) {
                writeResponse(exchange, "Задача с id = " + taskId + " не найдена", 404);
                return;
            }

            String response = gson.toJson(task);
            writeResponse(exchange, response, 200);
        }

        private void handleSaveTask(HttpExchange exchange) throws IOException {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                if (body.length() == 0) {
                    String response = "Задача не была сохранена, так как тело запроса пустое";
                    writeResponse(exchange, response, 400);
                    return;
                }

                Subtask task = gson.fromJson(body, Subtask.class);
                String response;

                if (task.getId() != null && task.getId() != 0) {
                    manager.updateSubtask(task);
                    response = "Задача с id = " + task.getId() + " обновлена";
                } else {
                    Integer taskId = manager.addNewSubtask(task);
                    response = "Задача добавлена, id = " + taskId;
                }

                writeResponse(exchange, response, 200);
            } catch (JsonSyntaxException e) {
                String response = "Задача не была сохранена, так как тело запроса не соответствует формату json";
                writeResponse(exchange, response, 400);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            manager.deleteSubtasks();
            writeResponse(exchange, "Задачи удалены", 200);
        }

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            manager.deleteSubtaskById(taskId);
            String response = "Задача с id = " + taskId + " удалена";
            writeResponse(exchange, response, 200);
        }

        private void handleGetSubtaskByEpicId(HttpExchange exchange) throws IOException {
            Optional<Integer> epicIdOpt = getTaskId(exchange);

            if (epicIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }

            int epicId = epicIdOpt.get();
            Epic epic = manager.getEpicById(epicId);

            if (epic == null) {
                writeResponse(exchange, "Эпик с id = " + epicId + " не найден", 404);
                return;
            }

            List<Subtask> subtasks = manager.getSubtasksByEpic(epic);
            String response = gson.toJson(subtasks);
            writeResponse(exchange, response, 200);
        }
    }

    static class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Endpoint endpoint = getEndpoint(uri.getPath(), uri.getQuery(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_TASK_BY_ID: {
                    handleGetTaskById(exchange);
                    break;
                }
                case POST_TASK: {
                    handleSaveTask(exchange);
                    break;
                }
                case DELETE_TASKS: {
                    handleDeleteTasks(exchange);
                    break;
                }
                case DELETE_TASK_BY_ID: {
                    handleDeleteTaskById(exchange);
                    break;
                }
                case UNKNOWN: {
                }
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            List<Epic> tasks = manager.getEpics();
            String response = gson.toJson(tasks);
            writeResponse(exchange, response, 200);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            Epic task = manager.getEpicById(taskId);

            if (task == null) {
                writeResponse(exchange, "Задача с id = " + taskId + " не найдена", 404);
                return;
            }

            String response = gson.toJson(task);
            writeResponse(exchange, response, 200);
        }

        private void handleSaveTask(HttpExchange exchange) throws IOException {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                if (body.length() == 0) {
                    String response = "Задача не была сохранена, так как тело запроса пустое";
                    writeResponse(exchange, response, 400);
                    return;
                }

                Epic task = gson.fromJson(body, Epic.class);
                String response;

                if (task.getId() != null && task.getId() != 0) {
                    manager.updateEpic(task);
                    response = "Задача с id = " + task.getId() + " обновлена";
                } else {
                    Integer taskId = manager.addNewEpic(task);
                    response = "Задача добавлена, id = " + taskId;
                }

                writeResponse(exchange, response, 200);
            } catch (JsonSyntaxException e) {
                String response = "Задача не была сохранена, так как тело запроса не соответствует формату json";
                writeResponse(exchange, response, 400);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            manager.deleteEpics();
            writeResponse(exchange, "Задачи удалены", 200);
        }

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            manager.deleteEpicById(taskId);
            String response = "Задача с id = " + taskId + " удалена";
            writeResponse(exchange, response, 200);
        }
    }

    private static void writeResponse(HttpExchange exchange,
                                      String responseString,
                                      int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private static Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] queryParts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(queryParts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private static Endpoint getEndpoint(String requestPath, String requestQuery, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 4 && "subtask".equals(pathParts[2]) && "epic".equals(pathParts[3])
                && requestQuery != null) {
            return Endpoint.GET_SUBTASK_BY_EPIC_ID;
        }

        if (pathParts.length == 3) {
            if (!"history".equals(pathParts[2])) {
                if (requestQuery == null) {
                    if ("GET".equals(requestMethod)) {
                        return Endpoint.GET_TASKS;
                    }
                    if ("POST".equals(requestMethod)) {
                        return Endpoint.POST_TASK;
                    }
                    if ("DELETE".equals(requestMethod)) {
                        return Endpoint.DELETE_TASKS;
                    }
                } else {
                    if ("GET".equals(requestMethod)) {
                        return Endpoint.GET_TASK_BY_ID;
                    }
                    if ("DELETE".equals(requestMethod)) {
                        return Endpoint.DELETE_TASK_BY_ID;
                    }
                }
            } else {
                if ("GET".equals(requestMethod) && requestQuery == null) {
                    return Endpoint.GET_HISTORY;
                }
            }
        }

        return Endpoint.UNKNOWN;
    }

    private enum Endpoint {
        GET_TASKS,
        GET_TASK_BY_ID,
        POST_TASK,
        DELETE_TASKS,
        DELETE_TASK_BY_ID,
        GET_HISTORY,
        GET_SUBTASK_BY_EPIC_ID,
        UNKNOWN
    }
}
