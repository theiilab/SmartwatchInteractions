package yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yuanren.tvsamrtwatch.smartwatchinteractions.utils.FileUtils;

public class QDollarRecognizer {
    private static final String TAG = "QDollarRecognizer";
    private final int numPointClouds = 16;

    private ArrayList<PointCloud> pointClouds;

    public QDollarRecognizer(Context context) {
        pointClouds = new ArrayList<>();

        // Initialize PointClouds array with predefined gestures
        ArrayList<PointCloud> preDefined = FileUtils.read(context);
        pointClouds.addAll(preDefined);

//        pointClouds.add(new PointCloud("-", new ArrayList<>(Arrays.asList(
//                new Point(150,60,1), new Point(150,90,1), new Point(150,120,1), new Point(150,150,1)
//        ))));
    }

    public Result classify(ArrayList<Point> points) {
        long t0 = System.currentTimeMillis();
        PointCloud candidate = new PointCloud("", points);

        int index = -1;
        double minSoFar = Double.POSITIVE_INFINITY;

        for (int i = 0; i < pointClouds.size(); i++) {
            double distance = cloudMatch(candidate, pointClouds.get(i), minSoFar);
            if (distance < minSoFar) {
                minSoFar = distance;
                index = i;
            }
        }

        long t1 = System.currentTimeMillis();

        return (index == -1) ? new Result("No match.", 0.0, t1 - t0) :
                new Result(pointClouds.get(index).name, minSoFar > 1.0 ? 1.0 / minSoFar : 1.0, t1 - t0);
    }

    public int addGesture(String name, ArrayList<Point> points) {
        // add gesture to the end of the list
        pointClouds.add(new PointCloud(name, points));

        int num = 0;
        for (int i = 0; i < pointClouds.size(); i++) {
            if (pointClouds.get(i).name.equals(name)) {
                num++;
            }
        }

        return num;
    }

    public int deleteUserGestures() {
        pointClouds.subList(0, numPointClouds).clear();
        return numPointClouds;
    }

    private double cloudMatch(PointCloud candidate, PointCloud template, double minSoFar) {
        int n = candidate.points.size();
        int step = (int) Math.floor(Math.pow(n, 0.5));

        double[] LB1 = computeLowerBound(candidate.points, template.points, step, template.lut);
        double[] LB2 = computeLowerBound(template.points, candidate.points, step, candidate.lut);

        for (int i = 0, j = 0; i < n; i += step, j++) {
            if (LB1[j] < minSoFar)
                minSoFar = Math.min(minSoFar, cloudDistance(candidate.points, template.points, i, minSoFar));
            if (LB2[j] < minSoFar)
                minSoFar = Math.min(minSoFar, cloudDistance(template.points, candidate.points, i, minSoFar));
        }

        return minSoFar;
    }

    private double cloudDistance(ArrayList<Point> pts1, ArrayList<Point> pts2, int start, double minSoFar) {
        int n = pts1.size();
        List<Integer> unmatched = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            unmatched.add(j);
        }

        int i = start;
        int weight = n;
        double sum = 0.0;
        do {
            int u = -1;
            double b = Double.POSITIVE_INFINITY;
            for (int j = 0; j < unmatched.size(); j++) {
                double d = sqrEuclideanDistance(pts1.get(i), pts2.get(unmatched.get(j)));
                if (d < b) {
                    b = d;
                    u = j;
                }
            }

            unmatched.remove(u);
            sum += weight * b;

            if (sum >= minSoFar) {
                return sum;
            }

            weight--;
            i = (i + 1) % n;

        } while (i != start);

        return sum;
    }

    private double[] computeLowerBound(ArrayList<Point> pt1, ArrayList<Point> pt2, int step, int[][] lut) {
        int n = pt1.size();
        double[] lb = new double[n / step + 1];
        double[] sat = new double[n];
        lb[0] = 0.0;

        for (int i = 0; i < n; i++) {
            int x = (int) Math.round(pt1.get(i).x / PointCloud.lutScaleFactor);
            int y = (int) Math.round(pt1.get(i).y / PointCloud.lutScaleFactor);
            int index = lut[x][y];
            if (i == pt2.size()) {
                Log.d(TAG, "123");
            }
            double d = sqrEuclideanDistance(pt1.get(i), pt2.get(i));
            sat[i] = (i == 0) ? d : sat[i - 1] + d;
            lb[0] += (n - i) * d;
        }

        for (int i = step, j = 1; i < n; i += step, j++) {
            lb[j] = lb[0] + i * sat[n - 1] - n * sat[i - 1];
        }

        return lb;
    }

    private double sqrEuclideanDistance(Point pt1, Point pt2) {
        double dx = pt2.x - pt1.x;
        double dy = pt2.y - pt1.y;
        return (dx * dx + dy * dy);
    }
}
