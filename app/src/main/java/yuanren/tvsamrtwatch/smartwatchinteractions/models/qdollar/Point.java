package yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar;

public class Point {
    double x;
    double y;
    int id;  // stroke id
    int intX;
    int intY;

    public Point(double x, double y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.intX = 0;
        this.intY = 0;
    }
}