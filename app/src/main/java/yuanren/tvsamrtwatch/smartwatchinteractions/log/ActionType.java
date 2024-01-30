package yuanren.tvsamrtwatch.smartwatchinteractions.log;

public enum ActionType {
    TYPE_ACTION_UNKNOWN("Unknown"),
    TYPE_ACTION_SWIPE_LEFT("Swipe Left"),
    TYPE_ACTION_SWIPE_RIGHT("Swipe Right"),
    TYPE_ACTION_SWIPE_UP("Swipe Up"),
    TYPE_ACTION_SWIPE_DOWN("Swipe Down"),
    TYPE_ACTION_SWIPE_LEFT_HOLD("Swipe Left Hold"),
    TYPE_ACTION_SWIPE_RIGHT_HOLD("Swipe Right Hold"),
    TYPE_ACTION_TAP("Tap"),
    TYPE_ACTION_TWO_FINGER_TAP("Two-Finger Tap"),
    TYPE_ACTION_LONG_PRESS("Long Press"),
    TYPE_ACTION_CROWN_ROTATE("Crown Rotate");
    public final String name;

    private ActionType(String name) {
        this.name = name;
    }

}
