package org.educoins.core.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<E extends Hashable> implements Hashable {

    private E element;
    private Sha256Hash hash;
    private TreeNode parent;
    private List<TreeNode<E>> children;

    TreeNode(TreeNode parent, List<TreeNode<E>> children) {
        this.element = null;
        Sha256Hash leftHash = Sha256Hash.wrap(children.get(0).hash());
        Sha256Hash rightHash = Sha256Hash.wrap(children.get(1).hash());
        this.hash = leftHash.concat(rightHash);
        this.parent = parent;
        this.children = new ArrayList<TreeNode<E>>(children);
    }

    TreeNode(E element, TreeNode parent) {
        this.element = element;
        this.hash = Sha256Hash.wrap(element.hash());
        this.parent = parent;
        this.children = null;
    }

    public TreeNode getLeft() {
        return  this.children.get(0);
    }

    public TreeNode getRight() {
        return  this.children.get(1);
    }

    public TreeNode getParent() {
        return  this.parent;
    }

    public E getElement() {
        return  this.element;
    }

    public boolean isLeaf() {
        return  this.element != null &&  this.children == null;
    }

    public boolean isRoot() {
        return  this.element == null &&  this.children != null &&  this.children.size() == 2;
    }

    public List<TreeNode<E>> getAllLeaves() {
        List<TreeNode<E>> retList = new LinkedList<>();
        if (children != null) {
            for (TreeNode<E> child : children) {
                retList.addAll(child.getAllLeaves());
            }
        } else {
            retList.add(this);
        }
        return retList;
    }

    @Override
    public byte[] hash() {
        return this.hash.getBytes();
    }

    public void setParent(TreeNode node) {
        this.parent = node;
    }
}
