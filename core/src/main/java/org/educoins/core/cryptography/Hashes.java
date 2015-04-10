package org.educoins.core.cryptography;

import org.educoins.core.cryptography.Scrypt;

public enum Hashes { //Verstehe nicht genau was die Klasse so macht????
		
    /**
     * Use Java implementation of SHA256 (code 2)
     */
    SHA256((byte)2) {
        public byte[] hash(byte[] input) {
            return SHA.sha256().digest(input);
        }
    },
    /**
     * Use Java implementation of Scrypt
     */
    SCRYPT((byte)5) {
        public byte[] hash(byte[] input) {
            return threadLocalScrypt.get().hash(input);
        }
    };
    
    
    private static final ThreadLocal<Scrypt> threadLocalScrypt = new ThreadLocal<Scrypt>() {
        @Override 
        	protected Scrypt initialValue() {
            return new Scrypt();
        }
    };

    private final byte id;
    
    Hashes(byte id) {
        this.id = id;
    }
    
    
/* Brauche ich das???
 
   public static Hashes getHashFunction(byte id) {
        for (Hashes function : values()) {
            if (function.id == id) {
                return function;
            }
        }
        throw new IllegalArgumentException(String.format("illegal algorithm %d", id));
    }

    public byte getId() {
        return id;
    }
        
    public abstract byte[] hash(byte[] input);*/
   

}
