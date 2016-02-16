package org.educoins.core.config;

import org.educoins.core.utils.IO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * Makes resources/application.properties accessible.
 * Created by typus on 12/1/15.
 */
@Component
public class AppConfig {
    private static AppConfig inner;
    private static String inetAddress;

    @Value("${server.port}")
    private int ownPort;

    @Value("${educoins.peer.pubkey}")
    private String ownPubKey;

    @Value("${educoins.discovery.central.url}")
    private String centralUrl;

    @Value("${educoins.discovery.retries.max}")
    private String discoveryMaxRetries;

    @Value("${educoins.discovery.rating.increaseValue}")
    private String discoveryRatingIncreaseValue;

    @Value("${educoins.discovery.rating.decreaseValue}")
    private String discoverRatingDecreaseValue;

    @Value("${educoins.discovery.rating.default}")
    private String discoveryRatingDefault;

    //    @Value("${educoins.blockstore.directory}")
    private String blockStoreDirectory;

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

    public void setOwnPort(int ownPort) {
        this.ownPort = ownPort;
    }

    public static String getCentralUrl() {
        return inner.centralUrl;
    }

    public void setCentralUrl(String centralUrl) {
        this.centralUrl = centralUrl;
    }

    public static String getOwnPublicKey() {
        return inner.ownPubKey;
    }

    public static int getServerPort() {
        return inner.ownPort;
    }

    public static int getMaxDiscoveryRetries() {
        return Integer.parseInt(inner.discoveryMaxRetries);
    }

    public static double getRatingIncreaseValue() {
        return Double.parseDouble(inner.discoveryRatingIncreaseValue);
    }

    public static double getRatingDecreaseValue() {
        return Double.parseDouble(inner.discoverRatingDecreaseValue);
    }

    public static double getDefaultRanking() {
        return Double.parseDouble(inner.discoveryRatingDefault);
    }

    public static File getBlockStoreDirectory() throws IOException {
        String path = inner.blockStoreDirectory;
        if (path != null && !path.equals("")) {
            return new File(path);
        } else {
            return IO.createTmpDir("blocks-" + getOwnPublicKey().toString());
        }
    }

    public void setBlockStoreDirectory(String blockStoreDirectory) {
        this.blockStoreDirectory = blockStoreDirectory;
    }

    public static AppConfig getInner() {
        return inner;
    }

    public static void setInner(AppConfig inner) {
        AppConfig.inner = inner;
    }

    @PostConstruct
    void inject() {
        AppConfig.inner = this;
    }

    //region inner getter/setter
    public String getOwnPubKey() {
        return ownPubKey;
    }

    public void setOwnPubKey(String ownPubKey) {
        this.ownPubKey = ownPubKey;
    }

    public String getDiscoveryMaxRetries() {
        return discoveryMaxRetries;
    }

    public void setDiscoveryMaxRetries(String discoveryMaxRetries) {
        this.discoveryMaxRetries = discoveryMaxRetries;
    }

    public String getDiscoveryRatingIncreaseValue() {
        return discoveryRatingIncreaseValue;
    }

    public void setDiscoveryRatingIncreaseValue(String discoveryRatingIncreaseValue) {
        this.discoveryRatingIncreaseValue = discoveryRatingIncreaseValue;
    }

    public String getDiscoverRatingDecreaseValue() {
        return discoverRatingDecreaseValue;
    }

    public void setDiscoverRatingDecreaseValue(String discoverRatingDecreaseValue) {
        this.discoverRatingDecreaseValue = discoverRatingDecreaseValue;
    }

    public String getDiscoveryRatingDefault() {
        return discoveryRatingDefault;
    }

    public void setDiscoveryRatingDefault(String discoveryRatingDefault) {
        this.discoveryRatingDefault = discoveryRatingDefault;
    }

    //endregion
}