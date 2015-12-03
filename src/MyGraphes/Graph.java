package MyGraphes;

import java.util.Iterator;

/**
 * Created by artemka on 11/24/15.
 */
public interface Graph<V, E> {
    int numVertices();
    Iterator<? extends Vertex<V>> vertices();

    int numEdges();
    Iterator<? extends Edge<V, E>> edges();

    Edge<V, E> getEdge(Vertex <V> f, Vertex <V> s);
    Iterator<? super Vertex<V>> endVertices(Edge<V, E> e);

    Vertex<V> opposite(Vertex<V> v, Edge<V, E> e);
    int outDegree(Vertex<V> v);
    int inDegree(Vertex<V> v);

    Iterator<? extends Edge<V, E>> outgoingEdges(Vertex<V> v);
    Iterator<? extends Edge<V, E>> incomingEdges(Vertex<V> v);

    Vertex<V> insertVertex(V v);
    Edge<V, E> insertEdge(E e, Vertex<V> f, Vertex<V> s);

    boolean isConnected(Vertex<V> f, Vertex<V> s);
    int traverse(Vertex<V> f);  // Returns number of visited nodes

    void removeVertex(Vertex<V> v);
    void removeEdge(Edge<V, E> e);
}
