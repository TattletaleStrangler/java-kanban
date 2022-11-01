import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private Integer nextId;
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;

    public Manager() {
        nextId = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteEpics() {
        epics.clear();
        deleteSubtasks();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            List<Integer> subtasksOfEpic = epic.getSubtasksId();
            subtasksOfEpic.clear();
        }
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Integer addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public Integer addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        synchronizeEpic(epic.getId());
        return epic.getId();
    }

    public Integer addNewSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        return subtask.getId();
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        synchronizeEpic(epic.getId());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        synchronizeEpic(subtask.getEpicId());
    }

    public void deleteEpicById(Integer id) {
        Epic epic = epics.get(id);
        List<Integer> subtasksOfEpic = epic.getSubtasksId();

        for (Integer subtask : subtasksOfEpic) {
            subtasks.remove(subtask);
        }

        epics.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        Subtask subtask = getSubtaskById(id);

        for (Epic epic : epics.values()) {
            epic.getSubtasksId().remove(subtask.getId());
        }

        subtasks.remove(id);
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtasksId()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
        }

        return subtasksOfEpic;
    }

    private void synchronizeEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtasksOfEpic = epic.getSubtasksId();
        boolean statusIsDone = true;
        boolean statusIsNew = true;

        for (Integer subtaskId : subtasksOfEpic) {
            subtasks.get(subtaskId).setEpicId(epic.getId());

            if (!subtasks.get(subtaskId).getStatus().equals(TaskStatus.DONE)) {
                statusIsDone = false;
            }

            if (!subtasks.get(subtaskId).getStatus().equals(TaskStatus.NEW)) {
                statusIsNew = false;
            }
        }

        if (statusIsDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (statusIsNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private Integer generateId() {
        return nextId++;
    }
}
