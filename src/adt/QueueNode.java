package adt;

public class QueueNode<T> {
    public T data;
    public QueueNode<T> next;
    
    public QueueNode(T data) {
        this.data = data;
        this.next = null;
    }
    
    public T getData() {
        return data; 
    }
    
    public QueueNode<T> getNext() {
        return next;
    }
    
    public void setNext(QueueNode<T> next) {
        this.next = next;
    }
}
