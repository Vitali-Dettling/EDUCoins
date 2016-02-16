

package org.educoins.core.utils;

import org.educoins.core.config.AppConfig;

/**
 * Used for unit testing to inject values into the config, if the test does not use spring.
 * Created by typus on 1/18/16.
 */
public class AppConfigInitializer {

    /**
     * Inits the config, do this only in unit tests, where you are not using spring.
     */
    public static void init() {
        AppConfig config = new AppConfig();
        config.setCentralUrl("http://localhost:1337/");

        config.setDiscoveryMaxRetries("5");
        config.setDiscoverRatingDecreaseValue("2");
        config.setDiscoveryRatingIncreaseValue("1");
        config.setDiscoveryRatingDefault("5");
        config.setOwnPubKey("affeaffeaffeaffeaffeaffeaffeaffeaffeaffeaffeaffeaffeaffeaffeaffe");
        AppConfig.setInner(config);
    }

}


