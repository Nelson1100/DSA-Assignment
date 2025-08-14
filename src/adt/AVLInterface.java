package adt;

public interface AVLInterface<T extends Comparable<T>> {
    AVLInterface<T> insert(T data);
    AVLInterface<T> delete(T data);
    void traverse();
    T getMax();
    T getMin();
    boolean isEmpty();
    boolean contains(T data);
    int size();
    int height();
    void clear();
    T[] toArrayInorder();
    boolean isValidAVL();
}
