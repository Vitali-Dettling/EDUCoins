package org.educoins.core.p2p.peers.server.exceptions;

import org.educoins.core.BlockChain;
import org.educoins.core.utils.Sha256Hash;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An {@link IllegalArgumentException} thrown whenever the {@link BlockChain} discovers conflicts while validation.
 * Created by typus on 11/30/15.
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Block could not be verified.")
public class BlockChainVerificationException extends IllegalArgumentException {

    private Sha256Hash hash;

    public BlockChainVerificationException(Sha256Hash hash) {
        this.hash = hash;
    }

    public BlockChainVerificationException() {
    }

    public BlockChainVerificationException(String s) {
        super(s);
    }

    public BlockChainVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockChainVerificationException(Throwable cause) {
        super(cause);
    }
}
