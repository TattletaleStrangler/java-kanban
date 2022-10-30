public class Main {

    public static void main(String[] args) {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзадача 1 Эпика 1", "Описание подзадачи 1 эпика 1", epic1);
        Subtask subtask2 = new Subtask("Подзадача 2 Эпика 1", "Описание подзадачи 2 эпика 1", epic1);
        epic1.addSubtask(subtask1);
        epic1.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask3 = new Subtask("Подзадача 1 Эпика 2", "Описание подзадачи 1 эпика 2", epic2);
        epic2.addSubtask(subtask3);

        Manager manager = new Manager();
        manager.updateEpic(epic1);
        manager.updateEpic(epic2);

        System.out.println("Печать эпиков до изменений:");
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println();

        System.out.println("Изменен статус подзадачи с id=5 эпика c id=4 на DONE:");
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);
        System.out.println(epic2);
        System.out.println();

        System.out.println("Изменен статус подзадачи с id=3 эпика c id=1 на IN_PROGRESS:");
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask2);
        System.out.println(epic1);
        System.out.println();

        System.out.println("Изменен статус подзадач с id=2, id=3 эпика c id=1 на DONE:");
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        System.out.println(epic1);
        System.out.println();

        System.out.println("Удалена подзадача c id=2 эпика с id=1:");
        manager.deleteSubtaskById(2);
        System.out.println(epic1);
        System.out.println();

        System.out.println("Удален эпик с id=4, печатаем его и его подзадачу с id=5:");
        manager.deleteEpicById(4);
        epic2 = manager.getEpicById(4);
        System.out.println(epic2);
        subtask3 = manager.getSubtaskById(5);
        System.out.println(subtask3);
    }
}
