import java.util.Base64;

public class AESEncryption {
    private static final String ENCRYPTION_KEY = "my-secret-key";

    public static String encrypt(String message) {
        byte[] keyBytes = ENCRYPTION_KEY.getBytes();
        byte[] messageBytes = message.getBytes();
        byte[] encrypted = new byte[messageBytes.length];

        for (int i = 0; i < messageBytes.length; i++) {
            encrypted[i] = (byte) (messageBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedMessage) {
        try {
            byte[] keyBytes = ENCRYPTION_KEY.getBytes();
            byte[] messageBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decrypted = new byte[messageBytes.length];

            for (int i = 0; i < messageBytes.length; i++) {
                decrypted[i] = (byte) (messageBytes[i] ^ keyBytes[i % keyBytes.length]);
            }

            return new String(decrypted);
        } catch (Exception e) {
            return "[❌ Invalid encrypted message]";
        }
    }
}
