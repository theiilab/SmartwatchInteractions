package yuanren.tvsamrtwatch.smartwatchinteractions;

import android.content.Context;
import android.util.Log;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class NetworkUtils {
    public static final String TAG = "NetworkUtils";

    public static final int KEY_PAIR_SIZE = 2048;
    public static final Long CERTIFICATE_VALID_TO = 60L * 60 * 24 * 365;  // one year later


    public static final int SERVER_PORT = 6467;
//    public static final String SERVER_IP = "10.0.0.4";
//    public static final String SERVER_IP = "192.168.0.111";
    public static final String SERVER_IP = "192.168.0.19";

    private static X509CertificateHolder certificateHolder;
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


            // Generate key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_PAIR_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Get the SubjectPublicKeyInfo
            SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

            // Certificate validity dates
            Instant now = Instant.now();
            Date validFrom = Date.from(now);
            Date validTo = Date.from(now.plusSeconds(CERTIFICATE_VALID_TO));

            // Create the certificate builder
            X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                    new X500Name("CN=Smartwatch Interactions,O=Yuan Ren,L=My City,C=DE"),
                    BigInteger.ONE,
                    validFrom,
                    validTo,
                    new X500Name("CN=Smartwatch Interactions,O=Yuan Ren,L=My City,C=DE"),
                    subPubKeyInfo
            );

            // Create the signer
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA")
                    .setProvider(new BouncyCastleProvider())
                    .build(keyPair.getPrivate());

            // Build the certificate
            certificateHolder = certBuilder.build(signer);

            Log.d(TAG, "Certificate generated successfully!");

            String head = "-----BEGIN CERTIFICATE-----\n";
            String content = java.util.Base64.getMimeEncoder(64,
                    new byte[] {'\r', '\n'}).encodeToString(certificateHolder.getEncoded());
            String end = "-----END CERTIFICATE-----";
            SelfSignedCertificate certificate = new SelfSignedCertificate(BuildConfig.APPLICATION_ID);

            // Export the public key
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(path.toFile()))) {
//                X509CertificateHolder certHolder = new X509CertificateHolder(certificate.getEncoded());
//                writer.writeObject(certificateHolder.getSubjectPublicKeyInfo());
                writer.writeObject(certificate.cert());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Export the private key
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(path.toFile(), true))) {
                writer.writeObject(keyPair.getPrivate());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"Public and private keys exported successfully!");
        } catch (NoSuchAlgorithmException | OperatorCreationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    public static Certificate createSSLConnection(Context context) {
        try {
            // Load certificate
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(certificateHolder.getEncoded()));

            // Create a TrustManagerFactory with the loaded certificate
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("mycertificate", certificate);
            trustManagerFactory.init(keyStore);
            Log.d(TAG,"Created trustManager successfully!");

            // Create an SSLContext with the TrustManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // Create an SSLSocketFactory from the SSLContext
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Create an SSLSocket and connect to the server
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(SERVER_IP, SERVER_PORT);

            // Perform SSL handshake
            sslSocket.startHandshake();
            Log.d(TAG,"Start handshake!");

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
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
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

