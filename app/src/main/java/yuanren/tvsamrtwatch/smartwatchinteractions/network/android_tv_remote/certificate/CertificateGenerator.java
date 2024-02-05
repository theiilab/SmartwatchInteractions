package yuanren.tvsamrtwatch.smartwatchinteractions.network.android_tv_remote.certificate;

import android.content.Context;
import android.util.Log;

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
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import yuanren.tvsamrtwatch.smartwatchinteractions.BuildConfig;

public class CertificateGenerator {
    public static final String TAG = "CertificateGenerator";
    public X509Certificate serverCert;
    public X509Certificate clientCert;
    private Path path;

    private Context context;

    public CertificateGenerator(Context context) {
        this.context = context;
    }


    public SSLSocketFactory getSocketFactory() {
        generateCertificate();

        try {

            // Create an SSLSocket and connect to the server
            String caCrtFile = path.resolve("server.pem").toString();  // Run this command in terminal: openssl s_client -showcerts -connect 10.0.0.4:6467 > ~/Desktop/server.pem, and drag the server.pem under device external storage/data/data/YOUR_APP/files/
            String crtFile = path.resolve("client.pem").toString();
            String keyFile = path.resolve("private.pem").toString();
            String password = "";

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

            /**
             * build the certificate
             */
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
            SSLContext sslContext = SSLContext.getInstance("TLS"); // TLSv1.2
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            /**
             * Return the newly created socket factory object
             */
            return sslContext.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void generateCertificate() {
        path = Paths.get(context.getFilesDir().getAbsolutePath());

        try {
            /**
             *  Avoid regenerating if the certificate already existed
             */
            if (Files.exists(path.resolve("client.pem")) && Files.exists(path.resolve("private.pem"))) {
                Log.d(TAG, "Certificate already existed.");
                return;
            }
            SelfSignedCertificate certificate = new SelfSignedCertificate(BuildConfig.APPLICATION_ID);
            Log.d(TAG, "Certificate generated successfully!");

            /**
             * Export the certificate to PEM file
             */
            try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(path.resolve("client.pem").toFile()))) {
                writer.writeObject(certificate.cert()); // public key
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * Export the certificate to PEM file
             */
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

    public X509Certificate getServerCert() {
        return serverCert;
    }

    public X509Certificate getClientCert() {
        return clientCert;
    }
}
