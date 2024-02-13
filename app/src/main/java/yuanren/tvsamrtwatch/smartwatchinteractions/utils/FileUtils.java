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
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Block;
import yuanren.tvsamrtwatch.smartwatchinteractions.log.Task;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.Point;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.qdollar.PointCloud;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String extension = ".csv";
    public static final String blockHeader1 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Block Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Block,Actions Needed,Error Rate,Action Up Per Block on TV\n";
    public static final String blockHeader2 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Block Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Block,Actions Needed,Error Rate,Action Up Per Block on TV\n";
    public static final String blockHeader3 = "Participant,Method,Session,Data Set,Block,Block Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Block,Error Rate\n";

    public static final String taskHeader1 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Task,Task Name,Task Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Task,Actions Needed,Error Rate,Action Up Per Task on TV,Swipes Per Tasks,Swipes Needed,Swipe-Holds Per Task,Swipe-Holds Needed,Taps Per Task,Taps Needed,Long Presses Per Task,Long Presses Needed,Two Finger Taps Per Task,Two Finger Taps Needed,Crown Rotates Per Task,Crown Rotates Needed\n";
    public static final String taskHeader2 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Task,Task Name,Task Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Task,Actions Needed,Error Rate,Action Up Per Task on TV,Swipes Per Tasks,Swipes Needed,Swipe-Holds Per Task,Swipe-Holds Needed,Taps Per Task,Taps Needed,Long Presses Per Task,Long Presses Needed,Two Finger Taps Per Task,Two Finger Taps Needed,Crown Rotates Per Task,Crown Rotates Needed\n";
    public static final String taskHeader3 = "Participant,Method,Session,Data Set,Block,Target Movie,Movie Length(s),Selected Movie,Task,Task Name,Task Completion Time (ms),Start Time(ms),End Time(ms),Actions Per Task,Error Rate,Position On Select,Character Per Second,Backspace Count,Time Per Character(ms),Total Character Entered\n";

    public static final String logRawHeader = "Participant,Method,Session,Block,Target Movie,Selected Movie,Task,Action,Scope,Start Time,End Time,Duration,Other\n";

    public static void write(Context context, Task task) {
        String filename;
        if (task.sid == 3) {
            filename = "P" + task.pid + "-" + task.method + "-Search-Raw-Metrics" + extension;
        } else {
            filename = "P" + task.pid + "-" + task.method + "-Raw-Metrics" + extension;
        }

        File file = new File(context.getFilesDir(), filename);

        String data = "";
        if (!file.exists()) {
            if (task.sid == 1) {
                data = taskHeader1;
            } else if (task.sid == 2) {
                data = taskHeader2;
            } else {
                data = taskHeader3;
            }
        }
        data += task.toString();

        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Task data written");
    }

    public static void write(Context context, Block block) {
        String filename;
        if (block.sid == 3) {
            filename = "P" + block.pid + "-" + block.method + "-Search-Metrics" + extension;
        } else {
            filename = "P" + block.pid + "-" + block.method + "-Metrics" + extension;
        }

        File file = new File(context.getFilesDir(), filename);

        String data = "";
        if (!file.exists()) {
            if (block.sid == 1) {
                data = blockHeader1;
            } else if (block.sid == 2) {
                data = blockHeader2;
            } else {
                data = blockHeader3;
            }
        }
        data += block.toString();

        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Block data written");
    }

    public static void writeRaw(Context context, Action action) {
        if (action == null) {
            return;
        }
        String filename;
        if (action.sid == 3) {
            filename = "P" + action.pid + "-" + action.method + "-Search-Raw-Log" + extension;
        } else {
            filename = "P" + action.pid + "-" + action.method +"-Raw-Log" + extension;
        }

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
                String[] text = line.split(",");
                String name = text[0];

                ArrayList<Point> points = new ArrayList<>();
                for (int i = 1; i < text.length; ++i) {
                    String[] p = text[i].split("\\t");
                    Log.d(TAG, "points: " + text[i]);
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
