package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class QueueIterator<T> implements Iterator<T>{
    private QueueNode<T> current;
    
    public QueueIterator(QueueNode<T> front) {
        current = front;
    }
    
    public boolean hasNext() {
        return current != null;
    }
    
    public T next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more elements.");
        }
        T data = current.data;
        current = current.next;
        return data;
    }
    
    public T getNext() {
        return next();
    }
}
