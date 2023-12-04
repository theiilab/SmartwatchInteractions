package yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar;

public class Result {
    public String name;
    public double score;
    long time;

    Result(String name, double score, long ms) {
        this.name = name;
        this.score = score;
        this.time = ms;
    }
}
