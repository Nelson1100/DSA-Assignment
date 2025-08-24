package adt;

import java.util.Iterator;

public class AVLTree<T extends Comparable<T>> implements AVLInterface<T>{
    private AVLNode<T> root;
    private boolean insertionSuccess;
    private boolean deletionSuccess;
    
    @Override
    public boolean insert(T data) {
        insertionSuccess = false;
        root = insert(data, root);
        return insertionSuccess;
    }

    private AVLNode<T> insert(T data, AVLNode<T> node) {
        if (node == null) {
            insertionSuccess = true;
            return new AVLNode<>(data);
        }

        if (data.compareTo(node.getKey()) < 0)
            node.setLeft(insert(data, node.getLeft()));
        else if (data.compareTo(node.getKey()) > 0)
            node.setRight(insert(data, node.getRight()));
        else
            return node;

        updateHeight(node);
        return applyRotation(node);
    }
    
    @Override
    public boolean delete(T data){
        deletionSuccess = false;
        root = delete(data, root);
        return deletionSuccess;
    }
    
    private AVLNode<T> delete(T data, AVLNode<T> node){
        if (node == null)
            return null;

        if (data.compareTo(node.getKey()) < 0)
            node.setLeft(delete(data, node.getLeft()));
        else if (data.compareTo(node.getKey()) > 0)
            node.setRight(delete(data, node.getRight()));
        else {
            deletionSuccess = true;  // mark as success only when node is matched

            if (node.getLeft() == null)
                return node.getRight();
            else if (node.getRight() == null)
                return node.getLeft();

            node.setKey(getMax(node.getLeft()));
            node.setLeft(delete(node.getKey(), node.getLeft()));
        }

        updateHeight(node);
        return applyRotation(node);
    }
    
    @Override
    public T find(T data) {
        AVLNode<T> current = root;
        while (current != null) {
            int c = data.compareTo(current.getKey());
            if (c == 0) 
                return current.getKey();
            
            current = (c < 0) ? current.getLeft() : current.getRight();
        }
        return null;
    }

    private void updateHeight(AVLNode<T> node){
        int maxHeight = Math.max(height(node.getLeft()), height(node.getRight()));
        node.setHeight(maxHeight + 1);
    }
    
    @Override
    public int height() {
        return height(root);
    }

    private int height(AVLNode<T> node){
        return node != null ? node.getHeight() : 0;
    }
    
    @Override
    public int size() {
        return size(root);
    }
    
    private int size(AVLNode<T> n){
        return n == null ? 0 : 1 + size(n.getLeft()) + size(n.getRight());
    }
    
    private int balance(AVLNode<T> node){
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }
    
    private AVLNode<T> rotateRight(AVLNode<T> node){
        AVLNode<T> leftNode = node.getLeft();
        AVLNode<T> centerNode = leftNode.getRight();
        leftNode.setRight(node);
        node.setLeft(centerNode);
        updateHeight(node);
        updateHeight(leftNode);
        return leftNode;
    }
    
    private AVLNode<T> rotateLeft(AVLNode<T> node){
        AVLNode<T> rightNode = node.getRight();
        AVLNode<T> centerNode = rightNode.getLeft();
        rightNode.setLeft(node);
        node.setRight(centerNode);
        updateHeight(node);
        updateHeight(rightNode);
        return rightNode;
    }
    
    private AVLNode<T> applyRotation(AVLNode<T> node){
        int balance = balance(node);
        
        if (balance > 1){
            if (balance(node.getLeft()) < 0)
                node.setLeft(rotateLeft(node.getLeft()));
            
            return rotateRight(node);
        } else if (balance < -1){
            if (balance(node.getRight()) > 0)
                node.setRight(rotateRight(node.getRight()));
            
            return rotateLeft(node);
        }
        return node;
    }
    
    @Override
    public boolean contains(T data) {
        AVLNode<T> cur = root;
        while (cur != null) {
            int c = data.compareTo(cur.getKey());
            if (c == 0) return true;
            cur = (c < 0) ? cur.getLeft() : cur.getRight();
        }
        return false;
    }

    @Override
    public void traverse() {
        inorder(root);
        System.out.println();
    }

    private void inorder(AVLNode<T> node){
        if (node == null) return;
        inorder(node.getLeft());
        System.out.print(node.getKey() + " ");
        inorder(node.getRight());
    }

    @Override
    public T getMax() {
        if (root == null) return null;
        AVLNode<T> cur = root;
        while (cur.getRight() != null) cur = cur.getRight();
        return cur.getKey();
    }

    @Override
    public T getMin() {
        if (root == null) return null;
        AVLNode<T> cur = root;
        while (cur.getLeft() != null) cur = cur.getLeft();
        return cur.getKey();
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    // ---- Helpers used internally ----
    private T getMax(AVLNode<T> node){
        AVLNode<T> cur = node;
        while (cur.getRight() != null) cur = cur.getRight();
        return cur.getKey();
    }

    @SuppressWarnings("unused")
    private T getMin(AVLNode<T> node){
        AVLNode<T> cur = node;
        while (cur.getLeft() != null) cur = cur.getLeft();
        return cur.getKey();
    }
    
    @Override
    public void clear() {
        root = null;
    }
    
//    public java.util.List<T> toListInorder() {
//        java.util.List<T> out = new java.util.ArrayList<>();
//        toListInorder(root, out);
//        return out;
//    }
//    private void toListInorder(AVLNode<T> n, java.util.List<T> out){
//        if (n == null) return;
//        toListInorder(n.getLeft(), out);
//        out.add(n.getKey());
//        toListInorder(n.getRight(), out);
//    }
    
    @Override
    public Iterator<T> iterator() {
        return new AVLInOrderIterator<>(root); // root must be accessible
    }

    @Override
    public T[] toArrayInorder(T[] arr) {
        fillInorder(root, arr, new int[]{0});
        return arr;
    }

    private void fillInorder(AVLNode<T> n, T[] arr, int[] idx) {
        if (n == null)
            return;
        
        fillInorder(n.getLeft(), arr, idx);
        arr[idx[0]++] = n.getKey(); 
        fillInorder(n.getRight(), arr, idx);
    }
    
    @Override
    public boolean isValidAVL() {
        return checkAVL(root).ok;
    }

    private static class AvlCheck {
        final boolean ok;
        final int height;
        AvlCheck(boolean ok, int height){ this.ok = ok; this.height = height; }
    }

    private AvlCheck checkAVL(AVLNode<T> n){
        if (n == null) return new AvlCheck(true, 0);

        AvlCheck L = checkAVL(n.getLeft());
        AvlCheck R = checkAVL(n.getRight());
        if (!L.ok || !R.ok) return new AvlCheck(false, 0);

        // BST ordering relative to children
        boolean orderedLeft  = (n.getLeft()  == null) || (n.getLeft().getKey().compareTo(n.getKey())  < 0);
        boolean orderedRight = (n.getRight() == null) || (n.getRight().getKey().compareTo(n.getKey()) > 0);
        if (!orderedLeft || !orderedRight) return new AvlCheck(false, 0);

        int h = Math.max(L.height, R.height) + 1;
        boolean balanced = Math.abs(L.height - R.height) <= 1;

        // also check stored height equals computed height
        boolean heightMatches = (n.getHeight() == h);

        return new AvlCheck(balanced && heightMatches, h);
    }
    
}