package yuanren.tvsamrtwatch.smartwatchinteractions.utils;

import android.content.Context;
import android.util.Log;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import yuanren.tvsamrtwatch.smartwatchinteractions.BuildConfig;
import yuanren.tvsamrtwatch.smartwatchinteractions.utils.SelfSignedCertificate;

public class NetworkUtils {
    public static final String TAG = "NetworkUtils";

    public static final int KEY_PAIR_SIZE = 2048;
    public static final Long CERTIFICATE_VALID_TO = 60L * 60 * 24 * 365;  // one year later


    public static final int SERVER_PORT = 6467;
//    public static final String SERVER_IP = "10.0.0.4";
//    public static final String SERVER_IP = "192.168.0.111";
    public static final String SERVER_IP = "192.168.0.19";
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void createConnection() {
        if (socket != null) {
            Log.d(TAG, "Already connected");
            return;
        }

        try {
            // establish a connection
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            socket = new Socket(serverAddress, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (socket.isBound()) {
                Log.d(TAG, "Connected");
            }

        } catch (IOException e1) {
            Log.d(TAG,"Problem Connecting to server... Check your server IP and Port and try again");
            Log.d(TAG,e1.getMessage());
            e1.printStackTrace();
        } catch (NullPointerException e2) {
            Log.d(TAG,"Error returned");
        }
    }

    public static void send(byte[] payload) {
        if (out != null) {
            out.println(payload);
            Log.d(TAG,"Message sent");
        }
    }

    public static void receive() {
        try {
            if (in != null) {
                String responseMessage = in.readLine();

                while (responseMessage == null) {
                    responseMessage = in.readLine();
                }

                Log.d(TAG,"Server response: " + responseMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopConnection() {
        try {
            if (out != null) {
                socket.close();
                in.close();
                out.close();
                Log.i(TAG, "Client socket terminated.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateCertificate(Context context) {
        try {
//            // Avoid regenerating if the certificate already existed
            Path path = Paths.get(context.getFilesDir().getAbsolutePath() + "/client.pem");
//            if (Files.exists(path)) {
//                Log.d(TAG, "Certificate already existed.");
//                return;
//            }
            SelfSignedCertificate certificate = new SelfSignedCertificate(BuildConfig.APPLICATION_ID);
            Log.d(TAG, "Certificate generated successfully!");

            // Export the certificate to PEM file
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(path.toFile()))) {
                writer.writeObject(certificate.cert()); // public
                writer.flush();
                writer.writeObject(certificate.key());
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"Certificate exported successfully!");
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createSSLConnection() {

        // Create an SSLSocket and connect to the server
        SSLSocket sslSocket = null;
        try {
            sslSocket = (SSLSocket) getSocketFactory("", "", "", "").createSocket(SERVER_IP, SERVER_PORT);

            // Perform SSL handshake
            sslSocket.startHandshake();
            Log.d(TAG, "Start handshake!");

            // Send and receive data from the server
            OutputStream outputStream = sslSocket.getOutputStream();
            InputStream inputStream = sslSocket.getInputStream();

            // Write data to the server
            String message = "Hello, Server!";
            outputStream.write(message.getBytes());

            // Read response from the server
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String response = new String(buffer, 0, bytesRead);
            Log.d(TAG, "Server response: " + response);

            // Close the socket
            sslSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SSLSocketFactory getSocketFactory(final String caCrtFile, final String crtFile, final String keyFile,
                                                    final String password) {
        try {

            /**
             * Add BouncyCastle as a Security Provider
             */
            Security.addProvider(new BouncyCastleProvider());

            JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter().setProvider("BC");

            /**
             * Load Certificate Authority (CA) certificate
             */
            PEMParser reader = new PEMParser(new FileReader(caCrtFile));
            X509CertificateHolder caCertHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

            X509Certificate caCert = certificateConverter.getCertificate(caCertHolder);

            /**
             * Load client certificate
             */
            reader = new PEMParser(new FileReader(crtFile));
            X509CertificateHolder certHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

            X509Certificate cert = certificateConverter.getCertificate(certHolder);

            /**
             * Load client private key
             */
            reader = new PEMParser(new FileReader(keyFile));
            Object keyObject = reader.readObject();
            reader.close();

            PEMDecryptorProvider provider = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
            JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter().setProvider("BC");

            KeyPair key;

            if (keyObject instanceof PEMEncryptedKeyPair) {
                key = keyConverter.getKeyPair(((PEMEncryptedKeyPair) keyObject).decryptKeyPair(provider));
            } else {
                key = keyConverter.getKeyPair((PEMKeyPair) keyObject);
            }

            /**
             * CA certificate is used to authenticate server
             */
            KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            caKeyStore.load(null, null);
            caKeyStore.setCertificateEntry("ca-certificate", caCert);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(caKeyStore);

            /**
             * Client key and certificates are sent to server so it can authenticate the client
             */
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(null, null);
            clientKeyStore.setCertificateEntry("certificate", cert);
            clientKeyStore.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                    new Certificate[]{cert});

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());

            /**
             * Create SSL socket factory
             */
            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            /**
             * Return the newly created socket factory object
             */
            return context.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

