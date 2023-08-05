package yuanren.tvsamrtwatch.smartwatchinteractions.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import yuanren.tvsamrtwatch.smartwatchinteractions.BuildConfig;

public class NetworkUtils {
    public static final String TAG = "NetworkUtils";

    public static final int SERVER_PAIR_PORT = 6467;  // port for pairing
    public static final int SERVER_COMM_PORT = 6466;  // port for sending commands
//    public static final String SERVER_IP = "10.0.0.4";
    public static final String SERVER_IP = "192.168.0.111";
//    public static final String SERVER_IP = "192.168.0.19";

    private static X509Certificate serverCert;
    private static X509Certificate clientCert;
    private static SSLSocketFactory socketFactory;
    private static SSLSocket pairingSocket;
    private static OutputStream pairingOutputStream;
    private static InputStream pairingInputStream;
    private static SSLSocket commSocket;
    private static OutputStream commOutputStream;
    private static InputStream commInputStream;
    private static Path path;

    public static void stopSSLPairingConnection() {
        try {
            if (pairingOutputStream != null) {
                pairingSocket.close();
                pairingInputStream.close();
                pairingOutputStream.close();
                Log.i(TAG, "Client socket terminated.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void createSSLPairingConnection(Context context) {
        generateCertificate(context);

        // Create an SSLSocket and connect to the server
        String caCrtFile = path.resolve("server.pem").toString();  // Run this command in terminal: openssl s_client -showcerts -connect 10.0.0.4:6467 > ~/Desktop/server.pem, and drag the server.pem under device external storage/data/data/YOUR_APP/files/
        String crtFile = path.resolve("client.pem").toString();
        String keyFile = path.resolve("private.pem").toString();

        try {
            socketFactory = getSocketFactory(caCrtFile, crtFile, keyFile, "");
            pairingSocket = (SSLSocket) socketFactory.createSocket(SERVER_IP, SERVER_PAIR_PORT);

            // Perform SSL handshake
            pairingSocket.startHandshake();
            Log.d(TAG, "Start handshake!");

            // Send and receive data from the server
            pairingOutputStream = pairingSocket.getOutputStream();
            pairingInputStream = pairingSocket.getInputStream();

            // Send the Pairing message
            byte[] payload = pairing();
            sendPair(payload);
            receivePair();

            // Send the option message
            payload = optioning();
            sendPair(payload);
            receivePair();

            // Send the Configuration message
            payload = configuring();
            sendPair(payload);
            receivePair();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startPairing(String code) {
        byte[] payload = encodingSecret(code);
        sendPair(payload);
        receivePair();
    }

    public static void createSSLCommConnection() {
        try {
            commSocket = (SSLSocket) socketFactory.createSocket(SERVER_IP, SERVER_COMM_PORT);

            // Perform SSL handshake
            commSocket.startHandshake();
            Log.d(TAG, "Start comm handshake!");

            // Send and receive data from the server
            commOutputStream = commSocket.getOutputStream();
            commInputStream = commSocket.getInputStream();

            receive();

            // 1st configuration message
            byte[] payload = configuring1();
            send(payload);
            receive();

            // 2nd configuration message
            payload = configuring2();
            send(payload);
            receive();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendCommand(int keyCode) {
        // actual command message
        byte[] payload = getCommandDown(keyCode);
        send(payload);
        payload = getCommandUp(keyCode);
        send(payload);
        Log.d(TAG,receive().toString());
    }

    private static void sendPair(byte[] payload) {
        if (pairingOutputStream != null) {
            try {
                pairingOutputStream.write(payload);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static byte[] receivePair() {
        byte[] serverLength = new byte[1];
        byte[] serverVersion = new byte[2];
        byte[] serverStatus = new byte[3];
        byte[] rest = new byte[10];
        try {
            pairingInputStream.read(serverLength);
            pairingInputStream.read(serverVersion);
            pairingInputStream.read(serverStatus);
            pairingInputStream.read(rest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverStatus;
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
        byte[] serverResponse = new byte[100];
        try {
            commInputStream.read(serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverResponse;
    }

    private static byte[] pairing () {
        byte[] version = new byte[] {8, 2};  // the protocol version 2
        byte[] statusCode = new byte[] {16, (byte) 200, 1};  // Status OK
        byte[] messageTag = new byte[] {82};  // the message tag
        byte[] serviceTag = new byte[] {10};  // the service name tag
        byte[] serviceName = new byte[] {121,117,97,110,114,101,110,46,116,118,115,109,97,114,116,119,97,116,99,104,46,115,109,97,114,116,119,97,116,99,104,105,110,116,101,114,97,99,116,105,111,110,115};  // the service name: yuanren.tvsmartwatch.smartwatchinteractions
        byte[] tagDeviceName = new byte[] {18};  // the tag device
        byte[] clientName = new byte[] {105, 110, 116, 101, 114, 102, 97, 99, 101, 32, 119, 101, 98};  // the client name: interface web

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

        return buff.array();
    }

    private static byte[] optioning() {
        byte[] version = new byte[] {8, 2};  // the protocol version 2
        byte[] statusCode = new byte[] {16, (byte) 200, 1};  // Status OK
        byte[] messageTag = new byte[] {(byte) 162};  // the message tag
        byte[] a = new byte[] {1};  // ???
        byte[] encoding = new byte[] {8};  // the encoding output
        byte[] b = new byte[] {10};  // ???
        byte[] size = new byte[] {4};  // the size of ???
        byte[] tagType = new byte[] {8};  // the type tag
        byte[] encodingType  = new byte[] {3};  // the protocol encoding
        byte[] sizeTag  = new byte[] {16};  // the size tag
        byte[] symbolLength  = new byte[] {6};  // the symbol length
        byte[] preferredRoleTag  = new byte[] {24};  // the preferred role tag
        byte[] preferredRole  = new byte[] {1};  // the preferred role

        int length = version.length + statusCode.length + messageTag.length + a.length + encoding.length + b.length + size.length + tagType.length + encodingType.length + sizeTag.length + symbolLength.length + preferredRoleTag.length + preferredRole.length;
        byte[] lengthOfOverall = new byte[] {(byte) length};  // the length of total

        // prepare the payload byte array
        byte[] allByteArray = new byte[length + 1];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(lengthOfOverall);
        buff.put(version);
        buff.put(statusCode);
        buff.put(messageTag);
        buff.put(a);
        buff.put(encoding);
        buff.put(b);
        buff.put(size);
        buff.put(tagType);
        buff.put(encodingType);
        buff.put(sizeTag);
        buff.put(symbolLength);
        buff.put(preferredRoleTag);
        buff.put(preferredRole);

        return buff.array();
    }

    private static byte[] configuring () {
        byte[] version = new byte[] {8, 2};  // the protocol version 2
        byte[] statusCode = new byte[] {16, (byte) 200, 1};  // Status OK
        byte[] messageTag = new byte[] {(byte) 242};  // the message tag
        byte[] a = new byte[] {1};  // ???
        byte[] encodingTag = new byte[] {8};  // the encoding tag
        byte[] b = new byte[] {10};  // ???
        byte[] size = new byte[] {4};  // the size of ???
        byte[] typeTag = new byte[] {8};  // the type tag
        byte[] protocolEncoding  = new byte[] {3};  // the protocol encoding
        byte[] sizeTag  = new byte[] {16};  // the size tag
        byte[] symbolLength  = new byte[] {6};  // the symbol length
        byte[] preferredRoleTag  = new byte[] {16};  // the preferred role tag
        byte[] preferredRole  = new byte[] {1};  // the preferred role

        int length = version.length + statusCode.length + messageTag.length + a.length + encodingTag.length + b.length + size.length + typeTag.length + protocolEncoding.length + sizeTag.length + symbolLength.length + preferredRoleTag.length + preferredRole.length;
        byte[] lengthOfOverall = new byte[] {(byte) length};  // the length of total

        // prepare the payload byte array
        byte[] allByteArray = new byte[length + 1];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(lengthOfOverall);
        buff.put(version);
        buff.put(statusCode);
        buff.put(messageTag);
        buff.put(a);
        buff.put(encodingTag);
        buff.put(b);
        buff.put(size);
        buff.put(typeTag);
        buff.put(protocolEncoding);
        buff.put(sizeTag);
        buff.put(symbolLength);
        buff.put(preferredRoleTag);
        buff.put(preferredRole);

        return buff.array();
    }

    private static byte[] encodingSecret(String code) {
        byte[] version = new byte[] {8, 2};  // the protocol version 2
        byte[] statusCode = new byte[] {16, (byte) 200, 1};  // Status OK
        byte[] a = new byte[] {(byte) 194, 2, 34, 10};  // ???
        byte[] size = new byte[] {32};  // the size of the encoded secret
        byte[] message = computeAlphaValue(code);

        int length = version.length + statusCode.length + a.length + size.length + message.length;
        byte[] lengthOfOverall = new byte[] {(byte) length};  // the length of total

        // prepare the payload byte array
        byte[] allByteArray = new byte[length + 1];
        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
        buff.put(lengthOfOverall);
        buff.put(version);
        buff.put(statusCode);
        buff.put(a);
        buff.put(size);
        buff.put(message);

        return buff.array();
    }

    private static byte[] computeAlphaValue(String s) {
        PublicKey publicKey = clientCert.getPublicKey();
        PublicKey publicKey2 = serverCert.getPublicKey();

        if (!(publicKey instanceof RSAPublicKey)|| !(publicKey2 instanceof RSAPublicKey)) {
            Log.e(TAG, "Expecting RSA public key");
            return null;
        }

        RSAPublicKey rSAPublicKey = (RSAPublicKey) publicKey;  // client public key
        RSAPublicKey rSAPublicKey2 = (RSAPublicKey) publicKey2;  // server public key

        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");

            byte[] byteArray = rSAPublicKey.getModulus().abs().toByteArray();  // client modulus
            byte[] byteArray2 = rSAPublicKey.getPublicExponent().abs().toByteArray();  // client exponent
            byte[] byteArray3 = rSAPublicKey2.getModulus().abs().toByteArray();  // server modulus
            byte[] byteArray4 = rSAPublicKey2.getPublicExponent().abs().toByteArray();  // server exponent
            byte[] code = new BigInteger(s.substring(2),16).toByteArray();  // nonce
            byte[] removeLeadingNullBytes = removeLeadingNullBytes(byteArray);
            byte[] removeLeadingNullBytes2 = removeLeadingNullBytes(byteArray2);
            byte[] removeLeadingNullBytes3 = removeLeadingNullBytes(byteArray3);
            byte[] removeLeadingNullBytes4 = removeLeadingNullBytes(byteArray4);
            code = removeLeadingNullBytes(code);

            instance.update(removeLeadingNullBytes);
            instance.update(removeLeadingNullBytes2);
            instance.update(removeLeadingNullBytes3);
            instance.update(removeLeadingNullBytes4);
            instance.update(code);
            return instance.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "no sha-256 implementation");
            throw new RuntimeException(e);
        }
    }

    private static byte[] removeLeadingNullBytes(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; ++i) {
            if (byteArray[i] != 0) {
                return Arrays.copyOfRange(byteArray, i, byteArray.length);
            }
        }
        return null;
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

    private static SSLSocketFactory getSocketFactory(final String caCrtFile, final String crtFile, final String keyFile,
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
            serverCert = caCert;

            /**
             * Load client certificate
             */
            reader = new PEMParser(new FileReader(crtFile));
            X509CertificateHolder certHolder = (X509CertificateHolder) reader.readObject();
            reader.close();

            //build the certificate
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                            .generateCertificate(new ByteArrayInputStream(certHolder.getEncoded()));
            clientCert = cert;
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
}