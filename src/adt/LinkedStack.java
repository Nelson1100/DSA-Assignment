package adt;

public class LinkedStack<T> {
    private StackNode<T> top;

    public void push(T item) {
        top = new StackNode<>(item, top);
    }

    public T pop() {
        if (isEmpty()) return null;
        T item = top.data;
        top = top.next;
        return item;
    }

    public T peek() {
        return isEmpty() ? null : top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    private static class StackNode<T> {
        T data;
        StackNode<T> next;

        StackNode(T data, StackNode<T> next) {
            this.data = data;
            this.next = next;
        }
    }
}
