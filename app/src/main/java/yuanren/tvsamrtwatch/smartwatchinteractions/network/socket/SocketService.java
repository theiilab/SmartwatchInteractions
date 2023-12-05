package yuanren.tvsamrtwatch.smartwatchinteractions.network.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.AndroidTVRemoteService;

public class SocketService {
    public static final String TAG = "SocketService";
    public static final int SERVER_PORT = 5050;

    private static Socket socket;
    private static PrintWriter out;
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
            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (socket.isBound()) {
                Log.i(TAG, "Connected");
            }
        } catch (IOException e1) {
            Log.i(TAG,"Problem Connecting to server... Check your server IP and Port and try again");
            Log.i(TAG,e1.getMessage());
            e1.printStackTrace();
        } catch (NullPointerException e2) {
            Log.i(TAG,"Error returned");
        }

    }

    public static void send(String messages) {
        if (out != null) {
            out.println(messages);
            Log.i(TAG,"Message sent");
        }
    }

    public static void stopConnection() {
        try {
            if (out != null) {
                socket.close();
//                in.close();
                out.close();
                Log.i(TAG, "Client socket terminated.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
