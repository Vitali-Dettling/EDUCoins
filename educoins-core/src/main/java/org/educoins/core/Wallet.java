package org.educoins.core;

import org.educoins.core.presentation.*;
import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public class Wallet {

    private static final int HEX = 16;
    private static final String KEY_SEPERATOR = ";";
    private static final Logger logger = LoggerFactory.getLogger(Wallet.class);
    private static IWalletInitializerView initializerView = new CLIWalletInitializerView();
    private static String keyStorageFile = "/wallet.keys";
    private static Path directoryKeyStorage = Paths.get(System.getProperty("user.home") + File.separator + "documents" + File.separator
            + "educoins" + File.separator + "demo" + File.separator + "wallet");


    public static void initialize(String[] args) throws IOException {
        List<String> argsList = Arrays.asList(args);
        if (argsList.contains(ArgsWalletInitializerView.ARG_WALLET)) {
            initializerView = new ArgsWalletInitializerView(argsList);
        } else {
            initializerView = new CLIWalletInitializerView();
        }

        directoryKeyStorage = initializerView.getWalletFile(directoryKeyStorage);

        File pathFile = directoryKeyStorage.toFile();
        if (pathFile.isFile()) {
            keyStorageFile = pathFile.getName();
            directoryKeyStorage = Paths.get(pathFile.getParent());
        }

        IO.createDirectory(directoryKeyStorage);
        IO.createFile(directoryKeyStorage + keyStorageFile);
    }


    public static boolean compare(String message, String signature, String publicKey) {

        ECDSA keyPair = new ECDSA();
        try {

            byte[] sign = ByteArray.convertFromString(signature, HEX);
            if (keyPair.verifySignature(message, sign, publicKey)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Verification of the Signature.");
        }

        return false;
    }

    //TODO Bad performance because the whole file will be checked over and over again. Will be better with the DB.
    public static boolean checkSignature(String hashedTranscation, String signature) {

        try {
            ECDSA keyPair = new ECDSA();

            List<String> publicKeys = getPublicKeys();
            byte[] sign = ByteArray.convertFromString(signature, HEX);
            for (String publicKey : publicKeys) {
                boolean rightKey = keyPair.verifySignature(hashedTranscation, sign, publicKey);
                if (rightKey) {
                    return true;
                }
            }

        } catch (Exception e) {
            logger.error("Verification of the Signature." + e.getMessage());
        }
        return false;
    }


    public static String getSignature(String publicKey, String hashedTranscation) {

        try {
            ECDSA keyPair = new ECDSA();

            byte[] signature = keyPair.getSignature(publicKey, hashedTranscation);
            return ByteArray.convertToString(signature, HEX);
        } catch (Exception e) {
            logger.error("Creating of the Signature." + e.getMessage());
        }
        return null;
    }

    /**
     * At first each new private key and public key will be stored in the ".keys" file.
     * before the public key will be returned. Moreover, the public key will be returned
     * as string value, because it is really just a random number.
     *
     * @return Each call of the method will create a new set of private and public key set!
     */
    //TODO Right now the private public key is stored in a file but this can change, maybe???
    public static String getPublicKey() {

        ECDSA keyPair = new ECDSA();

        String privateKey = keyPair.getPrivateKey();
        String publicKey = keyPair.getPublicKey();

        try {
            IO.appendToFile(directoryKeyStorage + keyStorageFile, privateKey + KEY_SEPERATOR + publicKey + "\r\n");
        } catch (IOException e) {
            logger.error("A public key could not be created.");
        }

        return publicKey;

    }

    public static List<String> getPublicKeys() {

        List<String> publicKeys = new ArrayList<>();
        try {
            String keyFile;
            keyFile = IO.readFromFile(directoryKeyStorage + keyStorageFile);

            BufferedReader reader = new BufferedReader(new StringReader(keyFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String publicKey = line.substring(line.indexOf(";") + 1);
                publicKeys.add(publicKey);
            }
        } catch (IOException e) {
            logger.error("Public keys could not be retrieved.");
        }
        return publicKeys;
    }


    /**
     * Nested calls that just the wallet class can use it.
     * This is important because only the wallet is managing keys and verifications etc.
     *
     * @information What is a private/public key and signature.
     * <p>
     * Book [Mastering Bitcoin]: P63
     * <p>
     * The private key is just a number. You can pick your private keys
     * randomly using just a coin, pencil, and paper: toss a coin 256
     * times and you have the binary digits of a random private key you
     * can use in a wallet. The public key can then be generated from
     * the private key.
     */
    private static class ECDSA {

        private static final String ECDSA = "EC";
        private static final String SHA256_WITH_ECDSA = "SHA256withECDSA";
        private static final int ADDRESS_SPACE_256 = 256;

        private KeyPairGenerator keyPairGenerator;
        private KeyPair keyPair;
        private Signature signature;


        /**
         * Public/private key verification for the Elliptic Curve Digital Signature
         * Algorithm (ECDSA).
         *
         * @throws NoSuchAlgorithmException If e.g. the ECDSA does not exist.
         */
        public ECDSA() {

            try {

                this.signature = Signature.getInstance(SHA256_WITH_ECDSA);
                this.keyPairGenerator = KeyPairGenerator.getInstance(ECDSA);

                this.keyPairGenerator.initialize(ADDRESS_SPACE_256, new SecureRandom());
                this.keyPair = this.keyPairGenerator.generateKeyPair();


            } catch (NoSuchAlgorithmException e) {
                System.out.println("Class ECDSA: Constructor: " + e.getMessage());
            }
        }

        /**
         * Returns the public key as string value, because it is really just a
         * random number.
         */
        public String getPublicKey() {
            return ByteArray.convertToString(this.keyPair.getPublic().getEncoded(), HEX);
        }

        /**
         * Returns the private key as string value, because it is really just a
         * random number.
         */
        public String getPrivateKey() {
            return ByteArray.convertToString(this.keyPair.getPrivate().getEncoded(), HEX);
        }

        /**
         * Verify the signature with a hashed transaction.
         *
         * @throws Exception [Class ECDSA] The signature transaction hash value cannot be null.
         * @warning For verification purpose one need to initial the signature each time.
         */
        public boolean verifySignature(String message, byte[] signature, String publicKey) throws Exception {

            if (message == null || signature == null) {
                throw new Exception("EXCEPTION: [Class ECDSA] The signature or the transaction hash value cannot be null.");
            }

//			byte[] encodedPublicKey = ByteArray.convertFromString(publicKey, 16);
//			KeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
//			KeyFactory keyFactory = KeyFactory.getInstance(ECDSA);
//			PublicKey orgPublicKey = keyFactory.generatePublic(publicKeySpec);

            byte[] encodedPublicKey = ByteArray.convertFromString(publicKey);
            KeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ECDSA);
            PublicKey orgPublicKey = keyFactory.generatePublic(publicKeySpec);

            this.signature.initVerify(orgPublicKey);
            this.signature.update(ByteArray.convertFromString(message));

            return this.signature.verify(signature);
        }

        /**
         * Create a Signature object and initial it with a message.
         *
         * @throws Exception Does not contain any message.
         * @warning For verification purpose one need to initial the signature each time.
         */
        public byte[] getSignature(String publicKey, String message) throws Exception {

            if (message == null) {
                throw new Exception("EXCEPTION: [Class ECDSA] The signature transaction hash value cannot be null.");
            }

            byte[] privateKey = ByteArray.convertFromString(getPrivateKey(publicKey));

            // Only ECPrivateKeySpec and PKCS8EncodedKeySpec supported for EC private keys
            KeyFactory keyFactory = KeyFactory.getInstance(ECDSA);
            KeySpec PrivateKeySpec = new PKCS8EncodedKeySpec(privateKey);

            PrivateKey orgPrivateKey = keyFactory.generatePrivate(PrivateKeySpec);

            this.signature.initSign(orgPrivateKey);
            this.signature.update(ByteArray.convertFromString(message));

            return this.signature.sign();
        }

        public String getPrivateKey(String publicKey) throws IOException {

            String keyFile = IO.readFromFile(directoryKeyStorage + keyStorageFile);
            BufferedReader reader = new BufferedReader(new StringReader(keyFile));
            String line;
            String privateKey = null;
            while ((line = reader.readLine()) != null) {
                String publicKeyStore = line.substring(line.indexOf(";") + 1);

                if (publicKeyStore.equals(publicKey)) {
                    privateKey = line.substring(0, line.indexOf(";"));
                    break;
                }
            }


            return privateKey;
        }
    }
}
