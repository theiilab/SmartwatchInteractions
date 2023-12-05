package yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.certificate.CertificateGenerator;

public class AndroidTVRemoteService {
    public static final String TAG = "NetworkUtils";


    public static final int SERVER_COMM_PORT = 6466;  // port for sending commands
    public static final String SERVER_IP = "10.0.0.136";
//    public static final String SERVER_IP = "192.168.0.111";
//    public static final String SERVER_IP = "192.168.0.19";

    private static CertificateGenerator generator;
    private static Context context;
    private static SSLSocket commSocket;
    private static OutputStream commOutputStream;
    private static InputStream commInputStream;

    //    private static boolean isCommConnectionAlive = false;
    private static boolean isPingPongWatcherAlive = false;

    private static PingPongWatcher pingPongWatcher = new PingPongWatcher();;

    public static void createSSLCommConnection(Context ct) {
        context = ct;
        generator = new CertificateGenerator(ct);
        try {
            SSLSocketFactory socketFactory = generator.getSocketFactory();
            commSocket = (SSLSocket) socketFactory.createSocket(SERVER_IP, SERVER_COMM_PORT);

            // Perform SSL handshake
            commSocket.startHandshake();
            Log.d(TAG, "Start comm handshake!");

            // Send and receive data from the server
            commOutputStream = commSocket.getOutputStream();
            commInputStream = commSocket.getInputStream();

            Log.d(TAG, "After communication SSL connected");
            receive();

            // 1st configuration message
            byte[] payload = configuring1();
            send(payload);
            Log.d(TAG, "After 1st configuration");
            receive();  // server will respond with 2 message
            receive();

            // 2nd configuration message
            payload = configuring2();
            Log.d(TAG, "After 2nd configuration");
            send(payload);
            receive();  // server will respond with 3 message
            receive();
            receive();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopSSLCommConnection() {
        try {
            if (commOutputStream != null) {
                commSocket.close();
                commInputStream.close();
                commOutputStream.close();

                if (pingPongWatcher != null) {
                    pingPongWatcher.interrupt();
                }
                Log.i(TAG, "Client communication socket terminated.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendCommand(int keyCode) {

//        if (!isCommConnectionAlive) {
//            Log.d(TAG, "Create comm connection");
//            createSSLCommConnection(context);
//            isCommConnectionAlive = true;
//        }
        // actual command message
        byte[] payload = getCommandDown(keyCode);  // for action down
        send(payload);
        payload = getCommandUp(keyCode);  // for action up
        send(payload);

        if (!isPingPongWatcherAlive) {
            Log.d(TAG, "ping pong thread fire!");
            pingPongWatcher.start();
            isPingPongWatcherAlive = true;
        }
    }

    private static void send(byte[] payload) {
        if (commOutputStream != null) {
            try {
                commOutputStream.write(payload);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static byte[] receive() {
        byte[] serverResponse = new byte[200];
        try {
            commInputStream.read(serverResponse);
            printResponse(serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverResponse;
    }

    private static byte[] configuring1() {
        byte[] tag1 = new byte[] {10};
        byte[] a = new byte[] {8, (byte) 238, 4, 18};  // ???
        byte[] b = new byte[] {24, 1, 34};  // ???
        byte[] appVersionNo = new byte[] {49};  // your app version number : 1
        byte[] sizeOfAppVersionNo = new byte[] {(byte) appVersionNo.length};  // your app version number : 1
        byte[] tag2 = new byte[] {42};
        byte[] packageName = new byte[] {97, 110, 100, 114, 111, 105, 116, 118, 45, 114, 101, 109, 111, 116, 101};  // package name: androidtv-remote
        byte[] sizeOfPackage = new byte[] {(byte) packageName.length};
        byte[] tag3 = new byte[] {50};
        byte[] appVersion = new byte[] {49,46,48,46,48};  // app version: 1.0.0
        byte[] sizeOfAppVersion = new byte[] {(byte) appVersion.length};

        int lengthOfSubMessage = b.length + sizeOfAppVersionNo.length + appVersionNo.length + tag2.length + sizeOfPackage.length + packageName.length + tag3.length + sizeOfAppVersion.length + appVersion.length;
        byte[] sizeOfSubMessage = new byte[] {(byte) lengthOfSubMessage};  // the length of sub message
        int lengthOfWholeMessage = tag1.length + a.length + lengthOfSubMessage;
        byte[] sizeOfWholeMessage = new byte[] {(byte) lengthOfWholeMessage};  // the length of whole message
        byte[] lengthOfOverall = new byte[] {(byte) (lengthOfWholeMessage + 2)};  // the length of total

        // prepare the payload byte array
        byte[] allByteArray = new byte[lengthOfWholeMessage + 3];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(lengthOfOverall);
        buff.put(tag1);
        buff.put(sizeOfWholeMessage);
        buff.put(a);
        buff.put(sizeOfSubMessage);
        buff.put(b);
        buff.put(sizeOfAppVersionNo);
        buff.put(appVersionNo);
        buff.put(tag2);
        buff.put(sizeOfPackage);
        buff.put(packageName);
        buff.put(tag3);
        buff.put(sizeOfAppVersion);
        buff.put(appVersion);

        return buff.array();
    }

    private static byte[] configuring2() {
        byte[] a = new byte[] {18, 3, 8, (byte) 238, 4};
        byte[] lengthOfOverall = new byte[] {(byte) (a.length)};

        byte[] allByteArray = new byte[a.length + 1];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(lengthOfOverall);
        buff.put(a);

        return buff.array();
    }

    private static byte[] getCommandDown(int key) {
        byte[] commandTag = new byte[] {82, 4, 8};  // the command tag
        byte[] keyEvent = new byte[] {(byte) key};
        byte[] action = new byte[] {16, 1};  // (16, 1) for action down or (16, 2) for action up

        int length = commandTag.length + keyEvent.length + action.length;
        byte[] lengthOfOverall = new byte[] {(byte) length};

        byte[] allByteArray = new byte[length + 1];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(lengthOfOverall);
        buff.put(commandTag);
        buff.put(keyEvent);
        buff.put(action);

        return buff.array();
    }

    private static byte[] getCommandUp(int key) {
        byte[] commandTag = new byte[] {82, 4, 8};  // the command tag
        byte[] keyEvent = new byte[] {(byte) key};
        byte[] action = new byte[] {16, 2};  // (16, 1) for action down or (16, 2) for action up

        int length = commandTag.length + keyEvent.length + action.length;
        byte[] lengthOfOverall = new byte[] {(byte) length};

        byte[] allByteArray = new byte[length + 1];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(lengthOfOverall);
        buff.put(commandTag);
        buff.put(keyEvent);
        buff.put(action);

        return buff.array();
    }

    private static void printResponse(byte[] payload) {
        String s = "";

        for (byte b: payload) {
            s += String.valueOf(b) + ",";
        }
        Log.d(TAG, "Server response: " + s);
    }

    private static class PingPongWatcher extends Thread {
        @Override
        public void run() {
            byte[] response = new byte[50];
            int semaphore = 0;
            int turn = 0;
            try {
//                while (true) {
                    while (commInputStream.read(response) != -1) { // received ping message from server
                        Log.d(TAG, "Server pings");
                        printResponse(response);

                        // wait for 3 pings
                        semaphore++;

                        byte pong = 0;
                        if (response[0] == 8) {
                            pong = response[1];
                        }

                        if (semaphore == 3) {
                            semaphore = 0;

                            if (pong < 0) {
                                turn = pong == -128 ? turn + 1: turn;
                                pong += 256;
                            }

                            if (turn == 0) {
                                send(new byte[]{4, 74, 2, 8, pong});
                                Log.d(TAG, "Sending: 4, 74, 2, 8, " + pong);
                            } else {
                                send(new byte[]{5, 74, 3, 8, pong, (byte) turn});
                                Log.d(TAG, "Sending: 5, 74, 3, 8, " + pong + ", " + turn);
                            }
                        }
                    }
//                }
            } catch (IOException e) {
                Log.d(TAG,e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}