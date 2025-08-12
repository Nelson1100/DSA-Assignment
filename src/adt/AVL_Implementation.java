package adt;

public interface AVL_Implementation<T extends Comparable<T>> {
    AVL_Implementation<T> insert(T data);
    AVL_Implementation<T> delete(T data);
    void traverse();
    T getMax();
    T getMin();
    boolean isEmpty();
    boolean contains(T data);
    int size();
    int height();
    void clear();
    java.util.List<T> toListInorder();
    boolean isValidAVL();
}
