package MyGraphes;

import java.util.Iterator;

/**
 * Created by artemka on 11/24/15.
 */
public interface Vertex<V> {
    V getValue();
    boolean getVisited();
    void setVisited(boolean visited);
    Iterator<? extends Vertex<V>> getAdjVertices();
}
