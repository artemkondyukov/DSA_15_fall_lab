package DSA_Asgn4_1;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by artemka on 11/25/15.
 */
public class Main {
    public interface Tree <E> extends TreeIterable<E> {
        Position<E> root();
        Position<E> setRoot(Position<E> p) throws IllegalStateException;
        Position<E> addRoot(E value) throws IllegalStateException;
        Position<E> parent(Position<E> p) throws IllegalArgumentException; //throws if p is not from the tree
        Position<E> validate(Position<E> p);

        Iterable<Position<E> > children(Position<E> p)
                throws IllegalArgumentException; //throws if p is not from the tree
        int numChildren(Position<E> p)
                throws IllegalArgumentException; //throws if p is not from the tree
        Position<E> remove(Position<E> p)
                throws IllegalArgumentException; //throws if p is not from the tree

        boolean isInternal(Position<E> p)
                throws IllegalArgumentException;//throws if p is not from the tree
        boolean isExternal(Position<E> p)
                throws IllegalArgumentException;//throws if p is not from the tree
        boolean isRoot(Position<E> p)
                throws IllegalArgumentException;//throws if p is not from the tree

        int height();
        int heightOfSubtree(Position<E> p);

        int size();
        boolean isEmpty();

        void draw();
    }

    public interface TreeIterable<E> extends Iterable<E> {
        Iterator<E> iterator();
        Iterable<Position<E> > positions();
        Iterable<Position<E> > preorder();
        Iterable<Position<E> > postorder();
        Iterable<Position<E> > breadthFirst();
    }

    public interface Position <E> {
        public E getElement();
    }

    public static abstract class AbstractTree<E> implements Tree<E> {

        public boolean isInternal(Position<E> p) {return numChildren(p) > 0;}
        public boolean isExternal(Position<E> p) {return numChildren(p) == 0;}
        public boolean isRoot(Position<E> p) {return parent(p) == null;}

        public boolean isEmpty() {return size() == 0;}

        @Override
        public int height() {
            return heightOfSubtree(root());
        }

        @Override
        public int heightOfSubtree(Position<E> p) {
            if (p == null) return 0;                    // For AVL, height of empty tree is 0
            int maxChildHeight = 0;
            for (Position<E> child: children(p)) {
                if (heightOfSubtree(child) > maxChildHeight)
                    maxChildHeight = heightOfSubtree(child);
            }
            return maxChildHeight + 1;
        }

        /*
         * By default we return preorder traversal iterable collection
         */
        @Override
        public Iterable<Position<E>> positions() {
            return preorder();
        }

        @Override
        public Iterator<E> iterator() {
            return new TreePreorderIterator<>();
        }

        public class TreePreorderIterator<E> implements Iterator<E> {
            private Iterable<Position<E>> snapshot;

            @SuppressWarnings("unchecked")
            public TreePreorderIterator() {
                AbstractTree<E> parent = (AbstractTree<E>) AbstractTree.this;
                snapshot = parent.preorder();
            }

            @Override
            public boolean hasNext() {
                return snapshot.iterator().hasNext();
            }

            @Override
            public E next() {
                return snapshot.iterator().next().getElement();
            }
        }

        @Override
        public Iterable<Position<E>> preorder() {
            if (isEmpty()) return null;
            List<Position<E>> result = new ArrayList<>();
            preorderSubtree(root(), result);
            return result;
        }

        /*
         * Recursive function for convenient preorder traversal
         * @p is a root of subtree to be traversal
         * @snapshot is a collection where we place elements to
         */
        public void preorderSubtree(Position<E> p, List<Position<E>> snapshot) {
            snapshot.add(p);
            for (Position<E> child: children(p)) {
                preorderSubtree(child, snapshot);
            }
        }

        @Override
        public Iterable<Position<E>> postorder() {
            if (isEmpty()) return null;
            List<Position<E>> result = new ArrayList<>();
            postorderSubtree(root(), result);
            return result;
        }

        /*
         * Recursive function for convenient preorder traversal
         * @p is a root of subtree to be traversal
         * @snapshot is a collection where we place elements to
         */
        public void postorderSubtree(Position<E> p, List<Position<E>> snapshot) {
            for (Position<E> child: children(p)) {
                postorderSubtree(child, snapshot);
            }
            snapshot.add(p);
        }

        /*
         * Breadth-first traversal algorithm implementation.
         * Returns iterable collection of positions ordered by depth
         */
        @Override
        public Iterable<Position<E>> breadthFirst() {
            if (isEmpty()) return null;
            List<Position<E>> result = new ArrayList<>();
            Queue<Position<E> > fringe = new ArrayDeque<>(size());
            fringe.add(root());
            while (!fringe.isEmpty()) {
                Position<E> current = fringe.remove();
                for (Position<E> child: children(current)) {
                    fringe.add(child);
                }
                result.add(current);
            }
            return result;
        }
    }

    public static class MyArrayTree<E> extends AbstractTree<E> {
        private final static int DEFUNCT_POS = -1;

        private class Node <E> implements Position<E> {
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
        public MyArrayTree(int k, int capacity) {               // k is a degree of Tree
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

        /*
         * Adds value to the end of array, i.e. very useful for building complete tree
         */
        public Position<E> appendChild(E value) {
            Node<E> newChild = new Node<>(value, size);
            if (size >= valuesArray.length) resizeValueArray();
            valuesArray[size++] = newChild;
            return newChild;
        }

        /*
         * Returns the last child
         */
        public Position<E> getLast() {
            return valuesArray[size - 1];
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
            if (currentNode.position == 0) return null;
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

        @SuppressWarnings("unchecked")
        public void swap(Position<E> oldPosition, Position<E> newPosition) {
            if (validate(oldPosition) == null) throw new IllegalArgumentException("You give a node which is not from the tree");
            if (validate(newPosition) == null) throw new IllegalArgumentException("You give a node which is not from the tree");
            Node<E> tmpOld = (Node<E>)oldPosition;
            Node<E> tmpNew = (Node<E>)newPosition;
            valuesArray[tmpOld.position] = tmpNew;
            valuesArray[tmpNew.position] = tmpOld;
            tmpNew.position += tmpOld.position;
            tmpOld.position = tmpNew.position - tmpOld.position;
            tmpNew.position = tmpNew.position - tmpOld.position;
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public void draw() {
            // I'm to lazy to write actual drawing...
            for (Node<E> node: valuesArray) {
                if (node == null) break;
                System.out.println(node.getElement().toString());
            }
        }
    }

    public enum HEAP_TYPE {
        MAX_HEAP(1), MIN_HEAP(-1);

        private int numVal;
        HEAP_TYPE(int numVal) { this.numVal = numVal; }
        public int getNumVal() { return this.numVal; }
    }

    public static class MyArrayHeap<K extends Comparable<? super K>, V> extends TreeMap<K, V> {
        private HEAP_TYPE heapType;

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

        public MyArrayHeap(HEAP_TYPE type) {
            hidden = new MyArrayTree<>(2, 200);     // By default we use binary tree
            this.heapType = type;
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

    public interface Vertex<V> {
        V getValue();
        Iterator<? extends Vertex<V>> getAdjVertices();
    }

    public interface Edge<V, E> {
        List<Vertex<V>> endVertices();
        E getValue();
    }

    public interface Graph<V, E> {
        int numVertices();
        Iterator<? extends Vertex<V>> vertices();

        int numEdges();
        Iterator<? extends Edge<V, E>> edges();

        Edge<V, E> getEdge(Vertex<V> f, Vertex<V> s);
        Iterator<? super Vertex<V>> endVertices(Edge<V, E> e);

        Vertex<V> opposite(Vertex<V> v, Edge<V, E> e);
        int outDegree(Vertex<V> v);
        int inDegree(Vertex<V> v);

        Iterator<? extends Edge<V, E>> outgoingEdges(Vertex<V> v);
        Iterator<? extends Edge<V, E>> incomingEdges(Vertex<V> v);

        Vertex<V> insertVertex(V v);
        Edge<V, E> insertEdge(E e, Vertex<V> f, Vertex<V> s);

        void removeVertex(Vertex<V> v);
        void removeEdge(Edge<V, E> e);
    }

    /**
     * Created by artemka on 11/24/15.
     * Adjacency list implementation of graph for
     * graded assignment #4 (DSA Fall 15)
     * It's undirected graph by now
     */
    public static class MyGraph<V, E> implements Graph<V, E> {
        protected class Node implements Vertex<V> {
            private V value;
            private int position; // Describes position of the Node in MyGraph's storage

            // Adjacency list
            private List<Arc> adjList;

            public Node(V value) {
                this.value = value;
                adjList = new ArrayList<>();
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public Iterator<? extends Vertex<V>> getAdjVertices() {
                List<Vertex<V>> result = adjList.stream().map(arc -> opposite(this, arc)).collect(Collectors.toList());
                return result.iterator();
            }

            public List<Arc> getAdjList() {
                return adjList;
            }

            public int getPosition() {
                return position;
            }

            public void setPosition(int position) {
                this.position = position;
            }
        }

        protected class Arc implements Edge<V, E> {
            private int position; // Describes position of the Arc in MyGraph's storage
            private int posInFirst; // Describes position of the Arc in First Node adjList
            private int posInSecond; // Describes position of the Arc in Second Node adjList

            private E value;
            private Node first;
            private Node second;

            public Arc(Node first, Node second, E value, int position) {
                this.first = first;
                this.second = second;
                this.value = value;
                posInFirst = first.adjList.size();
                posInSecond = second.adjList.size();
            }

            @Override
            public List<Vertex<V>> endVertices() {
                List<Vertex<V>> result = new ArrayList<>();
                result.add(first);
                result.add(second);
                return result;
            }

            @Override
            public E getValue() {
                return value;
            }
        }

        private List<Node> vertices;
        private List<Arc> arcs;

        public MyGraph() {
            vertices = new ArrayList<>();
            arcs = new ArrayList<>();
        }

        /* If a given Vertex can be converted into
         * a valid Node, it do so and returns Node
         * Otherwise it returns null
         */
        Node validateVertex(Vertex<V> vertex) {
            try { return (Node) vertex; }
            catch (ClassCastException e) { return null; }
        }

        /* If a given Edge can be converted into
         * a valid Arc, it do so and returns Arc
         * Otherwise it returns null
         */
        Arc validateEdge(Edge<V, E> edge) {
            try { return (Arc) edge; }
            catch (ClassCastException e) { return null; }
        }

        @Override
        public int numVertices() {
            return this.vertices.size();
        }

        @Override
        public Iterator<? extends Vertex<V>> vertices() {
            return vertices.iterator();
        }

        @Override
        public int numEdges() {
            return arcs.size();
        }

        @Override
        public Iterator<? extends Edge<V, E>> edges() {
            return arcs.iterator();
        }

        @Override
        public Edge<V, E> getEdge(Vertex<V> f, Vertex<V> s) {
            Node first = validateVertex(f);
            Node second = validateVertex(s);
            if (first == null || second == null)
                throw new IllegalArgumentException("You put wrong vertices!");

            Node current = first.adjList.size() < second.adjList.size() ? first : second;
            Node opposite = first.adjList.size() < second.adjList.size() ? first : second;

            for (Arc arc : current.adjList)
                if (opposite(current, arc) == opposite)
                    return arc;

            return null;
        }

        @Override
        public Iterator<? super Vertex<V>> endVertices(Edge<V, E> e) {
            return e.endVertices().iterator();
        }

        @Override
        public Vertex<V> opposite(Vertex<V> v, Edge<V, E> e) {
            return e.endVertices().get(0) == v ? e.endVertices().get(1) : e.endVertices().get(0);
        }

        @Override
        public int outDegree(Vertex<V> v) {
            Node current = validateVertex(v);
            return current.adjList.size();
        }

        @Override
        public int inDegree(Vertex<V> v) {
            Node current = validateVertex(v);
            return current.adjList.size();
        }

        @Override
        public Iterator<? extends Edge<V, E>> outgoingEdges(Vertex<V> v) {
            Node current = validateVertex(v);
            return current.adjList.iterator();
        }

        @Override
        public Iterator<? extends Edge<V, E>> incomingEdges(Vertex<V> v) {
            Node current = validateVertex(v);
            return current.adjList.iterator();
        }

        @Override
        public Vertex<V> insertVertex(V v) {
            Node result = new Node(v);
            result.setPosition(vertices.size());
            vertices.add(result);
            return result;
        }

        @Override
        public Edge<V, E> insertEdge(E e, Vertex<V> f, Vertex<V> s) {
            Node first = validateVertex(f);
            Node second = validateVertex(s);
            if (first == null || second == null)
                throw new IllegalArgumentException("You put wrong vertices!");

            Arc result = new Arc(first, second, e, arcs.size());
            first.adjList.add(result);
            second.adjList.add(result);

            return result;
        }

        @Override
        public void removeVertex(Vertex<V> v) {
            Node current = validateVertex(v);
            if (current == null)
                throw new IllegalArgumentException("You put a wrong vertex!");

            for (Arc arc : current.adjList) arcs.remove(arc.position);
        }

        @Override
        public void removeEdge(Edge<V, E> e) {
            Node first = validateVertex(e.endVertices().get(0));
            Node second = validateVertex(e.endVertices().get(1));
            if (first == null || second == null)
                throw new IllegalArgumentException("You put wrong vertices!");

            Arc arc = validateEdge(e);
            if (arc == null)
                throw new IllegalArgumentException("You put a wrong arc!");

            first.getAdjList().remove(arc.posInFirst);
            second.getAdjList().remove(arc.posInSecond);

            arcs.remove(arc.position);
        }

        /* Returns the first occurrence of vertex with some
         * value in vertices list
         */
        public Vertex<V> getVertexByValue(V value) {
            for (Node node: vertices) if (node.getValue().equals(value)) return node;
            return null;
        }

    }

    public static void main(String[] args) {
        MyGraph<String, Integer> myGraph = new MyGraph<>();
        MyArrayHeap<String, Integer> myArrayHeap = new MyArrayHeap<>(   HEAP_TYPE.MIN_HEAP);

        InputStream is = null;
        InputStreamReader isr = null;
            BufferedReader br = null;
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            is = new FileInputStream("cities.txt");
            isr = new InputStreamReader(is, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            fos = new FileOutputStream("around.txt");
            bw = new BufferedWriter(new OutputStreamWriter(fos));
        }
        catch (Exception e) {

        }

        String line;
        assert (br != null);
        assert (bw != null);
        try {
            line = br.readLine();
            for (String word: line.split(" ")) {
                myGraph.insertVertex(word);
            }

            line = br.readLine();
            String nextWord = null;
            for (String word: line.split(" ")) {
                if (nextWord == null) nextWord = word;
                else {
                    myGraph.insertEdge(1, myGraph.getVertexByValue(nextWord), myGraph.getVertexByValue(word));
                    nextWord = null;
                }
            }

            Function<String, List<String>> adjFunction = s -> {
                List<String> result = new ArrayList<>();
                if (myGraph.getVertexByValue(s) == null) { result.add(" "); return result; }
                Iterator<? extends Vertex<String>> iterator = myGraph.getVertexByValue(s).getAdjVertices();
                while(iterator.hasNext()) {
                    myArrayHeap.add(iterator.next().getValue(), 0);
                }
                while(myArrayHeap.size() != 0)
                    result.add(myArrayHeap.removeMost().getElement().getKey());

                return result;
            };

            String[] cities = {"Donetsk-DU", "Kiev-U", "Lviv-U", "Batumi-G", "Rostov-R"};
            for (String city: cities) {
                List<String> adjList = adjFunction.apply(city);
                for (String s: adjList) bw.write(s + " ");
                bw.write("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
