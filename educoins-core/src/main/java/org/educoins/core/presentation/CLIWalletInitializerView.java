package org.educoins.core.presentation;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * The {@link IWalletInitializerView} implementation for CLIs.
 * Created by typus on 1/14/16.
 */
public class CLIWalletInitializerView implements IWalletInitializerView {

    @Override
    public Path getWalletFile(Path defaultLocation) throws IllegalArgumentException {
        System.out.println("------------------------");
        System.out.println("| Welcome to EDUCoins! |");
        System.out.println("------------------------");

        Scanner scanner = new Scanner(System.in);
        if (readDecision(scanner)) {
            return defaultLocation;
        } else {
            return readPath(scanner);
        }
    }


    private boolean readDecision(final Scanner scanner) {
        System.out.println("Do you want to use the default location for your wallet? (y/n)");
        System.out.flush();
        String input = scanner.nextLine().trim().toLowerCase();
        switch (input) {
            case "n":
                return false;
            case "y":
                return true;
            default:
                System.out.println("Invalid input! Please enter 'y'(yes) or 'n'(no)!");
                return readDecision(scanner);
        }
    }

    private Path readPath(final Scanner scanner) {
        System.out.println("Please enter the file you want to use:");
        System.out.flush();
        String fileInput = scanner.nextLine().trim();
        try {
            return Paths.get(URI.create("file://" + fileInput));
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.out.println("Not a valid path!");
            return readPath(scanner);
        }
    }

}
