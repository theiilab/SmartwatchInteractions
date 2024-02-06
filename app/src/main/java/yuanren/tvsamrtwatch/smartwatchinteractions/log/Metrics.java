package yuanren.tvsamrtwatch.smartwatchinteractions.log;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;

public class Metrics extends Application {
    public final static int SESSION_1_NUM_TASK = 8;  // change session1_tasks accordingly
    public final static int SESSION_2_NUM_TASK = 7;
    public final static int SESSION_3_NUM_BLOCK = 3;
    public final static int SESSION_3_NUM_TASK = 10;  // change session3_targetMovies accordingly
    public int pid = 0;
    public String method = "";
    public int session = 0;
    public int dataSet = 0;
    public int block = 1;
    public String targetMovie = "";
    public int movieLength = 0;
    public String selectedMovie = "";
    public int taskNum = 1;
    public String task = "";
    public Long taskCompletionTime = 0L;
    public Long startTime = 0L;
    public Long endTime = 0L;
    public int actionsPerTask = 0;
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
    public double characterPerSecond = 0;
    public int backspaceCount = 0;
    public Long timePerCharacter = 0L;
    public int totalCharacterEntered = 0;

    public int incorrectTitleCount = 0;

    public List<Action> actions = new ArrayList<>();

    private String[] session1_targetMovies = {
            "The King's Man",
            "Jumanji",
            "The Devil Wears Prada",
            "Venom",
            "Harry Potter and the Prisoner of Azkaban",
            "Insomnia",
            "Mama Mia",
            "Sherlock Holmes",
            "Flipped",
            "Inception",
            "Space Jam",
            "Death on the Nile"};

    private String[] session1_targetMovies2 = {
            "Red Notice",
            "Uncharted",
            "The Wolf of Wall Street",
            "Iron man",
            "Fantastic Beasts and Where to Find Them",
            "Fall",
            "Lala Land",
            "The Da Vinci Code",
            "Crazy Rich Asians",
            "The Adam Project",
            "Million Dollar Baby",
            "Pain Hustler"};

    private String[] session1_tasks = {
            TaskType.TYPE_TASK_FIND.name,
            TaskType.TYPE_TASK_PLAY_5_SEC.name,
            TaskType.TYPE_TASK_CHANGE_VOLUME.name,
            TaskType.TYPE_TASK_FORWARD.name,
            TaskType.TYPE_TASK_PAUSE.name,
            TaskType.TYPE_TASK_BACKWARD.name,
            TaskType.TYPE_TASK_GO_TO_END.name,
            TaskType.TYPE_TASK_GO_TO_START.name
    };

    private Map<String, Integer> session1_actionsNeeded = new HashMap<String, Integer>() {{
        put(TaskType.TYPE_TASK_PLAY_5_SEC.name, 0);
        put(TaskType.TYPE_TASK_CHANGE_VOLUME.name, 2);
        put(TaskType.TYPE_TASK_FORWARD.name, 2);
        put(TaskType.TYPE_TASK_PAUSE.name, 2);
        put(TaskType.TYPE_TASK_BACKWARD.name, 2);
        put(TaskType.TYPE_TASK_GO_TO_END.name, 1);
        put(TaskType.TYPE_TASK_GO_TO_START.name, 1);
    }};
    private Map<String, Integer> session1_swipesNeeded = new HashMap<String, Integer>() {{
        put(TaskType.TYPE_TASK_PLAY_5_SEC.name, 0);
        put(TaskType.TYPE_TASK_CHANGE_VOLUME.name, 0);
        put(TaskType.TYPE_TASK_FORWARD.name, 2);
        put(TaskType.TYPE_TASK_PAUSE.name, 0);
        put(TaskType.TYPE_TASK_BACKWARD.name, 2);
        put(TaskType.TYPE_TASK_GO_TO_END.name, 0);
        put(TaskType.TYPE_TASK_GO_TO_START.name, 0);
    }};
    private Map<String, Integer> session1_swipeHoldNeeded = new HashMap<String, Integer>() {{
        put(TaskType.TYPE_TASK_PLAY_5_SEC.name, 0);
        put(TaskType.TYPE_TASK_CHANGE_VOLUME.name, 0);
        put(TaskType.TYPE_TASK_FORWARD.name, 0);
        put(TaskType.TYPE_TASK_PAUSE.name, 0);
        put(TaskType.TYPE_TASK_BACKWARD.name, 0);
        put(TaskType.TYPE_TASK_GO_TO_END.name, 1);
        put(TaskType.TYPE_TASK_GO_TO_START.name, 1);
    }};
    private Map<String, Integer> session1_tapsNeeded = new HashMap<String, Integer>() {{
        put(TaskType.TYPE_TASK_PLAY_5_SEC.name, 0);
        put(TaskType.TYPE_TASK_CHANGE_VOLUME.name, 0);
        put(TaskType.TYPE_TASK_FORWARD.name, 0);
        put(TaskType.TYPE_TASK_PAUSE.name, 2);
        put(TaskType.TYPE_TASK_BACKWARD.name, 0);
        put(TaskType.TYPE_TASK_GO_TO_END.name, 0);
        put(TaskType.TYPE_TASK_GO_TO_START.name, 0);
    }};
    private Map<String, Integer> session1_crownRotatesNeeded = new HashMap<String, Integer>() {{
        put(TaskType.TYPE_TASK_PLAY_5_SEC.name, 0);
        put(TaskType.TYPE_TASK_CHANGE_VOLUME.name, 2);
        put(TaskType.TYPE_TASK_FORWARD.name, 0);
        put(TaskType.TYPE_TASK_PAUSE.name, 0);
        put(TaskType.TYPE_TASK_BACKWARD.name, 0);
        put(TaskType.TYPE_TASK_GO_TO_END.name, 0);
        put(TaskType.TYPE_TASK_GO_TO_START.name, 0);
    }};

    private String[] session2_targetMovies = {
            "The King's Man",
            "Jumanji",
            "The Devil Wears Prada",
            "Venom",
            "Harry Potter and the Prisoner of Azkaban",
            "Insomnia",
            "Mama Mia",
            "Sherlock Holmes"};

    private String[] session2_targetMovies2 = {
            "Red Notice",
            "Uncharted",
            "The Wolf of Wall Street",
            "Iron man",
            "Fantastic Beasts and Where to Find Them",
            "Fall",
            "Lala Land",
            "The Da Vinci Code"};

    private String[] session3_targetMovies = {
            "The King's Man",
            "Jumanji",
            "The Devil Wears Prada",
            "Venom",
            "Harry Potter and the Prisoner of Azkaban",
            "Insomnia",
            "Mama Mia",
            "Sherlock Holmes",
            "Flipped",
            "Inception"};
    private String[] session3_targetMovies2 = {
            "Red Notice",
            "Uncharted",
            "The Wolf of Wall Street",
            "Iron man",
            "Fantastic Beasts and Where to Find Them",
            "Fall",
            "Lala Land",
            "The Da Vinci Code",
            "Crazy Rich Asians",
            "The Adam Project"};

    @NonNull
    @Override
    public String toString() {
        String res;
        if (session == 1) {
            taskCompletionTime = endTime - startTime;
            errorRate = actionsNeeded != 0 ? ((double) actionsPerTask - (double) actionsNeeded) / actionsNeeded : 0;
            res = "" + pid + "," + method + "," + session + "," + dataSet + "," + block + "," + targetMovie + "," + movieLength + "," + selectedMovie + "," + taskNum + "," + task + "," + taskCompletionTime + "," + startTime + "," + endTime + "," + actionsPerTask + "," + actionsNeeded + "," + errorRate + "," + swipesPerTasks + "," + swipesNeeded + "," + swipeHoldsPerTasks + "," + swipeHoldNeeded + "," + tapsPerTasks + "," + tapsNeeded + "," + longPressesPerTasks + "," + longPressesNeeded + "," + twoFingerTapsPerTasks + "," + twoFingerTapsNeeded + "," + crownRotatesPerTasks + "," + crownRotatesNeeded + "\n";
        } else if (session == 2) {
            taskCompletionTime = endTime - startTime;
            errorRate = actionsNeeded != 0 ? ((double) actionsPerTask - (double) actionsNeeded) / actionsNeeded : 0;
            res = "" + pid + "," + method + "," + session + "," + dataSet + "," + block + "," + targetMovie + "," + movieLength + "," + selectedMovie + "," + taskNum + "," + task + "," + taskCompletionTime + "," + startTime + "," + endTime + "," + actionsPerTask + "," + actionsNeeded + "," + errorRate + "," + swipesPerTasks + "," + swipesNeeded + "," + swipeHoldsPerTasks + "," + swipeHoldNeeded + "," + tapsPerTasks + "," + tapsNeeded + "," + longPressesPerTasks + "," + longPressesNeeded + "," + twoFingerTapsPerTasks + "," + twoFingerTapsNeeded + "," + crownRotatesPerTasks + "," + crownRotatesNeeded + "\n";
        } else { // session 3
            taskCompletionTime = endTime - startTime;
            errorRate = incorrectTitleCount != 0 ? 1 / incorrectTitleCount : 0;
            characterPerSecond = (double) totalCharacterEntered / (taskCompletionTime / 1000);
            timePerCharacter = taskCompletionTime / totalCharacterEntered;
            res = "" + pid + "," + method + "," + session + "," + dataSet + "," + block + "," + targetMovie + "," + movieLength + "," + selectedMovie + "," + taskNum + "," + task + "," + taskCompletionTime + "," + startTime + "," + endTime + "," + actionsPerTask + "," + errorRate + "," + characterPerSecond + "," + backspaceCount + "," + timePerCharacter + "," + totalCharacterEntered + "\n";
        }
        return res;
    }

    public void init(int pid, int session, String method, int dataSet) {
        this.pid = pid;
        this.session = session;
        this.method = method;
        this.dataSet = dataSet;
        this.block = 1;
        this.taskNum = 1;
        if (session == 1) {
            // for task 1
            this.targetMovie = dataSet == 0 ? session1_targetMovies[0] : session1_targetMovies2[0];
            this.task = session1_tasks[0];
            this.actionsNeeded = calculateS1T1ActionsNeeded();
            this.swipesNeeded = actionsNeeded - 1;
            this.tapsNeeded = 1;
        } else if (session == 2) {
            // for task 1
            this.targetMovie = dataSet == 0 ? session2_targetMovies[0] : session2_targetMovies2[0];
            this.task = "Question 1";
            this.actionsNeeded = 3;
            this.twoFingerTapsNeeded = 1;
            this.tapsNeeded = 1;
            this.longPressesNeeded = 1;
        } else {
            this.targetMovie = dataSet == 0 ? session3_targetMovies[0] : session3_targetMovies2[0];
            this.task = "Search 1";
        }
        this.movieLength = MovieList.getMovie(targetMovie).getLength();
    }

    public void nextBlock() {
        if (session == 1) { // 1
            block = block > session1_targetMovies.length ? block : block + 1;
            targetMovie = dataSet == 0 ? session1_targetMovies[Math.min(session1_targetMovies.length - 1, block - 1)] : session1_targetMovies2[Math.min(session1_targetMovies.length - 1, block - 1)];
            task = session1_tasks[0];
            actionsNeeded = calculateS1T1ActionsNeeded();
            swipesNeeded = actionsNeeded - 1;
            tapsNeeded = 1;
        } else if (session == 2) { // 2
            block = block > session2_targetMovies.length ? block : block + 1;
            targetMovie = dataSet == 0 ? session2_targetMovies[Math.min(session2_targetMovies.length - 1, block - 1)] : session2_targetMovies2[Math.min(session2_targetMovies.length - 1, block - 1)];
            task = "Question 1";
            actionsNeeded = 3;
            twoFingerTapsNeeded = 1;
            tapsNeeded = 1;
            longPressesNeeded = 1;
        } else { // 3
            block = block + 1 > 3 ? block : block + 1;
            targetMovie = dataSet == 0 ? session3_targetMovies[0] : session3_targetMovies2[0];
            task = "Search 1";
        }
        movieLength = MovieList.getMovie(targetMovie).getLength();
        selectedMovie = "";
        taskNum = 1;
        taskCompletionTime = 0L;
        startTime = 0L;
        endTime = 0L;
        actionsPerTask = 0;
        errorRate = 0;
        // smartwatch only
        swipesPerTasks = 0;
        swipesNeeded = 0;
        swipeHoldsPerTasks = 0;
        swipeHoldNeeded = 0;
        tapsPerTasks = 0;
        tapsNeeded = 0;
        longPressesPerTasks = 0;
        longPressesNeeded = 0;
        twoFingerTapsPerTasks = 0;
        twoFingerTapsNeeded = 0;
        crownRotatesPerTasks = 0;
        crownRotatesNeeded = 0;
    }

    public void nextTask() {
        if (session == 3) {
            taskNum = Math.min(session3_targetMovies.length, taskNum + 1);
            task = "Search " + taskNum;
            targetMovie = dataSet == 0 ? session3_targetMovies[Math.max(0, taskNum - 1)]: session3_targetMovies2[Math.max(0, taskNum - 1)];
            selectedMovie = "";
            taskCompletionTime = 0L;
            startTime = 0L;
            endTime = 0L;
            actionsPerTask = 0;
            errorRate = 0;
            characterPerSecond = 0;
            backspaceCount = 0;
            timePerCharacter = 0L;
            totalCharacterEntered = 0;
            incorrectTitleCount = 0;
        } else if (session == 2) {
            taskNum = Math.min(SESSION_2_NUM_TASK, taskNum + 1);
            task = "Question " + taskNum;
            actionsNeeded = 3;
            swipesNeeded = 1;
            tapsNeeded = 1;
            longPressesNeeded = 1;
            swipeHoldNeeded = 0;
            twoFingerTapsNeeded = 0;
            crownRotatesNeeded = 0;

            taskCompletionTime = 0L;
            startTime = 0L;
            endTime = 0L;
            actionsPerTask = 0;
            errorRate = 0;
            // smartwatch only
            swipesPerTasks = 0;
            swipeHoldsPerTasks = 0;
            tapsPerTasks = 0;
            longPressesPerTasks = 0;
            twoFingerTapsPerTasks = 0;
            crownRotatesPerTasks = 0;
        } else {  // session 1
            taskNum = Math.min(SESSION_1_NUM_TASK, taskNum + 1);
            task = session1_tasks[Math.max(0, taskNum - 1)];
            actionsNeeded = session1_actionsNeeded.get(task);
            swipesNeeded = session1_swipesNeeded.get(task);
            swipeHoldNeeded = session1_swipeHoldNeeded.get(task);
            tapsNeeded = session1_tapsNeeded.get(task);
            crownRotatesNeeded = session1_crownRotatesNeeded.get(task);
            longPressesNeeded = 0;
            twoFingerTapsNeeded = 0;

            taskCompletionTime = 0L;
            startTime = 0L;
            endTime = 0L;
            actionsPerTask = 0;
            errorRate = 0;
            // smartwatch only
            swipesPerTasks = 0;
            swipeHoldsPerTasks = 0;
            tapsPerTasks = 0;
            longPressesPerTasks = 0;
            twoFingerTapsPerTasks = 0;
            crownRotatesPerTasks = 0;
        }
    }

    private int calculateS1T1ActionsNeeded() {
        Movie movie = MovieList.getMovie(targetMovie);

        int count = 0;
        if (task.equals(TaskType.TYPE_TASK_FIND.name)) {
            if (block <= 1) {
                count = movie.getCategoryIndex() + movie.getPosition() + 1; // vertical navigation + horizontal navigation + click
            } else {
                Movie prevMovie = MovieList.getMovie(dataSet == 0 ? session1_targetMovies[block - 2] : session1_targetMovies2[block - 2]);
                int categoryDiff = Math.abs(movie.getCategoryIndex() - prevMovie.getCategoryIndex());
                int positionDiff = Math.abs(movie.getPosition() - prevMovie.getPosition());
                count = categoryDiff + positionDiff + 1; // vertical difference + horizontal difference + click
            }
        }
        return count;
    }
}
