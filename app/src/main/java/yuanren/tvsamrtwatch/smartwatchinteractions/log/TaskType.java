package yuanren.tvsamrtwatch.smartwatchinteractions.log;

public enum TaskType {
    TYPE_TASK_FIND("Find Titles"),
    TYPE_TASK_PLAY_5_SEC ("Play For 5 Sec"),
    TYPE_TASK_CHANGE_VOLUME("Change Volume By 2 Units"),
    TYPE_TASK_FORWARD("Forward By 10 Sec"),
    TYPE_TASK_PAUSE("Pause"),
    TYPE_TASK_BACKWARD("Backward By 10 Sec"),
    TYPE_TASK_GO_TO_END("Go To The End Of The Title"),
    TYPE_TASK_GO_TO_START("Go To The Start Of The Title");

    private final String name;

    private TaskType(String name) {
        this.name = name;
    }

}
