package yuanren.tvsamrtwatch.smartwatchinteractions.log;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yuanren.tvsamrtwatch.smartwatchinteractions.data.MovieList;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;

public class Block {
    private final static String TAG = "Block";
    public final static int SESSION_1_NUM_TASK = 8;
    public final static int SESSION_2_NUM_TASK = 7;
    public final static int SESSION_3_NUM_TASK = 10;
    private Context context;

    public int pid;
    public int sid;
    public String method;
    public int dataSet;

    public int id;
    public String targetMovie = "";
    public int movieLength = 0;
    public String selectedMovie = "";
    public Long startTime = 0L;
    public Long endTime = 0L;
    public Long blockCompletionTime = 0L;
    public int actionsPerBlock = 0;
    public int actionUpsPerBlock = 0;
    public int actionsNeeded = 0;
    public double errorRate = 0;
    public int index = 0; // zero-based
    public int incorrectTitleCount = 0;
    public List<Task> tasks = new ArrayList<>();

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
    private String[] session3_block1_1 = {
            "Dancer in the Dark",
            "Facing the Giants ",
            "Key West",
            "Lady of the Manor",
            "Nancy",
            "Qiu Jin",
            "Radius",
            "San Andreas",
            "Valley of Love",
            "Year by the Sea"
    };
    private String[] session3_block2_1 = {
            "Danton",
            "Far Out Man",
            "Killing Mr Griffin",
            "Learn to Swim",
            "Nelly",
            "Queen Bees",
            "Reach for Me",
            "Scenes of the Crime",
            "Vesper",
            "Yona"
    };
    private String[] session3_block3_1 = {
            "Darling",
            "Feast of Love",
            "Kiss of Death",
            "Lincoln",
            "Never Have I Ever",
            "Quincy",
            "Red Joan",
            "Second Act",
            "Vice",
            "You Again"
    };
    private String[] session3_block4_1 = {
            "Django Unchained",
            "Fantastic Mr Fox",
            "Keeping Mum",
            "Lady Bird",
            "National Treasure",
            "Quality Time",
            "Radiant Reminiscence",
            "Saving Private Ryan",
            "Valentines Day",
            "Year One"
    };

    private String[] session3_block5_1 = {
            "Dunkirk",
            "Fight Club",
            "Killing Moon",
            "Lawrence of Arabia",
            "Nebula Nectar",
            "Quantum Odyssey",
            "Raging Bull",
            "Selma",
            "Van Helsing",
            "Yojimbo"
    };
    private String[] session3_block6_1 = {
            "Dr Strangelove",
            "Ford v Ferrari",
            "King of California",
            "Little Miss Sunshine",
            "No Country for Old Men",
            "Quantum Quasar",
            "Rain Man",
            "Shutter Island",
            "Vertigo",
            "You People"
    };

    private String[] session3_block1_2 = {
            "Dangerous Liaisons",
            "Fairy",
            "Kicked in the Head",
            "Land of Mine",
            "Navalny",
            "Qu Yuan",
            "Radio Flyer",
            "Sami Blood",
            "Vampire Diary",
            "Yes Day"
    };
    private String[] session3_block2_2 = {
            "Dark Star",
            "Fear",
            "Kindergarten Cop",
            "Leaving Neverland",
            "Nemesis",
            "Queen and Country",
            "Ready Player One",
            "Scenic Route",
            "Vessel",
            "Yosemite"
    };
    private String[] session3_block3_2 = {
            "Day for Night",
            "Foster",
            "Knock Knock",
            "Line Walker",
            "No Looking Back",
            "Quiz Show",
            "Redeeming Love",
            "Seeking Justice",
            "Viking",
            "You Are Here"
    };
    private String[] session3_block4_2 = {
            "Dallas Buyers Club",
            "Farewell My Concubine",
            "Kikis Delivery Service",
            "Late Spring",
            "Nausicaa of the Valley of the Wind",
            "Quantum Love",
            "Radiant Reverie",
            "Schindlers List",
            "Valerian and the City of a Thousand Planets",
            "Yellow Rose"
    };
    private String[] session3_block5_2 = {
            "Donnie Darko",
            "Five Centimeters Per Second",
            "King Kong",
            "Life of Pi",
            "Nebula Nexus",
            "Quantum of Solace",
            "Raiders of the Lost Ark",
            "Seven Samurai",
            "Venom",
            "Young Adam"
    };
    private String[] session3_block6_2 = {
            "Drunken Master",
            "Forrest Gump",
            "Knives Out",
            "Little Women",
            "North by Northwest",
            "Quay",
            "Raise the Red Lantern",
            "Silver Linings Playbook",
            "V for Vendetta",
            "Your Name"
    };

    public Block(Context context, int pid, int sid, String method, int dataSet, int bid, String targetMovie) {
        this.context = context;
        this.pid = pid;
        this.sid = sid;
        this.method = method;
        this.dataSet = dataSet;
        this.id = bid;
        this.targetMovie = targetMovie;
        this.movieLength = MovieList.getMovie(context, targetMovie) != null ? MovieList.getMovie(context, targetMovie).getLength() : 0;

        switch (sid) {
            case 1:
                for (int i = 0; i < session1_tasks.length; ++i) {
                    Task task = new Task(pid, sid, method, dataSet, bid, i + 1, session1_tasks[i], targetMovie, movieLength);
                    if (i == 0) {
                        task.actionsNeeded = calculateS1T1ActionsNeeded();
                        task.swipesNeeded = task.actionsNeeded - 1;
                        task.tapsNeeded = 1;
                    } else {
                        task.actionsNeeded = session1_actionsNeeded.get(task.name);
                        task.crownRotatesNeeded = session1_crownRotatesNeeded.get(task.name);
                        task.swipesNeeded = session1_swipesNeeded.get(task.name);
                        task.tapsNeeded = session1_tapsNeeded.get(task.name);
                        task.swipeHoldNeeded = session1_swipeHoldNeeded.get(task.name);
                    }
                    tasks.add(task);
                }
                break;
            case 2:
                for (int i = 0; i < SESSION_2_NUM_TASK; ++i) {
                    Task task = new Task(pid, sid, method, dataSet, bid, i + 1, "Question " + (i + 1), targetMovie, movieLength);
                    task.actionsNeeded = 3;
                    if (i == 0) {
                        task.twoFingerTapsNeeded = 1;
                        task.tapsNeeded = 1;
                        task.longPressesNeeded = 1;
                    } else {
                        task.swipesNeeded = 1;
                        task.tapsNeeded = 1;
                        task.longPressesNeeded = 1;
                    }
                    tasks.add(task);
                }
                break;
            default:
                if (id == 1) {
                    for (int i = 0; i < SESSION_3_NUM_TASK; ++i) {
                        this.targetMovie = dataSet == 0 ? session3_block1_1[i]: session3_block1_2[i];
                        this.movieLength = MovieList.getMovie(context, this.targetMovie) != null ? MovieList.getMovie(context, this.targetMovie).getLength() : 0;
                        Task task = new Task(pid, sid, method, dataSet, bid, i + 1, "Search " + (i + 1), this.targetMovie, movieLength);
                        tasks.add(task);
                    }
                } else if (id == 2) {
                    for (int i = 0; i < SESSION_3_NUM_TASK; ++i) {
                        this.targetMovie = dataSet == 0 ? session3_block2_1[i]: session3_block2_2[i];
                        this.movieLength = MovieList.getMovie(context, this.targetMovie) != null ? MovieList.getMovie(context, this.targetMovie).getLength() : 0;
                        Task task = new Task(pid, sid, method, dataSet, bid, i + 1, "Search " + (i + 1), this.targetMovie, movieLength);
                        tasks.add(task);
                    }
                } else if (id == 3){ // block 3
                    for (int i = 0; i < SESSION_3_NUM_TASK; ++i) {
                        this.targetMovie = dataSet == 0 ? session3_block3_1[i]: session3_block3_2[i];
                        this.movieLength = MovieList.getMovie(context, this.targetMovie) != null ? MovieList.getMovie(context, this.targetMovie).getLength() : 0;
                        Task task = new Task(pid, sid, method, dataSet, bid, i + 1, "Search " + (i + 1), this.targetMovie, movieLength);
                        tasks.add(task);
                    }
                } else if (id == 4) {
                    for (int i = 0; i < SESSION_3_NUM_TASK; ++i) {
                        this.targetMovie = dataSet == 0 ? session3_block4_1[i]: session3_block4_2[i];
                        this.movieLength = MovieList.getMovie(context, this.targetMovie) != null ? MovieList.getMovie(context, this.targetMovie).getLength() : 0;
                        Task task = new Task(pid, sid, method, dataSet, bid, i + 1, "Search " + (i + 1), this.targetMovie, movieLength);
                        tasks.add(task);
                    }
                } else if (id == 5) {
                    for (int i = 0; i < SESSION_3_NUM_TASK; ++i) {
                        this.targetMovie = dataSet == 0 ? session3_block5_1[i]: session3_block5_2[i];
                        this.movieLength = MovieList.getMovie(context, this.targetMovie) != null ? MovieList.getMovie(context, this.targetMovie).getLength() : 0;
                        Task task = new Task(pid, sid, method, dataSet, bid, i + 1, "Search " + (i + 1), this.targetMovie, movieLength);
                        tasks.add(task);
                    }
                } else { // block 6
                    for (int i = 0; i < SESSION_3_NUM_TASK; ++i) {
                        this.targetMovie = dataSet == 0 ? session3_block6_1[i]: session3_block6_2[i];
                        this.movieLength = MovieList.getMovie(context, this.targetMovie) != null ? MovieList.getMovie(context, this.targetMovie).getLength() : 0;
                        Task task = new Task(pid, sid, method, dataSet, bid, i + 1, "Search " + (i + 1), this.targetMovie, movieLength);
                        tasks.add(task);
                    }
                }
                break;
        }
    }

    public Task nextTask() {
        switch (sid) {
            case 1:
                index = Math.min(index + 1, session1_tasks.length - 1);
                break;
            case 2:
                index = Math.min(index + 1, SESSION_2_NUM_TASK - 1);
                break;
            default:
                index = Math.min(index + 1, SESSION_3_NUM_TASK - 1);
                break;
        }
        return tasks.get(index);
    }

    public int getCurrentIndex() {
        return index;
    }

    public Task getCurrentTask() {
        return tasks.get(index);
    }

    @NonNull
    @Override
    public String toString() {
        if (sid == 3) {
            for (Task task: tasks) {
                incorrectTitleCount += task.incorrectTitleCount;
            }
            errorRate = incorrectTitleCount != 0 ? incorrectTitleCount / (incorrectTitleCount + 10.0) : 0;
            blockCompletionTime = endTime - startTime;
            return "" + pid + "," + method + "," + sid + "," + dataSet + "," + id + "," + blockCompletionTime + "," + startTime + "," + endTime + "," + actionsPerBlock + "," + errorRate + "\n";
        } else {
            for (Task task: tasks) {
                actionsNeeded += task.actionsNeeded;
            }
            blockCompletionTime = endTime - startTime;
            errorRate = actionsNeeded != 0 ? ((double) actionsPerBlock - (double) actionsNeeded) / actionsNeeded : 0;
            return "" + pid + "," + method + "," + sid + "," + dataSet + "," + id + "," + targetMovie + "," + movieLength + "," + selectedMovie + "," + blockCompletionTime + "," + startTime + "," + endTime + "," + actionsPerBlock + "," + actionsNeeded + "," + errorRate + "," + actionUpsPerBlock + "\n";
        }
    }

    private int calculateS1T1ActionsNeeded() {
        Movie movie = MovieList.getMovie(context, targetMovie);
        int count = 0;
        if (id == 1) { // 1st movie/block
            count = movie.getCategoryIndex() + movie.getPosition() + 1; // vertical navigation + horizontal navigation + click
        } else {
            int prevId = (int) Math.max(0, movie.getRealId() - 2);
            Movie prevMovie = MovieList.getPrevMovie(context, prevId);
            int categoryDiff = Math.abs(movie.getCategoryIndex() - prevMovie.getCategoryIndex());
            int positionDiff = Math.abs(movie.getPosition() - prevMovie.getPosition());
            count = categoryDiff + positionDiff + 1; // vertical difference + horizontal difference + click
        }
        return count;
    }
}
