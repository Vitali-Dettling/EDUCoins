package org.educoins.core.presentation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * Created by typus on 1/14/16.
 */
public class ArgsWalletInitializerView implements IWalletInitializerView {
    public static final String ARG_WALLET = "--wallet";
    private final List<String> args;

    public ArgsWalletInitializerView(List<String> args) {
        this.args = args;
    }

    @Override
    public Path getWalletFile(Path defaultLocation) throws IllegalArgumentException {
        Iterator<String> iterator = args.iterator();
        boolean next = false;
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (value.equals(ARG_WALLET)) {
                next = true;
            } else if (next) {
                return Paths.get(value);
            }
        }
        throw new IllegalArgumentException("No " + ARG_WALLET + " argument!");
    }
}
