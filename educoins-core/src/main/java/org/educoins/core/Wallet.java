package org.educoins.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.educoins.core.utils.ByteArray;
import org.educoins.core.utils.IO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.ec.ECKeyFactory;

public class Wallet {

	private static final int HEX = 16;
	private static final String SEPERATOR = ";";
	private static final String KeyStorageFile = "/wallet.keys";
	
	private ECDSA keyPair;
//	private PrintWriter walletKeysStorage; 
	private Path directoryKeyStorage;
	
	public Wallet(){
		
		try {
			
			this.directoryKeyStorage = Paths.get(System.getProperty("user.home") + File.separator + "documents" + File.separator
					+ "educoins" + File.separator + "demo" + File.separator + "wallet");
		
			IO.deleteDirectory(directoryKeyStorage);
			IO.createDirectory(directoryKeyStorage);
			IO.createFile(this.directoryKeyStorage  + KeyStorageFile);
			
//			this.walletKeysStorage = new PrintWriter(this.directoryKeyStorage + KeyStorageFile);
		
		} catch (IOException e) {
			System.err.println("ERROR: Class Wallet Constructor!!!!");
			e.printStackTrace();
		}
	}
	
	//TODO [Vitali] Bad performance because the whole file will be checked over and over again. Will be better with the DB.
	public boolean checkSignature(String hashedTranscation, byte[] signature){
		
		try {
			
//			String fullKeyStorage = IO.readFromFile(this.directoryKeyStorage + KeyStorageFile);
//
//			for(String oneKey : fullKeyStorage.split(SEPERATOR)){
//				boolean rightKey = this.keyPair.verifySignature(hashedTranscation, signature, oneKey);
//				if(rightKey){
//					return true; 	
//				}
//			}
			
			List<String> publicKeys = this.getPublicKeys();
			for (String publicKey : publicKeys) {
				boolean rightKey = this.keyPair.verifySignature(hashedTranscation, signature, publicKey);
				if (rightKey) {
					return true;
				}
			}

		} catch (Exception e) {
			System.err.println("ERROR: Class Wallet. Verification of the Signature.");
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public String getSignature(String hashedTranscation){
		
		try {
				
			return ByteArray.convertToString(this.keyPair.getSignature(hashedTranscation));
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

//		this.walletKeysStorage.println(privateKey + SEPERATOR + publicKey);
//		this.walletKeysStorage.println();
//		this.walletKeysStorage.flush();

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
		private KeyFactory keyFactory;

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
				
				this.keyFactory = KeyFactory.getInstance(ECDSA);
				
			
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
//			byte[] decodedPublicKexString = new BASE64Decoder().decodeBuffer(publicKey);
//			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedPublicKexString);
//			PublicKey orgPublicKey = this.keyFactory.generatePublic(publicKeySpec);
//
//			this.signature.initVerify(orgPublicKey);
//			this.signature.update(message.getBytes());
			
			byte[] encodedPublicKey = ByteArray.convertFromString(publicKey, 16);
			KeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
			PublicKey orgPublicKey = this.keyFactory.generatePublic(publicKeySpec);
			
			this.signature.initVerify(orgPublicKey);
			this.signature.update(ByteArray.convertFromString(message, 16));
			
			return this.signature.verify(signature);
		}

		/**
		 * Create a Signature object and initial it with a message.
		 * 
		 * @throws Exception Does not contain any message.
		 * 
		 * @warning For verification purpose one need to initial the signature each time.
		 * */
		public byte[] getSignature(String message) throws Exception {
			
			if (message == null) {
				throw new Exception("EXCEPTION: [Class ECDSA] The signature transaction hash value cannot be null.");
			}

			this.signature.initSign(this.keyPair.getPrivate());
			this.signature.update(message.getBytes());
			
			return this.signature.sign();
		}
	
	}
	
}
