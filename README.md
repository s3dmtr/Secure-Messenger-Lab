# 🔐 Secure Messenger Lab

A hands-on cybersecurity project that demonstrates how encrypted communication works, how it can be attacked, and how it can be improved.

---

## 🧠 Overview

This project started as a university assignment focused on AES encryption.  
It later evolved into a personal lab to explore real-world security concepts.

The main idea behind this project is:

> Encryption alone does not guarantee security.

Through this project, I demonstrate:
- How messages are encrypted using AES
- How attackers can manipulate encrypted data
- Why integrity and replay protection are critical

---

## 🚀 Project Versions

### 🟢 Version 1 — University CLI Version
- AES encryption and decryption (CBC mode)
- Random key and IV generation
- Message tampering simulation
- Replay attack simulation
- Command-line output for demonstration

---

### 🔵 Version 2 — Portfolio GUI Version
- Java Swing graphical interface
- User message input
- Encrypt and decrypt buttons
- Clean and interactive demonstration
- Designed for portfolio and LinkedIn showcase

---

## ⚙️ Technologies Used

- Java
- Java Swing
- AES (CBC Mode)
- SecureRandom
- Base64 Encoding

---

## 📌 Key Takeaways

- AES provides **confidentiality**, but not **integrity**
- Encrypted messages can still be modified without detection
- Replay attacks are possible without freshness validation
- A secure system must include:
  - Integrity protection (HMAC / AEAD)
  - Authentication
  - Replay protection mechanisms

---

## 📂 Project Structure
