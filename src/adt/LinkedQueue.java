package adt;

import java.util.Iterator;

public class LinkedQueue<T> implements QueueInterface<T>, Iterable<T>{
    private QueueNode<T> front;
    private QueueNode<T> rear;
    private int size;
    
    public LinkedQueue() {
        front = null;
        rear = null;
        size = 0;
    }
    
    @Override
    public void enqueue(T newEntry) {
        QueueNode<T> newNode = new QueueNode<>(newEntry);
        if (isEmpty()) {
            front = newNode;
        } else {
            rear.next = newNode;
        }
        rear = newNode;
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty.");
        }
        T frontData = front.data;
        front = front.next;
        if (front == null) {
            rear = null; // queue is now empty
        }
        size--;
        return frontData;
    }

    @Override
    public T getFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty.");
        }
        return front.data;
    }

    @Override
    public boolean isEmpty() {
        return front == null;
    }

    @Override
    public void clear() {
        front = rear = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new QueueIterator<>(front);
    }
    
    @Override
    public QueueIterator<T> getIterator() {
        return new QueueIterator<>(front);
    }
    
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return front.getData();
    }
    
    public T[] toArray(T[] a) {
    if (a.length < size) {
        // Create a new array of the correct type
        a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    }

    QueueNode<T> current = front;
    int index = 0;
    while (current != null) {
        a[index++] = current.getData();
        current = current.getNext();
    }

    if (a.length > size) {
        a[size] = null;
    }

    return a;
}
}
