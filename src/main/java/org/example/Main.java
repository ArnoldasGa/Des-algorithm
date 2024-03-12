package org.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Encryption");
        System.out.println("2. Decryption");
        System.out.print("Pasirinkite: ");
        int operationChoice = scanner.nextInt();
        scanner.nextLine();

        if (operationChoice == 1) {
            uzkoduoti(scanner);
        } else if (operationChoice == 2) {
            atkoduoti(scanner);
        } else {
            System.out.println("Neteisingas pasirinkimas");
        }
    }

    private static void uzkoduoti(Scanner scanner) throws Exception {
        System.out.print("Iveskite teksta: ");
        String tekstas = scanner.nextLine();

        SecretKey slaptasRaktas = getKey(scanner);

        Cipher cipher = getMode(scanner);
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[8];
        random.nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        if (cipher.getAlgorithm().contains("CBC") || cipher.getAlgorithm().contains("CFB")) {
            cipher.init(Cipher.ENCRYPT_MODE, slaptasRaktas, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, slaptasRaktas);
        }

        byte[] encryptedBytes = cipher.doFinal(tekstas.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

        System.out.println("Kaip norite issaugoti: ");
        System.out.println("1. Tik atspausdinti.");
        System.out.println("2. Tik issaugoti i faila");
        System.out.println("3. Atspausdinti ir issaugoti i faila");
        int issaugojimas = scanner.nextInt();
        scanner.nextLine();
        String filename;
        BufferedWriter IV;
        String ivBase64;
        switch (issaugojimas) {
            case 1 :
                System.out.println(encryptedText);
                ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
                System.out.println("IV kodas: " + ivBase64);
                break;
            case 2 :
                System.out.print("Iveskite failo pavadinima: ");
                filename = scanner.nextLine();
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                writer.write(encryptedText);
                writer.close();
                IV = new BufferedWriter(new FileWriter(filename+"IV"));
                ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
                IV.write(ivBase64);
                IV.close();
                System.out.println("Issaugota i " + filename);
                break;
            case 3:
                System.out.print("Iveskite failo pavadinima: ");
                filename = scanner.nextLine();
                BufferedWriter writer1 = new BufferedWriter(new FileWriter(filename));
                writer1.write(encryptedText);
                writer1.close();
                IV = new BufferedWriter(new FileWriter(filename+"IV"));
                ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
                IV.write(ivBase64);
                IV.close();
                System.out.println("Issaugota i " + filename);
                System.out.println(encryptedText);
                break;
            default:
                System.out.println("Nera tokio pasirinkimo tik atspausdinama");
        }

    }

    private static void atkoduoti(Scanner scanner) throws Exception {
        System.out.println("1. Atsifruoti is failo");
        System.out.println("2. Atsifruoti is consoles");
        int atsifravimas = scanner.nextInt();
        scanner.nextLine();
        String encryptedText;
        String ivBase64;
        byte[] ivBytes;
        IvParameterSpec ivSpec;
        switch (atsifravimas) {
            case 1:
                System.out.print("Iveskite failo pavadinima: ");
                String filename = scanner.nextLine();
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                encryptedText = reader.readLine();
                reader.close();
                BufferedReader IV = new BufferedReader(new FileReader(filename+"IV"));
                ivBase64 = IV.readLine();
                IV.close();
                ivBytes = Base64.getDecoder().decode(ivBase64);
                ivSpec = new IvParameterSpec(ivBytes);
                break;
            case 2:
                System.out.print("Iveskite teksta: ");
                encryptedText = scanner.nextLine();
                System.out.print("Enter IV (8 bytes in base64): ");
                ivBase64 = scanner.nextLine();
                ivBytes = Base64.getDecoder().decode(ivBase64);
                ivSpec = new IvParameterSpec(ivBytes);
                break;
            default:
                System.out.println("Nera tokio pasirinkimo");
                System.out.print("Iveskite teksta: ");
                encryptedText = scanner.nextLine();
                System.out.print("Enter IV (8 bytes in base64): ");
                ivBase64 = scanner.nextLine();
                ivBytes = Base64.getDecoder().decode(ivBase64);
                ivSpec = new IvParameterSpec(ivBytes);
        }

        SecretKey slaptasRaktas = getKey(scanner);
        Cipher cipher = getMode(scanner);

        if (cipher.getAlgorithm().contains("CBC") || cipher.getAlgorithm().contains("CFB")) {
            cipher.init(Cipher.DECRYPT_MODE, slaptasRaktas, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, slaptasRaktas);
        }
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        String decryptedText = new String(decryptedBytes);
        System.out.println("Gautas tekstas: " + decryptedText);
    }

    private static SecretKey getKey(Scanner scanner) throws Exception {
        System.out.print("Iveskite rakta: ");
        String raktas = scanner.nextLine();
        byte[] raktoData = raktas.getBytes();
        DESKeySpec desKeySpec = new DESKeySpec(raktoData);
        SecretKeyFactory raktoFactory = SecretKeyFactory.getInstance("DES");
        return raktoFactory.generateSecret(desKeySpec);
    }
    private static Cipher getMode(Scanner scanner) throws Exception {
        System.out.println("Pasirinkite moda:");
        System.out.println("1. ECB (Electronic Codebook)");
        System.out.println("2. CBC (Cipher Block Chaining)");
        System.out.println("3. CFB (Cipher Feedback)");
        System.out.println("4. OFB (Output Feedback)");
        System.out.println("5. CTR (Counter)");
        System.out.print("Iveskite numeri: ");
        int pasrinkimas = scanner.nextInt();
        scanner.nextLine();
        return switch (pasrinkimas) {
            case 1 -> Cipher.getInstance("DES/ECB/PKCS5Padding");
            case 2 -> Cipher.getInstance("DES/CBC/PKCS5Padding");
            case 3 -> Cipher.getInstance("DES/CFB/PKCS5Padding");
            case 4 -> Cipher.getInstance("DES/OFB/PKCS5Padding");
            case 5 -> Cipher.getInstance("DES/CTR/PKCS5Padding");
            default -> {
                System.out.println("Neteisingas pasirinkimas naudojamas ECB.");
                yield Cipher.getInstance("DES/ECB/PKCS5Padding");
            }
        };
    }
}