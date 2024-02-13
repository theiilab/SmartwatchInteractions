package yuanren.tvsamrtwatch.smartwatchinteractions.log;

import androidx.annotation.NonNull;

public class Task {
    private final static String TAG = "Task";
    // common
    public int pid;
    public int sid;
    public String method;
    public int dataSet;
    public int bid;

    public int id;
    public String name;
    public String targetMovie;
    public int movieLength;
    public String selectedMovie;
    public Long startTime = 0L;
    public Long endTime = 0L;
    public Long taskCompletionTime = 0L;
    public int actionsPerTask = 0;
    public int actionUpsPerTask = 0;
    public int actionsNeeded = 0;
    public double errorRate = 0;
    // smartwatch only
    public int swipesPerTasks = 0;
    public int swipesNeeded = 0;
    public int swipeHoldsPerTasks = 0;
    public int swipeHoldNeeded = 0;
    public int tapsPerTasks = 0;
    public int tapsNeeded = 0;
    public int longPressesPerTasks = 0;
    public int longPressesNeeded = 0;
    public int twoFingerTapsPerTasks = 0;
    public int twoFingerTapsNeeded = 0;
    public int crownRotatesPerTasks = 0;
    public int crownRotatesNeeded = 0;

    // session 3
    public int positionOnSelect = 0;
    public double characterPerSecond = 0;
    public int backspaceCount = 0;
    public Long timePerCharacter = 0L;
    public int totalCharacterEntered = 0;
    public int incorrectTitleCount = 0;

    public Task(int pid, int sid, String method, int dataSet, int bid, int tid, String name, String targetMovie, int movieLength) {
        this.pid = pid;
        this.sid = sid;
        this.method = method;
        this.dataSet = dataSet;
        this.bid = bid;
        this.id = tid;
        this.name = name;
        this.targetMovie = targetMovie;
        this.movieLength = movieLength;
    }

    @NonNull
    @Override
    public String toString() {
        String res;
        if (sid == 1) {
            taskCompletionTime = endTime - startTime;
            errorRate = actionsNeeded != 0 ? ((double) actionsPerTask - (double) actionsNeeded) / actionsNeeded : 0;
            res = "" + pid + "," + method + "," + sid + "," + dataSet + "," + bid + "," + targetMovie + "," + movieLength + "," + selectedMovie + "," + id + "," + name + "," + taskCompletionTime + "," + startTime + "," + endTime + "," + actionsPerTask + "," + actionsNeeded + "," + errorRate + "," + 0 + "," + swipesPerTasks + "," + swipesNeeded + "," + swipeHoldsPerTasks + "," + swipeHoldNeeded + "," + tapsPerTasks + "," + tapsNeeded + "," + longPressesPerTasks + "," + longPressesNeeded + "," + twoFingerTapsPerTasks + "," + twoFingerTapsNeeded + "," + crownRotatesPerTasks + "," + crownRotatesNeeded + "\n";
        } else if (sid == 2) {
            taskCompletionTime = endTime - startTime;
            errorRate = actionsNeeded != 0 ? ((double) actionsPerTask - (double) actionsNeeded) / actionsNeeded : 0;
            res = "" + pid + "," + method + "," + sid + "," + dataSet + "," + bid + "," + targetMovie + "," + movieLength + "," + selectedMovie + "," + id + "," + name + "," + taskCompletionTime + "," + startTime + "," + endTime + "," + actionsPerTask + "," + actionsNeeded + "," + errorRate + "," + 0 + "," + swipesPerTasks + "," + swipesNeeded + "," + swipeHoldsPerTasks + "," + swipeHoldNeeded + "," + tapsPerTasks + "," + tapsNeeded + "," + longPressesPerTasks + "," + longPressesNeeded + "," + twoFingerTapsPerTasks + "," + twoFingerTapsNeeded + "," + crownRotatesPerTasks + "," + crownRotatesNeeded + "\n";
        } else { // session 3
            taskCompletionTime = endTime - startTime;
            characterPerSecond = (double) totalCharacterEntered / (taskCompletionTime / 1000);
            timePerCharacter = totalCharacterEntered != 0 ? taskCompletionTime / totalCharacterEntered : 0;
            errorRate = incorrectTitleCount != 0 ? 1.0 / incorrectTitleCount : 0;
            res = "" + pid + "," + method + "," + sid + "," + dataSet + "," + bid + "," + targetMovie + "," + movieLength + "," + selectedMovie + "," + id + "," + name + "," + taskCompletionTime + "," + startTime + "," + endTime + "," + actionsPerTask + "," + errorRate + "," + positionOnSelect + "," + characterPerSecond + "," + backspaceCount + "," + timePerCharacter + "," + totalCharacterEntered + "\n";
        }
        return res;
    }
}
