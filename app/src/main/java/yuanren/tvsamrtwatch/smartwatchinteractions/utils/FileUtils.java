package yuanren.tvsamrtwatch.smartwatchinteractions.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.PointCloud;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.Point;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String extension = ".csv";

    private static String filename = "preDefinedData" + extension;
//    public static final String logHeader = "Participant,Action Id,Method,Action,Task Completion Time (ms),Action Completion Time (ms),Error Rate,Distance Swiped (px),Angle Tilted (degree),Force Changed\n";
//    public static final String logRawHeader = "Participant,Action Id,Method,Action,Task Start Time,Task End Time,Action Start Time,Action End Time,Error Rate,From X,From Y,To X,To Y,Start Angle,End Angle,Min Force, Max Force\n";


    public static void write(Context context, String data) {

        File file = new File(context.getFilesDir(), filename);
//        if (!file.exists()) {
//            data = logHeader + data;
//        }
        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Data written");
    }

    public static void writeRaw(Context context, String data) {
        File file = new File(context.getFilesDir(), filename);
//        if (!file.exists()) {
//            data = logRawHeader + data;
//        }
        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PointCloud> read(Context context) {
        ArrayList<PointCloud> pointClouds = new ArrayList<>();

        try {
            File file = new File(context.getFilesDir(), filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                String[] text = line.split(",");
                String name = text[0];

                ArrayList<Point> points = new ArrayList<>();
                for (int i = 1; i < text.length; ++i) {
                    String[] p = text[i].split("\\t");
                    Point point = new Point(Double.valueOf(p[0]), Double.valueOf(p[1]), Integer.valueOf(p[2]));
                    points.add(point);
                }

                if (points.size() == 0) {
                    Log.d(TAG, "empty point lists");
                }
                PointCloud pointCloud = new PointCloud(name, points);
                pointClouds.add(pointCloud);

                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Data read");
        return pointClouds;
    }
}
