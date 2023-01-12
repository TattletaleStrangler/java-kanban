package ru.yandex.practicum.tasktracker.managers.taskmanagers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}