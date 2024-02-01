package yuanren.tvsamrtwatch.smartwatchinteractions.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import yuanren.tvsamrtwatch.smartwatchinteractions.log.Action;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Metrics;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.PointCloud;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.Point;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String extension = ".csv";
    public static final String logHeader1 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Task Number,Task,Task Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Task,Actions Needed,Error Rate,Swipes Per Tasks,Swipes Needed,Swipe-Holds Per Task,Swipe-Holds Needed,Taps Per Task,Taps Needed,Long Presses Per Task,Long Presses Needed,Two Finger Taps Per Task,Two Finger Taps Needed,Crown Rotates Per Task,Crown Rotates Needed\n";
    public static final String logHeader2 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Task Number,Task,Task Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Task,Actions Needed,Error Rate,Swipes Per Tasks,Swipes Needed,Swipe-Holds Per Task,Swipe-Holds Needed,Taps Per Task,Taps Needed,Long Presses Per Task,Long Presses Needed,Two Finger Taps Per Task,Two Finger Taps Needed,Crown Rotates Per Task,Crown Rotates Needed\n";
    public static final String logHeader3 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Task Number,Task,Task Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Task,Error Rate,Character Per Second,Backspace Count,Time Per Character(ms),Total Character Entered\n";
    public static final String logRawHeader = "Participant,Method,Session,Block,Target Movie,Selected Movie,Action,Scope,Start Time,End Time,Duration,Other\n";

    public static void write(Context context, Metrics metrics) {
        String filename;
        if (metrics.session == 3) {
            filename = "P" + metrics.pid + "-" + metrics.method + "-Search" + extension;
        } else {
            filename = "P" + metrics.pid + "-" + metrics.method + extension;
        }

        File file = new File(context.getFilesDir(), filename);

        String data = "";
        if (!file.exists()) {
            if (metrics.session == 1) {
                data = logHeader1;
            } else if (metrics.session == 2) {
                data = logHeader2;
            } else {
                data = logHeader3;
            }
        }
        data += metrics.toString();

        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Data written");
    }

    public static void writeRaw(Context context, Action action) {
        String filename = "P" + action.pid + "-" + action.method + "-Raw" + extension;

        File file = new File(context.getFilesDir(), filename);

        String data = "";
        if (!file.exists()) {
            data = logRawHeader;
        }
        data += action.toString();

        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Raw data written");
    }

    public static ArrayList<PointCloud> read(Context context) {
        ArrayList<PointCloud> pointClouds = new ArrayList<>();
        String filename = "preDefinedData" + extension;

        try {
            File file = new File(context.getFilesDir(), filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();

            while (line != null) {
                String[] text = line.split("\\t");
                String name = text[0];

                ArrayList<Point> points = new ArrayList<>();
                for (int i = 1; i < text.length; ++i) {
                    String[] p = text[i].split(",");
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
