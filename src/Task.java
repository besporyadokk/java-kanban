import java.util.*;

public class Task {
    protected String name;
    protected int id;
    protected String description;
    protected TaskStatus status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }


    public void setStatus(TaskStatus status) {
        this.status = status;

    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name = '" + name + '\'' +
                ", description =  '" + description + '\'' +
                ", status = '" + status + '\'' +
                ", id = '" + id + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
