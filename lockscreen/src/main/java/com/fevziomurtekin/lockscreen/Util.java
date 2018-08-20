package com.fevziomurtekin.lockscreen;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import static android.content.Context.FINGERPRINT_SERVICE;

public class Util {

    private static final String KEY_ALIAS = "FINGERPRINT_KEY_PAIR_ALIAS";
    private static final String KEY_STORE = "AndroidKeyStore";

    private static KeyStore sKeyStore;
    private static KeyPairGenerator sKeyPairGenerator;
    private static Cipher sCipher;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkSensorState(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
        if (fingerprintManager.isHardwareDetected()) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure() && fingerprintManager.hasEnrolledFingerprints();
        } else return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    public static String encryptString(String string) {
        try {
            if (initKeyStore() && initCipher() && initKey() && initCipherMode(Cipher.ENCRYPT_MODE)) {
                byte[] bytes = sCipher.doFinal(string.getBytes());
                return Base64.encodeToString(bytes, Base64.NO_WRAP);
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static String decryptString(String string, Cipher cipher) {
        try {
            byte[] bytes = Base64.decode(string, Base64.NO_WRAP);
            return new String(cipher.doFinal(bytes));
        } catch (IllegalBlockSizeException | BadPaddingException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static boolean initKeyStore() {
        try {
            sKeyStore = KeyStore.getInstance(KEY_STORE);
            sKeyStore.load(null);
            return true;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean initCipher() {
        try {
            sCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            return true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean initCipherMode(int mode) {
        try {
            sKeyStore.load(null);
            switch (mode) {
                case Cipher.ENCRYPT_MODE:
                    PublicKey key = sKeyStore.getCertificate(KEY_ALIAS).getPublicKey();
                    PublicKey unrestricted = KeyFactory.getInstance(key.getAlgorithm()).generatePublic(new X509EncodedKeySpec(key.getEncoded()));
                    OAEPParameterSpec spec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
                    sCipher.init(mode, unrestricted, spec);
                    break;

                case Cipher.DECRYPT_MODE:
                    PrivateKey privateKey = (PrivateKey) sKeyStore.getKey(KEY_ALIAS, null);
                    sCipher.init(mode, privateKey);
                    break;
                default:
                    return false;
            }
            return true;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException  | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean initKey() {
        try {
            return sKeyStore.containsAlias(KEY_ALIAS) || generateNewKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean generateNewKey() {
        if (initKeyGenerator()) {
            try {
                sKeyPairGenerator.initialize(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .setUserAuthenticationRequired(true)
                        .build());
                sKeyPairGenerator.generateKeyPair();
                return true;
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static boolean initKeyGenerator() {
        try {
            sKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE);
            return true;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    public static FingerprintManager.CryptoObject getCryptoObject() {
        if (initKeyStore() && initCipher() && initKey() && initCipherMode(Cipher.DECRYPT_MODE)) {
            return new FingerprintManager.CryptoObject(sCipher);
        }
        return null;
    }

}
