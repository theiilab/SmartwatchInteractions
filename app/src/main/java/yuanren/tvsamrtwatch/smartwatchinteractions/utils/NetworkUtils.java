package yuanren.tvsamrtwatch.smartwatchinteractions.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import yuanren.tvsamrtwatch.smartwatchinteractions.BuildConfig;
import yuanren.tvsamrtwatch.smartwatchinteractions.R;

public class NetworkUtils {
    public static final String TAG = "NetworkUtils";

    public static final int SERVER_PORT = 6467;
    public static final String SERVER_IP = "10.0.0.4";
//    public static final String SERVER_IP = "192.168.0.111";
//    public static final String SERVER_IP = "192.168.0.19";
    private static SSLSocket socket;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static Path path;

    public static void send(byte[] payload) {
        if (outputStream != null) {
            try {
                outputStream.write(payload);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] receive() {
        byte[] serverLength = new byte[1];
        byte[] serverVersion = new byte[2];
        byte[] serverStatus = new byte[3];
        try {
            inputStream.read(serverLength);
            inputStream.read(serverVersion);
            inputStream.read(serverStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverStatus;
    }

    public static void stopConnection() {
        try {
            if (outputStream != null) {
                socket.close();
                inputStream.close();
                outputStream.close();
                Log.i(TAG, "Client socket terminated.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateCertificate(Context context) {
        path = Paths.get(context.getFilesDir().getAbsolutePath());

        try {
            // Avoid regenerating if the certificate already existed
            if (Files.exists(path.resolve("client.pem"))) {
                Log.d(TAG, "Certificate already existed.");
                return;
            }
            SelfSignedCertificate certificate = new SelfSignedCertificate(BuildConfig.APPLICATION_ID);
            Log.d(TAG, "Certificate generated successfully!");

            // Export the certificate to PEM file
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(path.resolve("client.pem").toFile()))) {
                writer.writeObject(certificate.cert()); // public key
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Export the certificate to PEM file
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(path.resolve("private.pem").toFile()))) {
                writer.writeObject(certificate.key()); // private key
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"Certificate exported successfully!");
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void createSSLConnection(Context context) {
        generateCertificate(context);

        // Create an SSLSocket and connect to the server
        String caCrtFile = path.resolve("server.pem").toString();
        String crtFile = path.resolve("client.pem").toString();
        String keyFile = path.resolve("private.pem").toString();

        try {
            socket = (SSLSocket) getSocketFactory(caCrtFile, crtFile, keyFile, "").createSocket(SERVER_IP, SERVER_PORT);

            // Perform SSL handshake
            socket.startHandshake();
            Log.d(TAG, "Start handshake!");

            // Send and receive data from the server
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            byte[] version = new byte[] {8, 2};  // the protocol version 2
            byte[] statusCode = new byte[] {16, (byte) 200, 1};  // Status OK
            byte[] messageTag = new byte[] {82};  // the message tag
            byte[] serviceTag = new byte[] {10};  // the service name tag
            byte[] serviceName = new byte[] {121,117,97,110,114,101,110,46,116,118,115,97,109,114,116,119,97,116,99,104,46,115,109,97,114,116,119,97,116,99,104,105,110,116,101,114,97,99,116,105,111,110,115};  // the service name
            byte[] tagDeviceName = new byte[] {18};  // the tag device
            byte[] clientName = new byte[] {105, 110, 116, 101, 114, 102, 97, 99, 101, 32, 119, 101, 98};  // the client name

            // length of names, messages and overall
            byte[] lenOfServiceName = new byte[] {(byte) serviceName.length};
            byte[] lenOfClientName = new byte[] {(byte) clientName.length};
            int length = version.length + statusCode.length + messageTag.length + 1 + serviceTag.length + serviceName.length + tagDeviceName.length + clientName.length;
            byte[] lengthOfMessage = new byte[] {(byte) (length)};  // the length of the message
            byte[] lengthOfOverall = new byte[] {(byte) (length + 2)};  // the length of total

            // prepare the payload byte array
            byte[] allByteArray = new byte[length + 3];
            ByteBuffer buff = ByteBuffer.wrap(allByteArray);
            buff.put(lengthOfOverall);
            buff.put(version);
            buff.put(statusCode);
            buff.put(messageTag);
            buff.put(lengthOfMessage);
            buff.put(serviceTag);
            buff.put(lenOfServiceName);
            buff.put(serviceName);
            buff.put(tagDeviceName);
            buff.put(lenOfClientName);
            buff.put(clientName);
            byte[] combined = buff.array();

            // Write data to the server
            send(combined);

            // Read response from the server
            System.out.println(receive());

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

            /**
             * Load Certificate Authority (CA) certificate
             */
            PEMParser reader = new PEMParser(new FileReader(caCrtFile));
            X509CertificateHolder caCertHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

            //build the CA certificate
            X509Certificate caCert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(caCertHolder.getEncoded()));

            /**
             * Load client certificate
             */
            reader = new PEMParser(new FileReader(crtFile));
            X509CertificateHolder certHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

            //build the certificate
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                            .generateCertificate(new ByteArrayInputStream(certHolder.getEncoded()));
            Log.d(TAG, String.valueOf(cert.getPublicKey()));

            /**
             * Load client private key
             */
            reader = new PEMParser(new FileReader(keyFile));
            Object keyObject = reader.readObject();
            reader.close();
            Log.d(TAG, String.valueOf(keyObject.toString()));

            PEMDecryptorProvider provider = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
            JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();

            PrivateKey key;
            if (keyObject instanceof PEMEncryptedKeyPair) {
                // Encrypted key - we will use provided password
                key = keyConverter.getPrivateKey(((PEMEncryptedKeyPair) keyObject).decryptKeyPair(provider).getPrivateKeyInfo());
            } else {
                // Unencrypted key - no password needed
                key = keyConverter.getPrivateKey(((PEMKeyPair) keyObject).getPrivateKeyInfo());
            }

            /**
             * CA certificate is used to authenticate server
             */
            KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            caKeyStore.load(null, null);
            caKeyStore.setCertificateEntry("ca-certificate", caCert);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(caKeyStore);

            /**
             * Client key and certificates are sent to server so it can authenticate the client
             */
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(null, null);
            clientKeyStore.setCertificateEntry("certificate", cert);
            clientKeyStore.setKeyEntry("private-key", key, password.toCharArray(),
                    new Certificate[]{cert});

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());

            /**
             * Create SSL socket factory
             */
            SSLContext context = SSLContext.getInstance("TLS"); // TLSv1.2
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

