package org.educoins.core.presentation;

import org.educoins.core.Wallet;

import java.net.URI;
import java.nio.file.Path;

/**
 * Created by typus on 1/14/16.
 */
public interface IWalletInitializerView {
    /**
     * Asks the user where to store the {@link Wallet} file. This file needs to be created. It is just an {@link URI} so
     * far.
     *
     * @param defaultLocation The default location.
     * @return The URI representing the File the user wants to store the wallet in.
     * @throws IllegalArgumentException if user interaction failed.
     */
    Path getWalletFile(Path defaultLocation) throws IllegalArgumentException;
}
