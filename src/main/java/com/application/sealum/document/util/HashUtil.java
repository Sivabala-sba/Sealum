package com.application.sealum.document.util;

import java.io.InputStream;
import java.security.MessageDigest;

public class HashUtil {

    public static String sha256(InputStream inputStream) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;

        while((bytesRead = inputStream.read(buffer)) != -1){
            digest.update(buffer, 0, bytesRead);
        }

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();

        for(byte b : hashBytes){
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

}
