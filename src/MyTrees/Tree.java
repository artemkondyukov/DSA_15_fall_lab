package MyTrees;

import java.util.Iterator;

/**
 * Created by artemka on 10/12/15.
 */
public interface Tree <E> extends TreeIterable<E> {
    Position<E> root();
    Position<E> setRoot(Position<E> p) throws IllegalStateException;
    Position<E> addRoot(E value) throws IllegalStateException;
    Position<E> removeRoot();

    Position<E> parent(Position<E> p) throws IllegalArgumentException; //throws if p is not from the tree
    Position<E> validate(Position<E> p);

    Iterable<Position<E> > children(Position<E> p)
            throws IllegalArgumentException; //throws if p is not from the tree
    int numChildren(Position<E> p)
            throws IllegalArgumentException; //throws if p is not from the tree
    Position<E> remove(Position<E> p)
            throws IllegalArgumentException; //throws if p is not from the tree

    default boolean isInternal(Position<E> p) throws IllegalArgumentException {
        //throws if p is not from the tree
        return numChildren(p) > 0;
    }

    default boolean isExternal(Position<E> p) throws IllegalArgumentException {
        //throws if p is not from the tree
        return numChildren(p) == 0;
    }

    default boolean isRoot(Position<E> p) throws IllegalArgumentException {
        //throws if p is not from the tree
        return parent(p) == null;
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    int height();
    int heightOfSubtree(Position<E> p);

    int size();

    void draw();
}
