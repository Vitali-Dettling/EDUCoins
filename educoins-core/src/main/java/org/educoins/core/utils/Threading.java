package org.educoins.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Util class to spawn Threads. Using {@link ExecutorService} with a FixedThreadPool.
 * Created by typus on 10/27/15.
 */
public class Threading {

    private final static int IOThreads = 2;

    private static int availableCores = Runtime.getRuntime().availableProcessors() + IOThreads;
    private static ExecutorService executor = Executors.newFixedThreadPool(availableCores);


    public static void run(Runnable runnable) {
        executor.execute(runnable);
    }

}
