package yuanren.tvsamrtwatch.smartwatchinteractions.log;

import androidx.annotation.NonNull;

import yuanren.tvsamrtwatch.smartwatchinteractions.log.Session;

public class Action {
    public int pid = 0;
    public String method = "";
    public int sid = 0;
    private int bid = 0;
    public String targetMovie = "";
    public String selectedMovie = "";
    private int tid = 0;
    private String name;
    private String scope; // performed in which activity
    private double startTime;
    private double endTime;
    private double duration;
    private String other = ""; // tap (the movie name selected, x-ray item selected, etc), swipe (directions)

    public Action(Session session, String selectedMovie, String name, String scope, double startTime, double endTime) {
        this.pid = session.pid;
        this.method = session.method;
        this.sid = session.id;
        this.bid = session.getCurrentBlock().id;
        this.targetMovie = this.sid == 3 ? session.getCurrentBlock().getCurrentTask().targetMovie : session.getCurrentBlock().targetMovie;
        this.selectedMovie = selectedMovie;

        this.tid = session.getCurrentBlock().getCurrentTask().id;
        this.name = name;
        this.scope = scope;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = endTime - startTime;
    }

    public Action(Session session, String selectedMovie, String name, String scope, double startTime, double endTime, String other) {
        this.pid = session.pid;
        this.method = session.method;
        this.sid = session.id;
        this.bid = session.getCurrentBlock().id;
        this.targetMovie = this.sid == 3 ? session.getCurrentBlock().getCurrentTask().targetMovie : session.getCurrentBlock().targetMovie;
        this.selectedMovie = selectedMovie;

        this.tid = session.getCurrentBlock().getCurrentTask().id;
        this.name = name;
        this.scope = scope;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = endTime - startTime;
        this.other = other;
    }

    @NonNull
    @Override
    public String toString() {
        return "" + pid + "," + method + "," + sid + "," + bid + "," + targetMovie + "," + selectedMovie + "," + tid + "," + name + "," + scope + "," + startTime + "," + endTime + "," + duration + "," + other + "\n";
    }
}
