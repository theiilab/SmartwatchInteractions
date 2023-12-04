package yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar;

import java.util.ArrayList;

public class PointCloud {
    public static final int numPoints = 32;
    private static Point origin = new Point(0,0,0);
    private static final int maxIntCoord = 1024;
    private static final int lutSize = 64; // default size of the lookup table is 64 x 64
    public static final double lutScaleFactor = maxIntCoord / lutSize; // used to scale from (IntX, IntY) to LUT
    String name;
    ArrayList<Point> points;
    int[][] lut;

    public PointCloud(String name, ArrayList<Point> points) {
        this.name = name;
        this.points = normalize(points);

    }

    private ArrayList<Point> normalize(ArrayList<Point> points) {
        points = resample(points);
        points = scale(points);
        points = translateTo(points);

        // constructs a lookup table for fast lower bounding (used by $Q)
        points = makeIntCoords(points);
        this.lut = computeLUT(points);

        return points;
    }
    private ArrayList<Point> resample(ArrayList<Point> points) {
        double I = pathLength(points) / (numPoints - 1); // interval length
        double D = 0.0;
        ArrayList<Point> newPoints = new ArrayList<>();
        newPoints.add(new Point(points.get(0).x, points.get(0).y, points.get(0).id));

        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).id == points.get(i - 1).id) {
                double d = euclideanDistance(points.get(i - 1), points.get(i));
                if ((D + d) >= I) {
                    double qx = points.get(i - 1).x + ((I - D) / d) * (points.get(i).x - points.get(i - 1).x);
                    double qy = points.get(i - 1).y + ((I - D) / d) * (points.get(i).y - points.get(i - 1).y);
                    Point q = new Point(qx, qy, points.get(i).id);
                    newPoints.add(q); // append new point 'q'
                    points.add(i, q); // insert 'q' at position i in points s.t. 'q' will be the next i
                    D = 0.0;
                } else {
                    D += d;
                }
            }
        }

        // Sometimes we fall a rounding-error short of adding the last point, so add it if so
        if (newPoints.size() == numPoints - 1) {
            newPoints.add(new Point(points.get(points.size() - 1).x, points.get(points.size() - 1).y, points.get(points.size() - 1).id));
        }

        return newPoints;
    }

    private ArrayList<Point> scale(ArrayList<Point> points) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Point point : points) {
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
        }

        double size = Math.max(maxX - minX, maxY - minY);
        ArrayList<Point> newPoints = new ArrayList<>();

        for (Point point : points) {
            double qx = (point.x - minX) / size;
            double qy = (point.y - minY) / size;
            newPoints.add(new Point(qx, qy, point.id));
        }

        return newPoints;
    }

    private ArrayList<Point> translateTo(ArrayList<Point> points) {
        Point c = centroid(points);
        ArrayList<Point> newPoints = new ArrayList<>();

        for (Point point : points) {
            double qx = point.x + origin.x - c.x;
            double qy = point.y + origin.y - c.y;
            newPoints.add(new Point(qx, qy, point.id));
        }

        return newPoints;
    }

    private ArrayList<Point> makeIntCoords(ArrayList<Point> points) {
        for (Point point : points) {
            point.intX = (int) Math.round((point.x + 1.0) / 2.0 * (maxIntCoord - 1));
            point.intY = (int) Math.round((point.y + 1.0) / 2.0 * (maxIntCoord - 1));
        }

        return points;
    }

    private int[][] computeLUT(ArrayList<Point> points) {
        int[][] res = new int[lutSize][lutSize];

        for (int x = 0; x < lutSize; x++) {
            for (int y = 0; y < lutSize; y++) {
                int u = -1;
                double b = Double.POSITIVE_INFINITY;

                for (int i = 0; i < points.size(); i++) {
                    int row = (int) Math.round(points.get(i).intX / lutScaleFactor);
                    int col = (int) Math.round(points.get(i).intY / lutScaleFactor);
                    double d = Math.pow((row - x), 2) + Math.pow((col - y), 2);

                    if (d < b) {
                        b = d;
                        u = i;
                    }
                }
                res[x][y] = u;
            }
        }
        return res;
    }

    private double pathLength(ArrayList<Point> points) {
        double d = 0.0;
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).id == points.get(i - 1).id) {
                d += euclideanDistance(points.get(i - 1), points.get(i));
            }
        }
        return d;
    }

    private double euclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private Point centroid(ArrayList<Point> points) {
        double x = 0.0, y = 0.0;

        for (Point point : points) {
            x += point.x;
            y += point.y;
        }

        x /= points.size();
        y /= points.size();

        // find the center point
        return new Point(x, y, 0);
    }
}
