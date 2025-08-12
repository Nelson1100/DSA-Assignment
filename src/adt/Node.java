package adt;

public class Node<T extends Comparable<T>> {
    private T key;
    private int height = 1;
    private Node<T> left;
    private Node<T> right;
    Node(T key) {
        this.key = key;
    }
    
    public void setKey(T key){
        this.key = key;
    }
    
    public void setHeight(int height){
        this.height = height;
    }
    
    public void setLeft(Node<T> left){
        this.left = left;
    }
    
    public void setRight(Node<T> right){
        this.right = right;
    }
    
    public T getKey(){
        return key;
    }
    
    public int getHeight(){
        return height;
    }
    
    public Node<T> getLeft(){
        return left;
    }
    
    public Node<T> getRight(){
        return right;
    }
}
