package org.educoins.core.config;

import org.educoins.core.utils.IO;
import org.educoins.core.utils.Sha256Hash;

import java.io.*;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Makes resources/application.properties accessible.
 * Created by typus on 12/1/15.
 */
public class AppConfig {

    public static Properties prop = new Properties();
    private static String inetAddress;

    static {
        InputStream inputStream = null;
        try {

            String propFileName = "application.properties";

            inputStream = AppConfig.class.getClassLoader().getResourceAsStream(propFileName);

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

    public static void setInetAddress(String ip) {
        inetAddress = ip;
    }

    public static URI getOwnAddress(String protocol) throws UnknownHostException {
        if (inetAddress == null)
            throw new UnknownHostException("IP not yet set!");
        return URI.create(protocol + inetAddress + ":" + AppConfig.getOwnPort());
    }

    /**
     * Returns the port configured of the running peer server.
     *
     * @return the port as int.
     */
    public static int getOwnPort() {
        return AppConfig.getServerPort();
    }

    public static String getCentralUrl() {
        return prop.getProperty("educoins.discovery.central.url");
    }

    public static Sha256Hash getOwnPublicKey() {
        String publicKey = prop.getProperty("educoins.peer.pubkey");
        return Sha256Hash.wrap(publicKey);
    }

    public static int getServerPort() {
        return Integer.parseInt(prop.getProperty("server.port"));
    }

    public static int getMaxDiscoveryRetries() {
        return Integer.parseInt(prop.getProperty("educoins.discovery.retries.max"));
    }

    public static double getRatingIncreaseValue() {
        return Double.parseDouble(prop.getProperty("educoins.discovery.rating.increaseValue"));
    }

    public static double getRatingDecreaseValue() {
        return Double.parseDouble(prop.getProperty("educoins.discovery.rating.decreaseValue"));
    }

    public static double getDefaultRanking() {
        return Double.parseDouble(prop.getProperty("educoins.discovery.rating.default"));
    }

    public static File getBlockStoreDirectory() throws IOException {
        String path = prop.getProperty("educoins.blockstore.directory");
        if (path != null && !path.equals("")) {
            return new File(path);
        } else {
            return IO.createTmpDir("blocks-" + getOwnPublicKey().toString());
        }
    }
}
