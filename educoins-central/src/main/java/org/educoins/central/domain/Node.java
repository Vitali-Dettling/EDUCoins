package org.educoins.central.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.net.URI;

/**
 * The Node representation like stored in the database and also presented to the requester.
 * Created by typus on 11/2/15.
 */
@Entity
public class Node {
    @Id
    private String pubkey;

    private URI inetAddress;

    private PeerType type;

    public Node() {
    }

    public Node(String pubkey, URI inetAddress, PeerType type) {
        this.inetAddress = inetAddress;
        this.pubkey = pubkey;
        this.type = type;
    }

    public URI getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(URI inetAddress) {
        this.inetAddress = inetAddress;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public PeerType getType() {
        return type;
    }

    public void setType(PeerType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int result = pubkey != null ? pubkey.hashCode() : 0;
        result = 31 * result + (inetAddress != null ? inetAddress.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return !(pubkey != null ? !pubkey.equals(node.pubkey) : node.pubkey != null)
                && !(inetAddress != null ? !inetAddress.equals(node.inetAddress) : node.inetAddress != null)
                && (type == node.type);

    }
}
