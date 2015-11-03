package org.educoins.core.p2p.peers;

import org.educoins.core.Block;

import java.net.InetAddress;
import java.util.Collection;

/**
 * The interface representing a remote note. This interface should be implemented to exchange data with a specific node.
 * Created by typus on 11/3/15.
 */
public abstract class RemoteNode {
    protected Long id;
    protected InetAddress inetAddress;
    protected String pubkey;

    public RemoteNode() {
    }

    public RemoteNode(Long id, InetAddress inetAddress, String pubkey) {
        this.id = id;
        this.inetAddress = inetAddress;
        this.pubkey = pubkey;
    }

    //    TODO: necessary? abstract void getHeaders();

    abstract Collection<Block> getBlocks();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemoteNode that = (RemoteNode) o;

        return !(id != null ? !id.equals(that.id) : that.id != null)
                && !(inetAddress != null ? !inetAddress.equals(that.inetAddress) : that.inetAddress != null)
                && !(pubkey != null ? !pubkey.equals(that.pubkey) : that.pubkey != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (inetAddress != null ? inetAddress.hashCode() : 0);
        result = 31 * result + (pubkey != null ? pubkey.hashCode() : 0);
        return result;
    }
}
