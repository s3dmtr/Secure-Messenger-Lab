// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SecureMessagingV1 {
   public SecureMessagingV1() {
   }

   public static void main(String[] var0) throws Exception {
      System.out.println("===== Version 1: Basic AES Messaging =====");
      KeyGenerator var1 = KeyGenerator.getInstance("AES");
      var1.init(128);
      SecretKey var2 = var1.generateKey();
      String var3 = "Hello Bob, this is a secure message from Alice.";
      byte[] var4 = new byte[16];
      SecureRandom var5 = new SecureRandom();
      var5.nextBytes(var4);
      IvParameterSpec var6 = new IvParameterSpec(var4);
      Cipher var7 = Cipher.getInstance("AES/CBC/PKCS5Padding");
      var7.init(1, var2, var6);
      byte[] var8 = var7.doFinal(var3.getBytes());
      Cipher var9 = Cipher.getInstance("AES/CBC/PKCS5Padding");
      var9.init(2, var2, var6);
      byte[] var10 = var9.doFinal(var8);
      String var11 = new String(var10);
      System.out.println("\nPlaintext Message:");
      System.out.println(var3);
      System.out.println("\nAES Key (Base64):");
      System.out.println(Base64.getEncoder().encodeToString(var2.getEncoded()));
      System.out.println("\nIV (Base64):");
      System.out.println(Base64.getEncoder().encodeToString(var4));
      System.out.println("\nCiphertext (Base64):");
      System.out.println(Base64.getEncoder().encodeToString(var8));
      System.out.println("\nDecrypted Message:");
      System.out.println(var11);
      System.out.println("\n===== Tampering Attack Simulation =====");
      byte[] var12 = (byte[])(([B)var8).clone();
      var12[0] = (byte)(var12[0] ^ 1);
      System.out.println("Tampered Ciphertext (Base64):");
      System.out.println(Base64.getEncoder().encodeToString(var12));

      try {
         Cipher var13 = Cipher.getInstance("AES/CBC/PKCS5Padding");
         var13.init(2, var2, var6);
         byte[] var14 = var13.doFinal(var12);
         String var15 = new String(var14);
         System.out.println("\nTampered Decrypted Message:");
         System.out.println(var15);
         System.out.println("\nObservation:");
         System.out.println("The message was changed, and AES-CBC alone does not guarantee message integrity.");
      } catch (Exception var17) {
         System.out.println("\nTampering detected or decryption failed.");
         System.out.println("Error: " + var17.getMessage());
      }

      System.out.println("\n===== Replay Attack Simulation =====");
      byte[] var18 = (byte[])(([B)var8).clone();
      Cipher var19 = Cipher.getInstance("AES/CBC/PKCS5Padding");
      var19.init(2, var2, var6);
      byte[] var20 = var19.doFinal(var18);
      String var16 = new String(var20);
      System.out.println("The attacker resends the same ciphertext.");
      System.out.println("\nReplayed Message:");
      System.out.println(var16);
      System.out.println("\nObservation:");
      System.out.println("Replay attack succeeded because there is no timestamp, nonce tracking, or message ID check.");
   }
}
