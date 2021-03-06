package org.educoins.core.config;

import org.educoins.core.utils.Sha256Hash;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Makes resources/application.properties accessible.
 * Created by typus on 12/1/15.
 */
public class AppConfig {

    public static Properties prop = new Properties();
    private static AppConfig config = new AppConfig();

    public AppConfig() {
        InputStream inputStream = null;
        try {

            String propFileName = "application.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Returns the port configured of the running peer server.
     *
     * @return the port as int.
     */
    public static int getOwnPort() {
        return config.getServerPort();
    }

    public static String getCentralUrl() {
        return prop.getProperty("educoins.discovery.central.url");
    }

    public static Sha256Hash getOwnPublicKey() {
        return Sha256Hash.wrap(prop.getProperty("educoins.peer.pubkey"));
    }

    public static int getServerPort() {
        return Integer.parseInt(prop.getProperty("server.port"));
    }

    public static int getMaxDiscoveryRetries() {
        return Integer.parseInt(prop.getProperty("educoins.discovery.retries.max"));
    }

}
