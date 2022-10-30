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

    public List<Epic> getAllEpics() {
        List<Epic> epics = new ArrayList<>(this.epics.values());
        return epics;
    }

    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtasks = new ArrayList<>(this.subtasks.values());
        return subtasks;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>(this.tasks.values());
        return tasks;
    }

    public void deleteAllEpics() {
        this.epics.clear();
        deleteAllSubtasks();
    }

    public void deleteAllSubtasks() {
        this.subtasks.clear();
        for (Epic epic : epics.values()) {
            List<Subtask> subtasksOfEpic = epic.getSubtasks();
            subtasksOfEpic.clear();
        }
    }

    public void deleteAllTasks() {
        this.tasks.clear();
    }

    public Epic getEpicById(Integer id) {
        return epics.getOrDefault(id, null);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.getOrDefault(id, null);
    }

    public Task getTaskById(Integer id) {
        return tasks.getOrDefault(id, null);
    }

    public void updateEpic(Epic epic) {
        List<Subtask> subtasksOfEpic = epic.getSubtasks();

        if (epic.getId() == null) {
            epic.setId(generateId());
        } else {
            Epic oldEpic = getEpicById(epic.getId());
            List<Subtask> subtasksOfOldEpic = oldEpic.getSubtasks();

            for (Subtask subtask : subtasksOfOldEpic) {
                if (!subtasksOfEpic.contains(subtask)) {
                    subtasks.remove(subtask.getId());
                }
            }
        }

        epics.put(epic.getId(), epic);

        for (Subtask subtask : subtasksOfEpic) {
            updateSubtask(subtask);
        }

        updateStatus(epic);
    }

    public void updateTask(Task task) {
        if (task.getId() == null) {
            task.setId(generateId());
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask.getId() == null) {
            subtask.setId(generateId());
        }
        subtasks.put(subtask.getId(), subtask);
        updateStatus(subtask.getEpic());
    }

    public void deleteEpicById(Integer id) {
        if (id == null || !epics.containsKey(id)) {
            return;
        }

        Epic epic = epics.get(id);
        List<Subtask> subtasksOfEpic = epic.getSubtasks();

        for (Subtask subtask : subtasksOfEpic) {
            subtasks.remove(subtask.getId());
        }

        epics.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        if (id == null || !subtasks.containsKey(id)) {
            return;
        }

        Subtask subtask = getSubtaskById(id);

        for (Epic epic : epics.values()) {
            epic.getSubtasks().remove(subtask);
        }

        subtasks.remove(id);
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    private void updateStatus(Epic epic) {
        List<Subtask> subtasksOfEpic = epic.getSubtasks();
        boolean statusIsDone = true;
        boolean statusIsNew = true;

        for (Subtask subtask : subtasksOfEpic) {
            if (!subtask.getStatus().equals(TaskStatus.DONE)) {
                statusIsDone = false;
            }

            if (!subtask.getStatus().equals(TaskStatus.NEW)) {
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
