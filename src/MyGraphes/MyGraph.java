package MyGraphes;

import MyTrees.Position;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by artemka on 11/24/15.
 * Adjacency list implementation of graph for
 * graded assignment #4 (DSA Fall 15)
 * It's undirected graph by now
 */
public class MyGraph<V, E> implements Graph<V, E> {
    protected class Node implements Vertex<V> {
        private V value;
        private int position; // Describes position of the Node in MyGraph's storage
        private boolean visited;

        // Adjacency list
        private MyDoublyLinkedList<Arc> adjList;

        public Node(V value) {
            this.value = value;
            adjList = new MyDoublyLinkedList<>();
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public boolean getVisited() {
            return visited;
        }

        @Override
        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        @Override
        public Iterator<? extends Vertex<V>> getAdjVertices() {
            List<Vertex<V>> result = new ArrayList<>();
            for (Arc arc: adjList) {
                result.add(opposite(this, arc));
            }
            return result.iterator();
        }

        public MyDoublyLinkedList<Arc> getAdjList() {
            return adjList;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    protected class Arc implements MyGraphes.Edge<V, E> {
        private Position<Arc> position; // Describes position of the Arc in MyGraph's storage
        private Position<Arc> posInFirst; // Describes position of the Arc in First Node adjList
        private Position<Arc> posInSecond; // Describes position of the Arc in Second Node adjList

        private E value;
        private Node first;
        private Node second;

        public Arc(Node first, Node second, E value, int position) {
            this.first = first;
            this.second = second;
            this.value = value;
//            posInFirst = first.ad
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

    private MyDoublyLinkedList<Node> vertices;
    private MyDoublyLinkedList<Arc> deletedG;
    private MyDoublyLinkedList<Arc> deletedU;
    private MyDoublyLinkedList<Arc> arcs;

    public MyGraph() {
        vertices = new MyDoublyLinkedList<>();
        deletedG = new MyDoublyLinkedList<>();
        deletedU = new MyDoublyLinkedList<>();
        arcs = new MyDoublyLinkedList<>();
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
    public Iterator<? extends MyGraphes.Edge<V, E>> edges() {
        return arcs.iterator();
    }

    @Override
    public MyGraphes.Edge<V, E> getEdge(Vertex<V> f, Vertex<V> s) {
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
    public Iterator<? super Vertex<V>> endVertices(MyGraphes.Edge<V, E> e) {
        return e.endVertices().iterator();
    }

    @Override
    public Vertex<V> opposite(Vertex<V> v, MyGraphes.Edge<V, E> e) {
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
    public Iterator<? extends MyGraphes.Edge<V, E>> outgoingEdges(Vertex<V> v) {
        Node current = validateVertex(v);
        return current.adjList.iterator();
    }

    @Override
    public Iterator<? extends MyGraphes.Edge<V, E>> incomingEdges(Vertex<V> v) {
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
    public MyGraphes.Edge<V, E> insertEdge(E e, Vertex<V> f, Vertex<V> s) {
        Node first = validateVertex(f);
        Node second = validateVertex(s);
        if (first == null || second == null)
            throw new IllegalArgumentException("You put wrong vertices!");

        Arc result = new Arc(first, second, e, arcs.size());
        result.posInFirst = first.adjList.add(result);
        result.posInSecond = second.adjList.add(result);
        result.position = arcs.add(result);

        return result;
    }

    @Override
    public boolean isConnected(Vertex<V> f, Vertex<V> s) {
        resetVisited();
        boolean result = isConnectedDFS(f, s);
        resetVisited();
        return result;
    }

    private boolean isConnectedDFS(Vertex<V> f, Vertex<V> s) {
        f.setVisited(true);
        while (f.getAdjVertices().hasNext()) {
            Vertex<V> next = f.getAdjVertices().next();
            if (next.getVisited()) continue;
            if (next == s) return true;
            if (isConnected(next, s)) return true;
        }
        f.setVisited(false);
        return false;
    }

    private int traverseDFS(Vertex<V> f, int result) {
        System.out.println("Traverse DFS, vertex: " + f.getValue().toString());
        f.setVisited(true);
        result++;
        System.out.println(result);
        Iterator<? extends Vertex<V>> iterator = f.getAdjVertices();
        while (iterator.hasNext()) {
            Vertex<V> next = iterator.next();
            if (next.getVisited()) continue;
            result = traverseDFS(next, result);
        }
        return result;
    }

    @Override
    public int traverse(Vertex<V> f) {
        resetVisited();
        int result = traverseDFS(f, 0);
        resetVisited();
        return result;
    }

    public boolean isConnective() {
        return traverse(vertices.iterator().next()) == vertices.size();
    }

    public List<Vertex<V>> shortestPath(Vertex<V> f, Vertex<V> s) {
        resetVisited();
        Map<Vertex<V>, Vertex<V>> parents = new LinkedHashMap<>();

        List<Vertex<V>> result = new ArrayList<>();
        MyDoublyLinkedList<Vertex<V>> queue = new MyDoublyLinkedList<>();
        queue.add(f);
        f.setVisited(true);
        parents.put(f, null);
        Iterator<? extends Vertex<V>> iterator = f.getAdjVertices();
        boolean found = false;
        while(queue.size() > 0 && !found) {

            // Go through children of current node
            while (iterator.hasNext()) {
                Vertex<V> current = iterator.next();
                if (current.getVisited()) continue;
                current.setVisited(true);
                // Put'em into queue in order to get BFS
                queue.add(current);
                // Put value in parents map in order to obtain a path
                parents.put(current, f);
                // Go away if you've already found a path
                if (current == s) { found = true; break; }
            }
            f = queue.iterator().next();
            iterator = f.getAdjVertices();
            queue.deleteFirst();
        }

        // Obtain the path in reverse order
        while(s != null) {
            result.add(s);
            s = parents.get(s);
        }
        // And reverse it
        Collections.reverse(result);

        return result;
    }

    private void removeU() {
//        deletedU = new ArrayList<>(vertices.stream().filter(node -> node.getValue().toString().contains("-U")).
//                flatMap(n -> n.getAdjList().stream()).collect(Collectors.toSet()));
//        deletedU.stream().forEach(this::removeEdge);
        deletedU = new MyDoublyLinkedList<>();
        for(Node node: vertices) {
            if (node.getValue().toString().contains("-U")) {
                for (Arc arc: node.adjList) {
                    deletedU.add(arc);
                    arc.first.adjList.delete(arc.posInFirst);
                    arc.second.adjList.delete(arc.posInSecond);
                    arcs.delete(arc.position);
                }
            }
        }
    }

    private void removeG() {
//        deletedG = new ArrayList<>(vertices.stream().filter(node -> node.getValue().toString().contains("-G")).
//                flatMap(n -> n.getAdjList().stream()).collect(Collectors.toSet()));
//        deletedG.stream().forEach(this::removeEdge);
        deletedG = new MyDoublyLinkedList<>();
        for(Node node: vertices) {
            if (node.getValue().toString().contains("-G")) {
                for (Arc arc: node.adjList) {
                    deletedG.add(arc);
                    arc.first.adjList.delete(arc.posInFirst);
                    arc.second.adjList.delete(arc.posInSecond);
                    arcs.delete(arc.position);
                }
            }
        }
    }

    private void addU() {
//        deletedU.stream().map(arc -> insertEdge(arc.value, arc.first, arc.second));
        for (Arc arc: deletedU) insertEdge(arc.value, arc.first, arc.second);
    }

    private void addG() {
//        deletedG.stream().map(arc -> insertEdge(arc.value, arc.first, arc.second));
        for (Arc arc: deletedG) insertEdge(arc.value, arc.first, arc.second);
    }

    /* Returns whether it's possible to start from a vertex
     * and return to it holding current constraints
     */
//    public boolean traverseConstraint(Vertex<V> f) {
//        return traverseConstraintDFS(f, f, 0);
//    }
//
//    private boolean traverseConstraintDFS(Vertex<V> f, Vertex<V> s, int result) {
//        System.out.println("Traverse DFS constraint, vertex: " + f.getValue().toString());
//        result++;
//        System.out.println(result);
//        f.setVisited(true);
//        Iterator<? extends Vertex<V>> iterator = f.getAdjVertices();
//        while (iterator.hasNext()) {
//            Vertex<V> next = iterator.next();
//            if (next.getVisited()) continue;
//            if ((next.getValue().toString().contains("-DU") && f.getValue().toString().contains("-R")) ||
//                    (f.getValue().toString().contains("-DU") && next.getValue().toString().contains("-R"))) {
//                removeU();
//            }
//            if ((next.getValue().toString().contains("-DG") && f.getValue().toString().contains("-R")) ||
//                    (f.getValue().toString().contains("-DG") && next.getValue().toString().contains("-R"))) {
//                removeG();
//            }
//            boolean tmp = traverseConstraintDFS(next, s, result);
//            if (tmp) {
//                System.out.println("Success, vertex: " + f.getValue().toString());
//                return true;                     // Success
//            }
//
//            // Unsuccessful, let's go back
//            if ((next.getValue().toString().contains("-DU") && f.getValue().toString().contains("-R")) ||
//                    (f.getValue().toString().contains("-DU") && next.getValue().toString().contains("-R"))) {
//                addU();
//            }
//            if ((next.getValue().toString().contains("-DG") && f.getValue().toString().contains("-R")) ||
//                    (f.getValue().toString().contains("-DG") && next.getValue().toString().contains("-R"))) {
//                addG();
//            }
//        }
//        if (result == numVertices())
//            if (isConnected(f, s)) return true;       // Success
//        f.setVisited(false);
//        return false;
//    }

    @Override
    public void removeVertex(Vertex<V> v) {
        Node current = validateVertex(v);
        if (current == null)
            throw new IllegalArgumentException("You put a wrong vertex!");

        for (Arc arc : current.adjList) arcs.delete(arc.position);
    }

    @Override
    public void removeEdge(MyGraphes.Edge<V, E> e) {
        Node first = validateVertex(e.endVertices().get(0));
        Node second = validateVertex(e.endVertices().get(1));
        if (first == null || second == null)
            throw new IllegalArgumentException("You put wrong vertices!");

        Arc arc = validateEdge(e);
        if (arc == null)
            throw new IllegalArgumentException("You put a wrong arc!");

        first.getAdjList().delete(arc.posInFirst);
        second.getAdjList().delete(arc.posInSecond);

        arcs.delete(arc.position);
    }

    /* Returns the first occurrence of vertex with some
     * value in vertices list
     */
    public Vertex<V> getVertexByValue(V value) {
        for (Node node: vertices)
            if (node.getValue().equals(value)) return node;
        return null;
    }

    /* Sets visited for all nodes to false
     */
    public void resetVisited() {
        vertices.forEach(s -> s.setVisited(false));
    }

}
