package adt;

public class QueueIterator<T> {
    private QueueNode<T> current;
    
    public QueueIterator(QueueNode<T> front) {
        current = front;
    }
    
    public boolean hasNext() {
        return current != null;
    }
    
    public T getNext() {
        if (!hasNext()) {
            throw new IllegalStateException("No more elements.");
        }
        T data = current.data;
        current = current.next;
        return data;
    }
}
