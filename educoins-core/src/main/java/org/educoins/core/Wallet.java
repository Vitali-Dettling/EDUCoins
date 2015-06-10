package org.educoins.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.IO;

public class Wallet {

	private static final int HEX = 16;
	private static final String SEPERATOR = ";";
	private static final String KeyStorageFile = "/wallet.keys";
	
	private ECDSA keyPair;
	private Path directoryKeyStorage;
	
	public Wallet(){
		
		try {
			
			this.directoryKeyStorage = Paths.get(System.getProperty("user.home") + File.separator + "documents" + File.separator
					+ "educoins" + File.separator + "demo" + File.separator + "wallet");
		
			IO.deleteDirectory(directoryKeyStorage);
			IO.createDirectory(directoryKeyStorage);
			IO.createFile(this.directoryKeyStorage  + KeyStorageFile);
		
		} catch (IOException e) {
			System.err.println("ERROR: Class Wallet Constructor!!!!");
			e.printStackTrace();
		}
	}
	
	//TODO [Vitali] Bad performance because the whole file will be checked over and over again. Will be better with the DB.
	public boolean checkSignature(String hashedTranscation, byte[] signature){
		
		try {
			
			List<String> publicKeys = this.getPublicKeys();
			for (String publicKey : publicKeys) {	
				boolean rightKey = this.keyPair.verifySignature(hashedTranscation, signature, publicKey);
				if (rightKey) {
					return true;
				}
			}

		} catch (Exception e) {
			System.err.println("ERROR: Class Wallet. Verification of the Signature." + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public String getSignature(String publicKey, String hashedTranscation){
		
		try {
			
			byte[] signature = this.keyPair.getSignature(publicKey, hashedTranscation);
			return ByteArray.convertToString(signature, HEX);
		} catch (Exception e) {
			System.err.println("ERROR: Class Wallet. Creating of the Signature.");
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * At first each new private key and public key will be stored in the ".keys" file.
	 * before the public key will be returned. Moreover, the public key will be returned
	 * as string value, because it is really just a random number.
	 * 
	 * @return Each call of the method will create a new set of private and public key set!
	 * */
	//TODO[Vitali] Right now the private public key is stored in a file but this can change, maybe???
	public String getPublicKey() {
		
		this.keyPair = new ECDSA();
		
		String privateKey = this.keyPair.getPrivateKey();
		String publicKey = this.keyPair.getPublicKey();
		
		try {
			IO.appendToFile(this.directoryKeyStorage + KeyStorageFile, privateKey + SEPERATOR + publicKey + "\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return publicKey;
		
	}
	
	public List<String> getPublicKeys() throws IOException {
		List<String> publicKeys = new ArrayList<>();
		String keyFile = IO.readFromFile(this.directoryKeyStorage + KeyStorageFile);
		BufferedReader reader = new BufferedReader(new StringReader(keyFile));
		String line;
		while ((line = reader.readLine()) != null) {
			String publicKey = line.substring(line.indexOf(";") + 1);
			publicKeys.add(publicKey);
		}
		
		return publicKeys;
	}
	
	/**
	 * Nested calls that just the wallet class can use it. 
	 * This is important because only the wallet is managing keys and verifications etc.
	 * 
	 * @information What is a private/public key and signature.
	 * 
	 *              Book [Mastering Bitcoin]: P63
	 * 
	 *              The private key is just a number. You can pick your private keys
	 *              randomly using just a coin, pencil, and paper: toss a coin 256
	 *              times and you have the binary digits of a random private key you
	 *              can use in a wallet. The public key can then be generated from
	 *              the private key.
	 * */
	private class ECDSA {

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
		 * @throws NoSuchAlgorithmException
		 *             If e.g. the ECDSA does not exist.
		 * 
		 * */
		public ECDSA() {

			try {

				this.signature = Signature.getInstance(SHA256_WITH_ECDSA);
				this.keyPairGenerator = KeyPairGenerator.getInstance(ECDSA);
						
				this.keyPairGenerator.initialize(ADDRESS_SPACE_256, new SecureRandom());
				this.keyPair = this.keyPairGenerator.generateKeyPair();
				
			
			} catch (NoSuchAlgorithmException e) {
				System.out.println("Class ECDSA: Constructor: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		/**
		 * Returns the public key as string value, because it is really just a
		 * random number.
		 * 
		 * */
		public String getPublicKey() {	
			return ByteArray.convertToString(this.keyPair.getPublic().getEncoded(), HEX);
		}

		/**
		 * Returns the private key as string value, because it is really just a
		 * random number.
		 * 
		 * */
		public String getPrivateKey() {
			return ByteArray.convertToString(this.keyPair.getPrivate().getEncoded(), HEX);
		}

		/**
		 * Verify the signature with a hashed transaction.
		 * 
		 * @warning For verification purpose one need to initial the signature each time.
		 * 
		 * @throws Exception
		 *             [Class ECDSA] The signature transaction hash value cannot be null.
		 * 
		 * */
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
		 * 
		 * @warning For verification purpose one need to initial the signature each time.
		 * */
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
			
			String keyFile = IO.readFromFile(directoryKeyStorage + KeyStorageFile);
			BufferedReader reader = new BufferedReader(new StringReader(keyFile));
			String line;
			String privateKey = null;
			while ((line = reader.readLine()) != null) {
				String publicKeyStore = line.substring(line.indexOf(";") + 1);
				
				if(publicKeyStore.equals(publicKey)){
					privateKey = line.substring(0, line.indexOf(";"));
					break;
				}	
			}
			
	
			return privateKey;
		}
		
		//TODO[Vitali] Adub this to the ECDSA class is preaty goot... -> When Time
//		public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
//		    byte[] clear = ByteArray.convertFromString(key64, 16);
//		    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
//		    KeyFactory fact = KeyFactory.getInstance("DSA");
//		    PrivateKey priv = fact.generatePrivate(keySpec);
//		    Arrays.fill(clear, (byte) 0);
//		    return priv;
//		}
//
//
//		public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
//		    byte[] data = ByteArray.convertFromString(stored, 16);
//		    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
//		    KeyFactory fact = KeyFactory.getInstance("DSA");
//		    return fact.generatePublic(spec);
//		}
//
//		public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
//		    KeyFactory fact = KeyFactory.getInstance("DSA");
//		    PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
//		            PKCS8EncodedKeySpec.class);
//		    byte[] packed = spec.getEncoded();
//		    String key64 = ByteArray.convertToString(packed, 16);
//
//		    Arrays.fill(packed, (byte) 0);
//		    return key64;
//		}
//
//
//		public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
//		    KeyFactory fact = KeyFactory.getInstance("DSA");
//		    X509EncodedKeySpec spec = fact.getKeySpec(publ,
//		            X509EncodedKeySpec.class);
//		    return ByteArray.convertToString(spec.getEncoded(), 16);
//		}
//		
//		
//		public static void main(String[] args) throws Exception{
//
//			
//		
//			 KeyPairGenerator gen = KeyPairGenerator.getInstance("DSA");
//		    KeyPair pair = gen.generateKeyPair();
//
//		    String pubKey = savePublicKey(pair.getPublic());
//		    PublicKey pubSaved = loadPublicKey(pubKey);
//		    System.out.println(pair.getPublic()+"\n"+pubSaved);
//
//		    String privKey = savePrivateKey(pair.getPrivate());
//		    PrivateKey privSaved = loadPrivateKey(privKey);
//		    System.out.println(pair.getPrivate()+"\n"+privSaved);
//			
//			
//			    
//			   System.out.println(); 
//			
//
//		}
		
		
		
	
	}
	
}
