package yuanren.tvsamrtwatch.smartwatchinteractions.network.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;

public class RandomPositionSocketService {
    public static final String TAG = "RandomPositionSocketService";
    public static final int SERVER_PORT = 5051;
    private static Socket socket;
    private static BufferedReader in;

    public static void createConnection() {
        if (socket != null) {
            Log.i(TAG, "Already connected");
            return;
        }

        try {
            // establish a connection
            InetAddress serverAddress = InetAddress.getByName(AndroidTVRemoteService.SERVER_IP);
            socket = new Socket(serverAddress, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (socket.isBound()) {
                Log.i(TAG, "Connected");
            }
        } catch (IOException e1) {
            Log.i(TAG,"Problem Connecting to server... Check your server IP and Port and try again");
        } catch (NullPointerException e2) {
            Log.i(TAG,"Error returned");
        }

    }

    public static String receive() {
        if (in != null) {
            try {
                //checks to see if it is still connected and displays disconnected if disconnected
                String inputLine = in.readLine();
                if (inputLine == null) {
                    Thread.interrupted();
                    Log.i(TAG, "Server disconnected");
                }

                Log.i(TAG, "Server : " + inputLine);
                return inputLine;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    public static void stopConnection() {
        try {
            if (socket != null) {
                socket.close();
                in.close();
                Log.i(TAG, "Client socket terminated");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
