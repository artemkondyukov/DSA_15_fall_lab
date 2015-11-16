package MyTrees;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by artemka on 11/10/15.
 */
public class MyArrayHeap<K extends Comparable<? super K>, V> extends TreeMap<K, V> {
    private enum HEAP_TYPE {
        MAX_HEAP(1), MIN_HEAP(-1);

        private int numVal;
        HEAP_TYPE(int numVal) { this.numVal = numVal; }
        public int getNumVal() { return this.numVal; }
    }

    private HEAP_TYPE heapType = HEAP_TYPE.MAX_HEAP;

    public class Pair<T, E> {
        private T key;
        private E value;

        public Pair(T key, E value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() { return key; }
        public E getValue() { return value; }

        @Override
        public String toString() {
            return this.key + " " + this.value;
        }
    }

    private MyArrayTree<Pair<K, V>> hidden;
    private Position<Pair<K, V>> lastParent;    // This is node, where we are going to put new child

//    private void fixNextFreeParentOfHidden() {
//        boolean next = false;               // For finding next child of parent of lastParent
//        while (!hidden.isRoot(lastParent)) {
//            Position<Pair<K, V>> parent = hidden.parent(lastParent);
//            Iterator<Position<Pair<K, V>>> iterator = parent != null ?
//                    hidden.children(parent).iterator(): null;
//
//            if (iterator != null) {
//                while (iterator.hasNext())
//                    if (iterator.next() == lastParent && iterator.hasNext()) {
//                        next = true;
//                        break;
//                    }
//                if (next) {
//                    lastParent = iterator.next();
//                    break;
//                }
//                else {
//                    lastParent = hidden.parent(lastParent);
//                }
//            }
//            else break;
//        }
//        if (!next) {
//            lastParent = hidden.root();
//            // We always take the first element here (start new level from left)
//            while (hidden.numChildren(lastParent) > 0) lastParent = hidden.children(lastParent).iterator().next();
//        }
//    }

    public MyArrayHeap() {
        hidden = new MyArrayTree<>(2, 20000000);     // By default we use binary tree
    }

    public Position<Pair<K, V>> add(K key, V value) {
        if (hidden.root() == null) {
            hidden.addRoot(new Pair<>(key, value));
            return hidden.root();
        }

        Position<Pair<K, V>> newPosition = hidden.appendChild(new Pair<>(key, value));
        upHeap(newPosition);
        return newPosition;
    }

    public V peek() {
        return hidden.root().getElement().value;
    }

    /*
     * removes minimal or maximal element (depending on type of heap)
     */
    public Position<Pair<K, V>> removeMost() {
        Position<Pair<K, V>> result = hidden.root();
        if (result == hidden.getLast()) { hidden.remove(result); return result; }
        hidden.swap(result, hidden.getLast());
        hidden.remove(result);
        downHeap(hidden.root());
        return result;
    }

    private void upHeap(Position<Pair <K, V>> p) {
        Position<Pair<K, V>> parent = hidden.parent(p);
        while (heapType.getNumVal() * parent.getElement().key.compareTo(p.getElement().key) < 0) {

            // Roles are also should be swapped
            if (lastParent == parent)
                lastParent = p;
            else if (lastParent == p)
                lastParent = parent;

            hidden.swap(p, parent);
            parent = hidden.parent(p);

            if (parent == null) break;
        }
    }

    private void downHeap(Position<Pair <K, V>> p) {
        boolean swapped = false;
        while (!swapped) {
            Position<Pair<K, V>> mostChild = null;
            for (Position<Pair<K, V>> child : hidden.children(p)) {
                if (mostChild == null) {    // First iteration
                    mostChild = child;
                    continue;
                }
                if (heapType.getNumVal() * child.getElement().key.compareTo(mostChild.getElement().key) > 0) {
                    mostChild = child;
                }
            }
            if(mostChild == null ||
                    heapType.getNumVal() * mostChild.getElement().key.compareTo(p.getElement().key) < 0)
                swapped = true;
            else {
                hidden.swap(p, mostChild);
            }
        }
    }

    public int size() { return hidden.size(); }

    public void draw() { hidden.draw(); }
}
