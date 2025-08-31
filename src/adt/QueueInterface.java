package adt;

public interface QueueInterface<T> extends Iterable<T> {
    public void enqueue(T newEntry);
    public T dequeue();
    public T getFront();
    public boolean isEmpty();
    public void clear();
    public int size();
    QueueIterator<T> getIterator();
}
