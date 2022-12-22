# java-kanban  
---  
**Трекер задач**  
  
*На данный момент реализовано следующее:*  
1. Созданы классы задач Task, Epic, Subtask
2. Создан класс InMemoryTaskManager, который реализует интерфейс TaskManager и умеет:
    * Хранить все задачи в оперативной памяти
    * Выдавать список всех задач
    * Выдавать задачу по ID
    * Сохранять и обновлять задачу
    * Удалять задачу по ID
    * Выдавать список всех подзадач эпика
    * Обновлять статус эпика в зависимости от статуса подзадач
    * Возвращать список последних просмотренных задач
3. Создан класс InMemoryHistoryManager, который реализует интерфейс HistoryManager и умеет:
    * Хранить список просмотренных задач (Task, Subtask, Epic) в оперативной памяти без повторений
    * Добавлять в список очередную задачу
    * Возвращать список просмотренных задач
    * Удалять из истории просмотренную задачу
4. Создан класс FileBackedTaskManager, который наследует класс InMemoryTaskManager и умеет все то же, плюс:
    * Сохранять существующие задачи и историю в файл
    * Восстанавливать свое состояние (задачи, история) из файла
5. Создан утилитарный класс Manager, который умеет:
    * Создавать и возвращать FileBackedTaskManager с помощью метода getDefault()
    * Создавать и возвращать InMemoryHistoryManager с помощью метода getDefaultHistory()
6. В метод main добавлен код для проверки работы программы

