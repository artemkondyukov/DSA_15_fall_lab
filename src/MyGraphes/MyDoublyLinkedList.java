package MyGraphes;

import MyTrees.Position;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by artemka on 11/26/15.
 */
public class MyDoublyLinkedList<E> implements Iterable<E>{
    protected class Node implements Position<E> {
        private E value;
        private Node next;
        private Node prev;

        @Override
        public E getElement() {
            return value;
        }

        public Node(E value) {
            this.value = value;
        }
    }

    private Node header;
    private Node trailer;
    private int size;

    public MyDoublyLinkedList() {
        header = new Node(null);
        trailer = new Node(null);
        header.next = trailer;
        trailer.prev = header;
        size = 0;
    }

    public int size() {
        return this.size;
    }

    public Position<E> addFirst(E value) {
        Node result = new Node(value);
        if (header.next == null) {
            header.next = result;
            trailer.prev = result;
            return result;
        }

        result.next = header.next;
        result.prev = header;
        result.next.prev = result;
        header.next = result;
        size++;

        return result;
    }

    public Position<E> addBack(E value) {
        Node result = new Node(value);

        if (header.next == null) {
            header.next = result;
            trailer.prev = result;
            return result;
        }

        trailer.prev.next = result;
        result.prev = trailer.prev;
        result.next = trailer;
        trailer.prev = result;
        size++;

        return result;
    }

    // Just an alias
    public Position<E> add(E value) {
        return addBack(value);
    }

    public void deleteFirst() {
        delete(header.next);
    }

    public void deleteLast() {
        delete(trailer.prev);
    }

    public Node getFirst() {
        return header.next;
    }

    public Node getLast() {
        return trailer.prev;
    }

    public void delete(Position<E> position) {
        Node result = validate(position);
        if (result == null || result == header || result == trailer)
            throw new IllegalArgumentException("You put a wrong position");

        result.prev.next = result.next;
        result.next.prev = result.prev;

        size--;
    }

    private Node validate(Position<E> position) {
        try { return (Node)position; }
        catch (ClassCastException e) { return null; }
    }

    @Override
    public Iterator<E> iterator() {
        return new MyDoublyLinkedListIterator();
    }

    public class MyDoublyLinkedListIterator implements Iterator<E> {
        Node current;
        public MyDoublyLinkedListIterator() {
            current = header.next;
        }

        @Override
        public boolean hasNext() {
            return current != trailer;
        }

        @Override
        public E next() {
            Node tmp = current;
            current = current.next;
            return tmp.getElement();
        }
    }
}
