package com.example.authserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * A utility class for hashing and encoding data.
 *
 */
public class HashUtil {

    private static final Logger logger = LoggerFactory.getLogger(HashUtil.class);
    private static final String SHA_256_ALGORITHM = "SHA-256";

    private HashUtil() {
    }

    /**
     * Generates a SHA-256 hash of the input string and then Base64-encodes the hash.
     *
     * @param data The string to be hashed and encoded.
     * @return A Base64-encoded string representing the SHA-256 hash of the input data.
     * @throws RuntimeException If the SHA-256 algorithm is not available in the JRE.
     */
    public static String generateSha256Base64Encoded(String data) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256_ALGORITHM);
            md.update(data.getBytes());
            byte[] digest = md.digest();
            return Base64.getEncoder().encodeToString(digest);

        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to get SHA-256 algorithm", e);
            throw e;
        }
    }
}
