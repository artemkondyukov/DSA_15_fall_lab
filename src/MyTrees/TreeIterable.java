package MyTrees;

import java.util.Iterator;

/**
 * Created by artemka on 11/3/15.
 */
public interface TreeIterable<E> extends Iterable<E> {
    Iterator<E> iterator();
    Iterable<Position<E> > positions();
    Iterable<Position<E> > preorder();
    Iterable<Position<E> > postorder();
    Iterable<Position<E> > breadthFirst();
}
