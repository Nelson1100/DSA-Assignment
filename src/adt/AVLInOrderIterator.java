package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Group 3
 */
public class AVLInOrderIterator<T extends Comparable<T>> implements Iterator<T> {
    private LinkedStack<AVLNode<T>> stack = new LinkedStack<>();

    public AVLInOrderIterator(AVLNode<T> root) {
        pushLeftPath(root);
    }

    private void pushLeftPath(AVLNode<T> node) {
        while (node != null) {
            stack.push(node);
            node = node.getLeft();
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();

        AVLNode<T> node = stack.pop();
        T data = node.getData();

        if (node.getRight() != null) {
            pushLeftPath(node.getRight());
        }

        return data;
    }
}
