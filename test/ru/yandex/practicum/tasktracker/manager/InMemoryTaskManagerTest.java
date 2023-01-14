package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.tasktracker.managers.taskmanagers.InMemoryTaskManager;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}