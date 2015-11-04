package MyTrees;

import java.util.*;

/**
 * Created by artemka on 10/12/15.
 */
public class MyArrayTree<E> extends AbstractTree<E>{
    private final static int DEFUNCT_POS = -1;

    private class Node <E> implements Position <E> {
        E value;
        int position;                               // It's for convenient finding parent/child.

        public E getElement() {return this.value;}
        public Node (E val, int pos) {
            value = val;
            position = pos;
        }
    }

    private Node<E>[] valuesArray;
    private int size;
    private int degree;

    @SuppressWarnings("unchecked")
    public MyArrayTree(int k, int capacity) {               // k is a degree of MyTrees.Tree
        valuesArray = (Node<E>[]) new Node[capacity];
        degree = k;
        size = 0;
    }

    public MyArrayTree(int k) {
        if (k <= 0) throw new IllegalArgumentException("You set size of tree to a non-positive number!");
        new MyArrayTree(k, 100);
    }

    public MyArrayTree() {
        new MyArrayTree(2);
    }

    /*
     * private method to enlarge your array ; )
     * we can not use doubling strategy here, because if degree is greater than 2
     * resizing will not allow to put a child for the last element of the tree
     */
    @SuppressWarnings("unchecked")
    private void resizeValueArray() {
        Node<E>[] newValuesArray = (Node<E>[]) new Node[valuesArray.length * 2 * degree];
        for (int i = 0; i < valuesArray.length; i++) {
            newValuesArray[i] = valuesArray[i];
        }
        valuesArray = newValuesArray;
    }

    /*
     * Set (or add) root method
     * @value is a value corresponding to root element
     * @throws if there is a root of the tree
     * because we don't allow to create tree with 0-size valueArray
     * we don't have to check whether there is enough room to root
     */
    public Position<E> addRoot(E value) throws IllegalStateException {
        if (root() != null) throw new IllegalStateException("The tree already has a root!");
        Node<E> newRoot = new Node<>(value, 0);
        valuesArray[0] = newRoot;
        size = 1;
        return newRoot;
    }

    /*
     * Child adding method. We don't allow to set root through this method, because it's name
     * therefore will become a bit ambiguous
     * @p is a position which child we going to set
     * @value is a value of a new child
     * @throws if position provided is not from the tree or is full of children
     * if array doesn't have enough room, we resize it
     */
    @SuppressWarnings("unchecked")
    public Position<E> addChild(Position<E> p, E value) throws IllegalStateException {
        if (validate(p) == null) throw new IllegalArgumentException("This position is not from the tree");
        if (numChildren(p) >= degree)
            throw new IllegalStateException("This position has max allowed number of children");
        Node<E> currentNode = (Node)p;
        int start = currentNode.position * degree + 1;
        int end = currentNode.position * degree + degree;
        if (end > valuesArray.length - 1) {
            resizeValueArray();
        }
        if (end > valuesArray.length - 1) {
            throw new IllegalStateException("Something's gone terribly wrong");
        }
        for (int i = start; i <= end; i++) {
            if (valuesArray[i] == null) {
                Node<E> newChild = new Node<>(value, i);
                valuesArray[i] = newChild;
                size++;
                return newChild;
            }
        }
        return null;
    }

    /*
     * Method for add copy of a tree as a child to a node
     * @p is a 'parent' position
     * @t is a subtree to add
     * @throws when you try to add part of current tree as a subtree (infinite recursion)
     * or when you try to add a child to a full node
     */
    @SuppressWarnings("unchecked")
    public void addSubtree(Position<E> p, MyArrayTree t)
            throws IllegalArgumentException, IllegalStateException{
        if (validate(p) == null) throw new IllegalArgumentException("This position is not from the tree");
        if (numChildren(p) >= degree)
            throw new IllegalStateException("This position has max allowed number of children");
        Node<E> currentNode = (Node)p;
        addSubtreeRecursive(currentNode, (Node<E>)t.root(), t);
        size += t.size();
    }

    @SuppressWarnings("unchecked")
    private void addSubtreeRecursive(Node<E> parent, Node<E> currChild, MyArrayTree subtree) {
        Node<E> newChild = (Node<E>) addChild(parent, (E) currChild.getElement());
        for (Object child: subtree.children(currChild)) {
            addSubtreeRecursive(newChild, (Node<E>)child, subtree);
        }
    }

    @Override
    public Position<E> root() {
        return valuesArray[0];
    }

    @Override
    public Position<E> setRoot(Position<E> p) throws IllegalStateException {
        if (root() != null) throw new IllegalStateException("The tree already has a root!");
        Node<E> newRoot = (Node<E>) validate(p);
        valuesArray[0] = newRoot;
        size++;
        return newRoot;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Position<E> parent(Position<E> p) throws IllegalArgumentException {
        if (validate(p) == null) throw new IllegalArgumentException("You give a node which is not from the tree");
        Node<E> currentNode = (Node)p;
        int newPos = (currentNode.position - 1) / degree;
        return valuesArray[newPos];
    }

    @Override
    public Position<E> validate(Position<E> p) {
        if (!(p instanceof Node)) {
            return null;
        }
        if (((Node) p).position == DEFUNCT_POS) {
            return null;
        }
        return p;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<Position<E>> children(Position<E> p) throws IllegalArgumentException {
        if (validate(p) == null) throw new IllegalArgumentException("You give a node which is not from the tree");
        Node<E> currentNode = (Node)p;
        List<Position<E> > result = new ArrayList<>();
        int start = currentNode.position * degree + 1;
        int end = currentNode.position * degree + degree;
        if (end > valuesArray.length - 1) {
            end = valuesArray.length - 1;
        }
        for (int i = start; i <= end; i++) {
            if (validate(valuesArray[i]) != null)
                result.add(valuesArray[i]);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int numChildren(Position<E> p) throws IllegalArgumentException {
        if (validate(p) == null) throw new IllegalArgumentException("You give a node which is not from the tree");
        Node<E> currentNode = (Node)p;
        int result = 0;
        int start = currentNode.position * degree + 1;
        int end = currentNode.position * degree + degree;
        if (end > valuesArray.length - 1) {
            end = valuesArray.length - 1;
        }
        for (int i = start; i <= end; i++) {
            if (validate(valuesArray[i]) != null)
                result++;
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Position<E> remove(Position<E> p) throws IllegalArgumentException {
        if (validate(p) == null) throw new IllegalArgumentException("You give a node which is not from the tree");
        List<Position<E>> snapshot = new ArrayList<>();
        postorderSubtree(p, snapshot);
        for (Position<E> toDel: snapshot) {
            Node<E> curr = (Node<E>)toDel;
            valuesArray[curr.position] = null;
            size--;
        }
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void draw() {

    }
}