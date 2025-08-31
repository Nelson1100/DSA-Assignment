package adt;

/**
 *
 * @author Group 3
 */
public interface AVLInterface<T extends Comparable<T>> extends Iterable<T>{
    boolean insert(T data);
    boolean delete(T data);
    T find(T data);
    void traverse();
    T getMax();
    T getMin();
    boolean isEmpty();
    boolean contains(T data);
    int size();
    int height();
    void clear();
    T[] toArrayInorder(T[] arr);
    boolean isValidAVL();
}
