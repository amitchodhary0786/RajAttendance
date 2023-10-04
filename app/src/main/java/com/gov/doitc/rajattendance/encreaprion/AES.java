package com.gov.doitc.rajattendance.encreaprion;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static String ALGORITHM = "AES";
    private static String ENCODING = "UTF-8";


    private static byte[] getKeyBytes(String key) throws
            UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] parameterKeyBytes = key.getBytes(ENCODING);
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
                Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String plainText, String secretKey)
    {
        try
        {
            byte[] keyBytes = getKeyBytes(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes,
                    ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec,
                    ivParameterSpec);

            return
                    Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(ENCODING)));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
}