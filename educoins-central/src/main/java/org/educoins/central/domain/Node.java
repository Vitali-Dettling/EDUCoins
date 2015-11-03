package org.educoins.central.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.InetAddress;

/**
 * The Node representation like stored in the database and also presented to the requester.
 * Created by typus on 11/2/15.
 */
@Entity
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private InetAddress inetAddress;
    private String pubkey;

    public Node() {
    }

    public Node(long id, InetAddress inetAddress, String pubkey) {
        this.id = id;
        this.inetAddress = inetAddress;
        this.pubkey = pubkey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return !(id != null ? !id.equals(node.id) : node.id != null)
                && !(inetAddress != null ? !inetAddress.equals(node.inetAddress) : node.inetAddress != null)
                && !(pubkey != null ? !pubkey.equals(node.pubkey) : node.pubkey != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (inetAddress != null ? inetAddress.hashCode() : 0);
        result = 31 * result + (pubkey != null ? pubkey.hashCode() : 0);
        return result;
    }
}
