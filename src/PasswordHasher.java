import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    // Algorithm used to hash the password
    protected final static String ALGORITHM = "SHA-256";

    // Method to hash a password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(PasswordHasher.ALGORITHM);
            byte[] encodedHash = digest.digest(password.getBytes());
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Convert bytes to hex string
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}