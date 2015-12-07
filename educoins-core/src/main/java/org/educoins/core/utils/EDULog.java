package org.educoins.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EDULog {
    private static Logger logger = LogManager.getLogger("EDUCoins EDULog");

    public static void logError(String msg) {
        logger.error(msg);
    }

    public static void logDebug(String msg) {
        logger.debug(msg);
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }
}
