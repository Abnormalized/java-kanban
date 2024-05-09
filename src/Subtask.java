public class Subtask extends Epic{
    Epic epicOfThisSubtask;

    public Subtask(String name, String description, Epic epicOfThisSubtask) {
        super(name, description);
        this.epicOfThisSubtask = epicOfThisSubtask;
    }

    public void setStatus(Status status) {
        super.setStatus(status);
        try {
            epicOfThisSubtask.updateEpicStatus();
        } catch (NullPointerException e) {

        }
    }


}
