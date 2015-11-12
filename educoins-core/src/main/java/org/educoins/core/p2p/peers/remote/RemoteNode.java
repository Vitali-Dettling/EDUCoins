package org.educoins.core.p2p.peers.remote;

import org.educoins.core.Block;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 * The interface representing a remote note.
 * This interface should be implemented to exchange data with a specific node.
 * Created by typus on 11/3/15.
 */
public abstract class RemoteNode {
    protected Long id;
    protected URI uri;
    protected String pubkey;

    public RemoteNode() {
    }

    public RemoteNode(@NotNull Long id, @NotNull URI uri, @NotNull String pubkey) {
        this.id = id;
        this.uri = uri;
        this.pubkey = pubkey;
    }

    @NotNull
    public abstract Collection<Block> getHeaders() throws IOException;

    @NotNull
    public abstract Collection<Block> getBlocks() throws IOException;

    public String getPubkey() {
        return pubkey;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (pubkey != null ? pubkey.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemoteNode node = (RemoteNode) o;

        return !(id != null ? !id.equals(node.id) : node.id != null)
                && !(uri != null ? !uri.equals(node.uri) : node.uri != null)
                && !(pubkey != null ? !pubkey.equals(node.pubkey) : node.pubkey != null);
    }
}
