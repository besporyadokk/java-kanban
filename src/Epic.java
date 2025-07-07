import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtasksIds;


    public Epic(String name,String description){
        super(name,description);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds(){
        return subtasksIds;
    }

    public void addSubtaskId(int subtaskId){
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId){
        subtasksIds.remove(subtaskId);
    }


    @Override
    public String toString(){
        return "Epic{" +
                "name = '" + getName() + '\''+
                ", description = " + getDescription() + '\'' +
                ", status = " + getStatus() + '\'' +
                ", id = '" + getId() +
                "', subtaskIds = '" + getSubtasksIds() +
                '}';
    }

}
