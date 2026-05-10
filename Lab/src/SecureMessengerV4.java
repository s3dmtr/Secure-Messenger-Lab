import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.AEADBadTagException;
import javax.crypto.spec.GCMParameterSpec;
import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureMessengerV4 extends JFrame {

    private JTextArea aliceMessageArea;
    private JTextArea networkArea;
    private JTextArea bobInboxArea;
    private JLabel statusLabel;

    private SecretKey secretKey;
    private byte[] nonceBytes;
    private byte[] encryptedMessage;

    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_NONCE_LENGTH = 12;

    public SecureMessengerV4() {
        setTitle("Secure Messenger Lab - V4 Messaging Simulation");
        setSize(1000, 650);
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

            generateNewNonce();

        } catch (Exception e) {
            showError("Crypto initialization failed: " + e.getMessage());
        }
    }

    private void generateNewNonce() {
        nonceBytes = new byte[GCM_NONCE_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(nonceBytes);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Secure Messenger Lab", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Version 4 — Alice to Bob Secure Messaging Simulation", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(title);
        headerPanel.add(subtitle);

        aliceMessageArea = new JTextArea(8, 25);
        aliceMessageArea.setLineWrap(true);
        aliceMessageArea.setWrapStyleWord(true);
        aliceMessageArea.setText("Hello Bob, this is a protected message from Alice.");

        networkArea = new JTextArea(8, 30);
        networkArea.setEditable(false);
        networkArea.setLineWrap(true);
        networkArea.setWrapStyleWord(true);

        bobInboxArea = new JTextArea(8, 25);
        bobInboxArea.setEditable(false);
        bobInboxArea.setLineWrap(true);
        bobInboxArea.setWrapStyleWord(true);

        JPanel alicePanel = createBoxPanel("Alice - Sender", aliceMessageArea);
        JPanel networkPanel = createBoxPanel("Network Channel", networkArea);
        JPanel bobPanel = createBoxPanel("Bob - Receiver", bobInboxArea);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.add(alicePanel);
        centerPanel.add(networkPanel);
        centerPanel.add(bobPanel);

        JButton sendButton = new JButton("Send Encrypted Message");
        JButton tamperButton = new JButton("Tamper Message");
        JButton replayButton = new JButton("Replay Message");
        JButton clearButton = new JButton("Clear");

        sendButton.addActionListener(e -> sendEncryptedMessage());
        tamperButton.addActionListener(e -> tamperMessage());
        replayButton.addActionListener(e -> replayMessage());
        clearButton.addActionListener(e -> clearAll());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(sendButton);
        buttonPanel.add(tamperButton);
        buttonPanel.add(replayButton);
        buttonPanel.add(clearButton);

        statusLabel = new JLabel("Status: Waiting for Alice to send a message.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createBoxPanel(String title, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private void sendEncryptedMessage() {
        try {
            String plaintext = aliceMessageArea.getText();

            if (plaintext.trim().isEmpty()) {
                showError("Alice must write a message first.");
                return;
            }

            generateNewNonce();

            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonceBytes);

            Cipher encryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            encryptedMessage = encryptCipher.doFinal(plaintext.getBytes());

            networkArea.setText(
                    "Encrypted Message Sent\n\n" +
                    "Ciphertext + Tag (Base64):\n" +
                    Base64.getEncoder().encodeToString(encryptedMessage) + "\n\n" +
                    "Nonce (Base64):\n" +
                    Base64.getEncoder().encodeToString(nonceBytes)
            );

            String decryptedMessage = decrypt(encryptedMessage);

            bobInboxArea.setText(
                    "Message Received Successfully\n\n" +
                    "Decrypted Message:\n" +
                    decryptedMessage
            );

            statusLabel.setText("Status: Message encrypted, delivered, and verified successfully.");

        } catch (Exception e) {
            showError("Sending message failed: " + e.getMessage());
        }
    }

    private void tamperMessage() {
        try {
            if (encryptedMessage == null) {
                showError("Send a message before running tamper test.");
                return;
            }

            byte[] tamperedMessage = encryptedMessage.clone();
            tamperedMessage[0] = (byte) (tamperedMessage[0] ^ 1);

            networkArea.setText(
                    "Tampered Message Detected in Network\n\n" +
                    "Modified Ciphertext + Tag (Base64):\n" +
                    Base64.getEncoder().encodeToString(tamperedMessage)
            );

            try {
                String decryptedMessage = decrypt(tamperedMessage);

                bobInboxArea.setText(
                        "Unexpected Result\n\n" +
                        "Tampered message was decrypted:\n" +
                        decryptedMessage
                );

                statusLabel.setText("Status: Unexpected behavior — tampered message was accepted.");

            } catch (AEADBadTagException e) {
                bobInboxArea.setText(
                        "Message Rejected\n\n" +
                        "Reason:\n" +
                        "Tampering detected. Authentication tag verification failed.\n\n" +
                        "Security Result:\n" +
                        "AES-GCM protected Bob from receiving a modified message."
                );

                statusLabel.setText("Status: Tampering detected and message rejected.");
            }

        } catch (Exception e) {
            showError("Tamper test failed: " + e.getMessage());
        }
    }

    private void replayMessage() {
        try {
            if (encryptedMessage == null) {
                showError("Send a message before running replay test.");
                return;
            }

            byte[] replayedMessage = encryptedMessage.clone();
            String decryptedMessage = decrypt(replayedMessage);

            networkArea.setText(
                    "Replay Attempt\n\n" +
                    "The same valid ciphertext was resent.\n\n" +
                    "Ciphertext + Tag (Base64):\n" +
                    Base64.getEncoder().encodeToString(replayedMessage)
            );

            bobInboxArea.setText(
                    "Replayed Message Accepted\n\n" +
                    "Decrypted Message:\n" +
                    decryptedMessage + "\n\n" +
                    "Limitation:\n" +
                    "AES-GCM verifies integrity, but it does not prevent replay attacks by itself."
            );

            statusLabel.setText("Status: Replay accepted — system needs timestamps, message IDs, or nonce tracking.");

        } catch (Exception e) {
            showError("Replay test failed: " + e.getMessage());
        }
    }

    private String decrypt(byte[] encryptedData) throws Exception {
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonceBytes);

        Cipher decryptCipher = Cipher.getInstance("AES/GCM/NoPadding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        byte[] decryptedBytes = decryptCipher.doFinal(encryptedData);
        return new String(decryptedBytes);
    }

    private void clearAll() {
        networkArea.setText("");
        bobInboxArea.setText("");
        statusLabel.setText("Status: Waiting for Alice to send a message.");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SecureMessengerV4 app = new SecureMessengerV4();
            app.setVisible(true);
        });
    }
}
