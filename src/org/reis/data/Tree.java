package org.reis.data;

public class Tree {
    static class Node {
        Object data;
        Node left, right;
    }

    private Node root;
    public boolean isBalanced() {
        return balance(root) >= 0;
    }

    private int balance(Node root) {
        if(root == null) return 0;

        int leftBalance = balance(root.left);
        if(leftBalance == -1) return leftBalance;

        int rightBalance = balance(root.right);
        if(rightBalance == -1) return rightBalance;

        int rootBalance = Math.abs(rightBalance - leftBalance);
        if(rootBalance > 1) return -1;

        return Math.max(leftBalance, rightBalance) + 1;
    }
}
