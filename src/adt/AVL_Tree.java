package adt;

public class AVL_Tree<T extends Comparable<T>> implements AVL_Implementation<T> {
    private Node<T> root;
    
    @Override
    public AVL_Implementation<T> insert(T data){
        root = insert(data, root);
        return this;
    }
    
    @Override
    public AVL_Implementation<T> delete(T data){
        root = delete(data, root);
        return this;
    }
    
    @Override
    public T find(T data) {
        Node<T> current = root;
        while (current != null) {
            int c = data.compareTo(current.getKey());
            if (c == 0) 
                return current.getKey();
            
            current = (c < 0) ? current.getLeft() : current.getRight();
        }
        return null;
    }

    private void updateHeight(Node<T> node){
        int maxHeight = Math.max(height(node.getLeft()), height(node.getRight()));
        node.setHeight(maxHeight + 1);
    }
    
    public int height() {
        return height(root);
    }

    private int height(Node<T> node){
        return node != null ? node.getHeight() : 0;
    }
    
    public int size() {
        return size(root);
    }
    
    private int size(Node<T> n){
        return n == null ? 0 : 1 + size(n.getLeft()) + size(n.getRight());
    }
    
    private int balance(Node<T> node){
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }
    
    private Node<T> insert(T data, Node<T> node) {
        if (node == null)
            return new Node<>(data);
        
        if (data.compareTo(node.getKey()) < 0)
            node.setLeft(insert(data, node.getLeft()));
        else if (data.compareTo(node.getKey()) > 0)
            node.setRight(insert(data, node.getRight()));
        else
            return node;
        
        updateHeight(node);
        return applyRotation(node);
    }
    
    private Node<T> delete(T data, Node<T> node){
        if (node == null)
            return null;
        
        if (data.compareTo(node.getKey()) < 0)
            node.setLeft(delete(data, node.getLeft()));
        else if (data.compareTo(node.getKey()) > 0)
            node.setRight(delete(data, node.getRight()));
        else {
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

    private Node<T> rotateRight(Node<T> node){
        Node<T> leftNode = node.getLeft();
        Node<T> centerNode = leftNode.getRight();
        leftNode.setRight(node);
        node.setLeft(centerNode);
        updateHeight(node);
        updateHeight(leftNode);
        return leftNode;
    }
    
    private Node<T> rotateLeft(Node<T> node){
        Node<T> rightNode = node.getRight();
        Node<T> centerNode = rightNode.getLeft();
        rightNode.setLeft(node);
        node.setRight(centerNode);
        updateHeight(node);
        updateHeight(rightNode);
        return rightNode;
    }
    
    private Node<T> applyRotation(Node<T> node){
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
        Node<T> cur = root;
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

    private void inorder(Node<T> node){
        if (node == null) return;
        inorder(node.getLeft());
        System.out.print(node.getKey() + " ");
        inorder(node.getRight());
    }

    @Override
    public T getMax() {
        if (root == null) return null;
        Node<T> cur = root;
        while (cur.getRight() != null) cur = cur.getRight();
        return cur.getKey();
    }

    @Override
    public T getMin() {
        if (root == null) return null;
        Node<T> cur = root;
        while (cur.getLeft() != null) cur = cur.getLeft();
        return cur.getKey();
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    // ---- Helpers used internally ----
    private T getMax(Node<T> node){
        Node<T> cur = node;
        while (cur.getRight() != null) cur = cur.getRight();
        return cur.getKey();
    }

    @SuppressWarnings("unused")
    private T getMin(Node<T> node){
        Node<T> cur = node;
        while (cur.getLeft() != null) cur = cur.getLeft();
        return cur.getKey();
    }
    
    public void clear() {
        root = null;
    }
    
    public java.util.List<T> toListInorder() {
        java.util.List<T> out = new java.util.ArrayList<>();
        toListInorder(root, out);
        return out;
    }
    private void toListInorder(Node<T> n, java.util.List<T> out){
        if (n == null) return;
        toListInorder(n.getLeft(), out);
        out.add(n.getKey());
        toListInorder(n.getRight(), out);
    }

    
    public boolean isValidAVL() {
        return checkAVL(root).ok;
    }

    private static class AvlCheck {
        final boolean ok;
        final int height;
        AvlCheck(boolean ok, int height){ this.ok = ok; this.height = height; }
    }

    private AvlCheck checkAVL(Node<T> n){
        if (n == null) return new AvlCheck(true, 0);

        AvlCheck L = checkAVL(n.getLeft());
        AvlCheck R = checkAVL(n.getRight());
        if (!L.ok || !R.ok) return new AvlCheck(false, 0);
        
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