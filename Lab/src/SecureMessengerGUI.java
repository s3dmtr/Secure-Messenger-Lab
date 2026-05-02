import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureMessengerGUI extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;

    private SecretKey secretKey;
    private byte[] ivBytes;
    private byte[] ciphertext;

    public SecureMessengerGUI() {
        setTitle("Secure Messenger Lab - V2 GUI");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeCrypto();
        buildUI();
    }

    private void initializeCrypto() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            secretKey = keyGenerator.generateKey();

            ivBytes = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(ivBytes);
        } catch (Exception e) {
            showError("Crypto initialization failed: " + e.getMessage());
        }
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Secure Messenger Lab", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitle = new JLabel("Version 2 — GUI AES Encryption Demo", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(title);
        headerPanel.add(subtitle);

        inputArea = new JTextArea(5, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setText("Hello Bob, this is a secure message from Alice.");

        outputArea = new JTextArea(15, 40);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JButton encryptButton = new JButton("Encrypt Message");
        JButton decryptButton = new JButton("Decrypt Message");
        JButton clearButton = new JButton("Clear Output");

        encryptButton.addActionListener(e -> encryptMessage());
        decryptButton.addActionListener(e -> decryptMessage());
        clearButton.addActionListener(e -> outputArea.setText(""));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(clearButton);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Message Input:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(new JLabel("Output:"), BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(outputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void encryptMessage() {
        try {
            String plaintext = inputArea.getText();

            if (plaintext.trim().isEmpty()) {
                showError("Please enter a message first.");
                return;
            }

            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            ciphertext = encryptCipher.doFinal(plaintext.getBytes());

            outputArea.setText(
                    "===== Encryption Result =====\n\n" +
                    "Plaintext:\n" + plaintext + "\n\n" +
                    "AES Key (Base64):\n" + Base64.getEncoder().encodeToString(secretKey.getEncoded()) + "\n\n" +
                    "IV (Base64):\n" + Base64.getEncoder().encodeToString(ivBytes) + "\n\n" +
                    "Ciphertext (Base64):\n" + Base64.getEncoder().encodeToString(ciphertext) + "\n\n" +
                    "Observation:\n" +
                    "The plaintext message was encrypted into unreadable ciphertext."
            );

        } catch (Exception e) {
            showError("Encryption failed: " + e.getMessage());
        }
    }

    private void decryptMessage() {
        try {
            if (ciphertext == null) {
                showError("Please encrypt a message before decrypting.");
                return;
            }

            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decryptedBytes = decryptCipher.doFinal(ciphertext);
            String decryptedMessage = new String(decryptedBytes);

            outputArea.append(
                    "\n\n===== Decryption Result =====\n\n" +
                    "Decrypted Message:\n" + decryptedMessage + "\n\n" +
                    "Observation:\n" +
                    "The decrypted message matches the original plaintext."
            );

        } catch (Exception e) {
            showError("Decryption failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SecureMessengerGUI app = new SecureMessengerGUI();
            app.setVisible(true);
        });
    }
}
