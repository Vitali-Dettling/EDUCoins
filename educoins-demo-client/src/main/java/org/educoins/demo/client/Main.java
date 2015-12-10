package org.educoins.demo.client;

import org.educoins.core.Block;
import org.educoins.core.p2p.peers.remote.HttpProxy;

import java.io.IOException;
import java.net.URI;

/**
 * Created by typus on 12/10/15.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No TargetUri given! Exiting...");
            System.exit(0);
        }

        HttpProxy proxy = new HttpProxy(URI.create("http://" + args[0]), "test");

        Block prev = new Block();
        long run = 0;
        while (run++ < 100) {
            try {
                System.out.println("SENDING...");
                prev = BlockStoreFactory.getRandomBlock(prev);
                proxy.transmitBlock(prev);
                System.out.println("SLEEPING...");
                Thread.sleep(60 * 1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
