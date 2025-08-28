package adt;

/**
 *
 * @author Group 3
 */
public class AVLNode<T extends Comparable<T>> {
    private T key;
    private int height = 1;
    private AVLNode<T> left;
    private AVLNode<T> right;
    
    AVLNode(T key) {
        this.key = key;
    }
    
    public void setKey(T key){
        this.key = key;
    }
    
    public void setHeight(int height){
        this.height = height;
    }
    
    public void setLeft(AVLNode<T> left){
        this.left = left;
    }
    
    public void setRight(AVLNode<T> right){
        this.right = right;
    }
    
    public T getKey(){
        return key;
    }
    
    public int getHeight(){
        return height;
    }
    
    public AVLNode<T> getLeft(){
        return left;
    }
    
    public AVLNode<T> getRight(){
        return right;
    }
    
    public T getData() {
        return getKey(); 
    }
}
