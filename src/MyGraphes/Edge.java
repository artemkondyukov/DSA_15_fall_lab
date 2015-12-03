package MyGraphes;

import java.util.Iterator;
import java.util.List;

/**
 * Created by artemka on 11/24/15.
 */
public interface Edge<V, E> {
    List<Vertex<V>> endVertices();
    E getValue();
}
