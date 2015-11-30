package org.educoins.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.educoins.core.Block;
import org.educoins.core.Gate;
import org.educoins.core.Input;
import org.educoins.core.Input.EInputUnlockingScript;
import org.educoins.core.Output;
import org.educoins.core.Transaction;
import org.educoins.core.store.BlockStoreException;
import org.educoins.core.store.IBlockStore;
import org.educoins.core.store.LevelDbBlockStore;
import org.educoins.core.utils.IO.EPath;

/**
 * A factory for easier testing concerning the {@link IBlockStore}.
 * Created by typus on 11/12/15.
 */
public class BlockStoreFactory {
    public static IBlockStore getBlockStore() throws BlockStoreException {
        return new LevelDbBlockStore(IO.getDefaultFileLocation(EPath.TMP, EPath.EDUCoinsBlockStore));
    }

    public static IBlockStore getRandomlyFilledBlockStore() throws BlockStoreException {
        return new LevelDbBlockStore(IO.getDefaultFileLocation(EPath.TMP, EPath.EDUCoinsBlockStore));
    }

    public static void fillRandom(IBlockStore store, int filled) {
        for (int i = 0; i < filled; i++) {
            store.put(getRandomBlock());
        }
    }

    public static void fillRandomTree(IBlockStore store, int filled) {
        Block previous = getRandomBlock();
        for (int i = 0; i < filled; i++) {
            previous = getRandomBlockChain(previous);
            store.put(previous);
        }
    }
    
    public static Block getRandomBlockChain(Block block) {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits(Sha256Hash.wrap(ByteArray.convertFromInt((int) (Math.random() * Integer.MAX_VALUE))));
        String random =  Generator.getSecureRandomString256HEX();
        toReturn.setHashMerkleRoot(Sha256Hash.wrap(random));
        toReturn.setHashPrevBlock(block.hash());
        return toReturn;
    }

    public static Block getRandomBlock() {
        Block toReturn = new Block();
        toReturn.setVersion((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setNonce((int) (Math.random() * Integer.MAX_VALUE));
        toReturn.setBits(Sha256Hash.wrap(ByteArray.convertFromInt((int) (Math.random() * Integer.MAX_VALUE))));
        String random =  Generator.getSecureRandomString256HEX();
        toReturn.setHashMerkleRoot(Sha256Hash.wrap(random));
        return toReturn;
    }
    
    public static Gate generateExternSignedGate(){
    	
    	//Create a gate
    	Gate gate = new Gate();
    	String publicKey = MockedWallet.getPublicKey();
    	String signature = MockedWallet.getSignature(publicKey, gate.getMessage());
    	gate.setPublicKey(publicKey);
    	gate.setSignature(signature);
    	
    	//Simulation of letting extern person sign. 
    	byte[] concatedMessage = gate.getConcatedGate();
    	publicKey = MockedWallet.getPublicKey();
    	signature = MockedWallet.getSignature(publicKey, ByteArray.convertToString(concatedMessage));	
    	gate.externSignature(signature);
    	
    	return gate;
    }
    
    public static Transaction generateTransaction(int number) {
        Transaction t = new Transaction();
        for (int i = 0; i < 2 * number; i++) {
            Input input = new Input(5 * i * number, Sha256Hash.wrap(""), i);
            input.setUnlockingScript(Input.EInputUnlockingScript.PUBLIC_KEY, "12345");
            t.addInput(input);
        }
        for (int i = 0; i < 4 * number; i++) {
            t.addOutput(new Output(5 * i * number, Sha256Hash.wrap(""), Sha256Hash.wrap("123456")));
        }
        t.setApprovals(null);
        return t;
    }
    
    public static List<Transaction> getConnectedTransactions(){
    	
    	String publicKey = MockedWallet.getPublicKey();
    	String lockingScript = MockedWallet.getPublicKey();
    	
    	Transaction txPrev = generateTransaction(1);
    	
    	Transaction txNew = new Transaction();
    	List<Transaction> listTx = new ArrayList<Transaction>();
    	
    	Output outPrev = txPrev.getOutputs().get(0);
    	Sha256Hash bytePrevOut = Sha256Hash.wrap(outPrev.getConcatedOutput());
    
    	Input inNew = new Input(3, bytePrevOut, 0);
    	inNew.setUnlockingScript(EInputUnlockingScript.PUBLIC_KEY, lockingScript);
    	Output outNew = new Output(2, Sha256Hash.wrap(publicKey), Sha256Hash.wrap(lockingScript));
    	
    	txNew.addInput(inNew);
    	txNew.addOutput(outNew);
    	
    	listTx.add(txPrev);
    	listTx.add(txNew);
    	
    	return listTx;
    }
    
}
