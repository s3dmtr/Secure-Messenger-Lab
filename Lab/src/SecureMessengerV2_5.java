import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureMessengerV2_5 extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;

    private SecretKey secretKey;
    private byte[] ivBytes;
    private byte[] ciphertext;

    public SecureMessengerV2_5() {
        setTitle("Secure Messenger Lab - V2.5 Attack Simulation");
        setSize(850, 600);
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

        JLabel subtitle = new JLabel("Version 2.5 — GUI Attack Simulation", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(title);
        headerPanel.add(subtitle);

        inputArea = new JTextArea(6, 35);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setText("Hello Bob, this is a secure message from Alice.");

        outputArea = new JTextArea(18, 45);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JButton encryptButton = new JButton("Encrypt Message");
        JButton decryptButton = new JButton("Decrypt Message");
        JButton tamperButton = new JButton("Tamper Attack");
        JButton replayButton = new JButton("Replay Attack");
        JButton clearButton = new JButton("Clear Output");

        encryptButton.addActionListener(e -> encryptMessage());
        decryptButton.addActionListener(e -> decryptMessage());
        tamperButton.addActionListener(e -> simulateTamperingAttack());
        replayButton.addActionListener(e -> simulateReplayAttack());
        clearButton.addActionListener(e -> outputArea.setText(""));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(tamperButton);
        buttonPanel.add(replayButton);
        buttonPanel.add(clearButton);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Message Input:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(new JLabel("Security Lab Output:"), BorderLayout.NORTH);
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

    private void simulateTamperingAttack() {
        try {
            if (ciphertext == null) {
                showError("Please encrypt a message before simulating an attack.");
                return;
            }

            byte[] tamperedCiphertext = ciphertext.clone();

            // Flip one bit in the ciphertext to simulate modification
            tamperedCiphertext[0] = (byte) (tamperedCiphertext[0] ^ 1);

            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            outputArea.append(
                    "\n\n===== Tampering Attack Simulation =====\n\n" +
                    "Attack Action:\n" +
                    "One bit of the ciphertext was modified by the attacker.\n\n" +
                    "Tampered Ciphertext (Base64):\n" +
                    Base64.getEncoder().encodeToString(tamperedCiphertext) + "\n\n"
            );

            try {
                Cipher tamperDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                tamperDecryptCipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

                byte[] tamperedDecryptedBytes = tamperDecryptCipher.doFinal(tamperedCiphertext);
                String tamperedMessage = new String(tamperedDecryptedBytes);

                outputArea.append(
                        "Tampered Decrypted Message:\n" +
                        tamperedMessage + "\n\n" +
                        "Observation:\n" +
                        "The system decrypted the modified ciphertext, but the message became corrupted.\n" +
                        "This shows that AES-CBC provides confidentiality, but it does not guarantee integrity."
                );

            } catch (Exception e) {
                outputArea.append(
                        "Result:\n" +
                        "Decryption failed after tampering.\n\n" +
                        "Observation:\n" +
                        "Even when decryption fails, AES-CBC alone does not provide a clean integrity verification mechanism.\n" +
                        "A secure design should use HMAC or an authenticated encryption mode such as AES-GCM."
                );
            }

        } catch (Exception e) {
            showError("Tampering simulation failed: " + e.getMessage());
        }
    }

    private void simulateReplayAttack() {
        try {
            if (ciphertext == null) {
                showError("Please encrypt a message before simulating an attack.");
                return;
            }

            byte[] replayedCiphertext = ciphertext.clone();

            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher replayDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            replayDecryptCipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] replayedDecryptedBytes = replayDecryptCipher.doFinal(replayedCiphertext);
            String replayedMessage = new String(replayedDecryptedBytes);

            outputArea.append(
                    "\n\n===== Replay Attack Simulation =====\n\n" +
                    "Attack Action:\n" +
                    "The attacker resends the same ciphertext again.\n\n" +
                    "Replayed Message:\n" +
                    replayedMessage + "\n\n" +
                    "Observation:\n" +
                    "The system accepted the replayed message because there is no timestamp, nonce tracking, or message ID check.\n" +
                    "This shows that encrypted messages still need freshness validation."
            );

        } catch (Exception e) {
            showError("Replay simulation failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SecureMessengerV2_5 app = new SecureMessengerV2_5();
            app.setVisible(true);
        });
    }
}
