package org.educoins.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.stream.Stream;

import org.educoins.core.utils.ByteArray;


public class Wallet implements IBlockListener {

	private static final int HEX = 16;
	private static final String UTF_8 = "UTF-8";
	private static final String KeyStorageFile = "/wallet.keys";
	
	private IBlockReceiver blockReceiver;
	private IBlockTransmitter blockTransmitter;
	private ECDSA keyPair;
	private Path walletDirectory;
	private PrintWriter walletKeysStorage;
	
	public Wallet(IBlockReceiver blockReceiver, IBlockTransmitter blockTransmitter){
		
		try {
		
			this.blockReceiver = blockReceiver;				
			this.blockTransmitter = blockTransmitter;
			
			this.blockReceiver.addBlockListener(this);
			
			this.walletDirectory = Paths.get(System.getProperty("user.home") + File.separator + "documents" + File.separator
					+ "educoins" + File.separator + "demo" + File.separator + "wallet");
		
			this.walletKeysStorage = createNewDirectory();
			
		} catch (IOException e) {
			System.err.println("ERROR: Class Wallet Constructor!!!!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void blockReceived(Block block) {
		
		if(Verifier.verifyBlock(block)){
			
			//TODO[Vitali] Hier Implementierung wo und wie die BlockChain lokal gespeichert werden soll. 
			//P.s. Die jetztige Implemtierung ist nur zum veranschaulichen.
			this.blockTransmitter.transmitBlock(block);
			
			
		}
		else
		{
			System.err.println("INFO: Block was rejected!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Class Wallet");
			
		}
		
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

		this.walletKeysStorage.println(privateKey + publicKey);
		this.walletKeysStorage.println();
		this.walletKeysStorage.flush();

		return publicKey;
		
	}
	
	
	private PrintWriter createNewDirectory() throws IOException{
		
		if (Files.exists(this.walletDirectory)) {
			Stream<Path> localFiles = Files.list(this.walletDirectory);
			for (Object file : localFiles.toArray()) {
				Files.delete((Path) file);
			}
			localFiles.close();
		}
		
		if (Files.exists(this.walletDirectory) && !Files.isDirectory(this.walletDirectory)) {
			throw new IllegalArgumentException(this.walletDirectory.toString() + " is not a directory");
		}

		if (!Files.exists(this.walletDirectory)) {
			Files.createDirectories(this.walletDirectory);
		}
		
		return new PrintWriter(this.walletDirectory.toString() + KeyStorageFile, UTF_8);
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
				// TODO Auto-generated catch block
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
		 * Initialize the signature it with the a key.
		 * 
		 * @warning For verification purpose one need to initial the signature each
		 *          time.
		 * 
		 * @throws Exception
		 *             [Class ECDSA] The signature transaction hash value cannot be
		 *             null.
		 * 
		 * */
		public void initSignature(String hashedTranscation) throws Exception {

			if (hashedTranscation == null) {
				throw new Exception("EXCEPTION: [Class ECDSA] The signature transaction hash value cannot be null.");
			}

			this.signature.initSign(this.keyPair.getPrivate());
			this.signature.update(hashedTranscation.getBytes());
		}

		/**
		 * Verify the signature with a hashed transaction.
		 * 
		 * @warning For verification purpose one need to initial the signature each
		 *          time.
		 * 
		 * @throws Exception
		 *             [Class ECDSA] The signature transaction hash value cannot be
		 *             null.
		 * 
		 * */
		public boolean verifySignature(String hashedTranscation, byte[] signature) throws Exception {

			if (hashedTranscation == null) {
				throw new Exception("EXCEPTION: [Class ECDSA] The signature transaction hash value cannot be null.");
			}

			this.signature.initVerify(this.keyPair.getPublic());
			this.signature.update(hashedTranscation.getBytes());

			return this.signature.verify(signature);
		}

		/**
		 * Create a Signature object.
		 * 
		 * @warning For verification purpose one need to initial the signature each
		 *          time.
		 * 
		 * @throws SignatureException
		 *             Please note the warning.
		 * */
		public byte[] getSignature() throws SignatureException {
			return this.signature.sign();
		}

	}
	
	
	
}
