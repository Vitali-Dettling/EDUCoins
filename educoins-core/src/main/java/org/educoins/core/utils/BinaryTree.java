package org.educoins.core.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class BinaryTree<E extends Hashable> {

    private TreeNode<E> root;

    public BinaryTree(@NotNull List<E> leaves) {
        assert leaves.size() != 0;

        int next2Exp = 1;
        while (Math.pow(2, next2Exp) < leaves.size()) {
            next2Exp++;
        }
        List<TreeNode<E>> leavesList = new LinkedList<>();
        TreeNode<E> lastLeaf = null;
        for (E leaf : leaves) {
            lastLeaf = new TreeNode<E>(leaf, null);
            leavesList.add(lastLeaf);
        }
        int i = leaves.size();
        while (Math.pow(2, next2Exp) - i != 0) {
            leavesList.add(lastLeaf);
            i++;
        }
        List<TreeNode<E>> nextHashList = calculateNextLevelInBinaryTree(leavesList);
        while (nextHashList.size() != 1) {
            nextHashList = calculateNextLevelInBinaryTree(nextHashList);
        }
        this.root = nextHashList.get(0);
    }

    private List<TreeNode<E>> calculateNextLevelInBinaryTree(@NotNull List<TreeNode<E>> initialList) {
        assert initialList.size() % 2 == 0;
        List<TreeNode<E>> returnList = new LinkedList<>();
        for (int i = 0; i < initialList.size(); i += 2) {
            List<TreeNode<E>> childrenList = new ArrayList<>();
            TreeNode<E> left = initialList.get(i);
            TreeNode<E> right = initialList.get(i + 1);
            childrenList.add(left);
            childrenList.add(right);
            TreeNode<E> node = new TreeNode<E>(null, childrenList);
            left.setParent(node);
            right.setParent(node);
            returnList.add(node);
        }
        return returnList;
    }

    public TreeNode getRoot() {
        assert root.getParent() == null;
        return root;
    }

    public List<E> getElements() {
        List<TreeNode<E>> leaves = root.getAllLeaves();
        List<E> elements = new ArrayList<>();
        for (TreeNode<E> leaf : leaves) {
            elements.add(leaf.getElement());
        }
        return elements;
    }
}
