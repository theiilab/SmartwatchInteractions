package yuanren.tvsamrtwatch.smartwatchinteractions.network.pairing;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import yuanren.tvsamrtwatch.smartwatchinteractions.network.certificate.CertificateGenerator;

public class PairingManager {
    public static final String TAG = "PairingManager";
    public static final int SERVER_PAIR_PORT = 6467;  // port for pairing

    private static final String SERVER_IP = "10.0.0.4";
//    private static final String SERVER_IP = "192.168.0.111";
//    private static final String SERVER_IP = "192.168.0.19";

    private SSLSocket pairingSocket;
    private OutputStream pairingOutputStream;
    private InputStream pairingInputStream;
    private CertificateGenerator generator;

    public PairingManager(Context context){
        generator = new CertificateGenerator(context);
    }

    public void stopSSLPairingConnection() {
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
    public void createSSLPairingConnection(Context context) {
        try {
            SSLSocketFactory socketFactory = generator.getSocketFactory();
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

    private void sendPair(byte[] payload) {
        if (pairingOutputStream != null) {
            try {
                pairingOutputStream.write(payload);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private byte[] receivePair() {
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

    public void startPairing(String code) {
        byte[] payload = encodingSecret(code);
        sendPair(payload);
        receivePair();
    }


    private byte[] pairing () {
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

    private byte[] optioning() {
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

    private byte[] configuring () {
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

    private byte[] encodingSecret(String code) {
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

    private byte[] computeAlphaValue(String s) {
        PublicKey publicKey = generator.getClientCert().getPublicKey();
        PublicKey publicKey2 = generator.getServerCert().getPublicKey();

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

    private byte[] removeLeadingNullBytes(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; ++i) {
            if (byteArray[i] != 0) {
                return Arrays.copyOfRange(byteArray, i, byteArray.length);
            }
        }
        return null;
    }
}
