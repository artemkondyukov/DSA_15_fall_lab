package MyTrees;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artemka on 10/13/15.
 */
public abstract class AbstractBinaryTree<E> extends AbstractTree<E> {
    public enum Direction {
        LEFT, RIGHT
    }

    public abstract Position<E> getChild(Position<E> p, Direction d);
    public abstract Position<E> addChild(Position<E> p, E value, Direction d);
    public abstract Position<E> removeChild(Position<E> p, Direction d);

    public abstract Position<E> getLeftChild(Position<E> p);
    public abstract Position<E> getRightChild(Position<E> p);
    public abstract Position<E> addLeftChild(Position<E> p, E value);
    public abstract Position<E> addRightChild(Position<E> p, E value);
    public abstract Position<E> removeLeftChild(Position<E> p);
    public abstract Position<E> removeRightChild(Position<E> p);

    public Iterable<Position<E>> inorder() {
        if (isEmpty()) return null;
        List<Position<E> > result = new ArrayList<>();
        inorderSubtree(root(), result);
        return result;
    }

    /*
     * Recursive function for convenient inorder traversal
     * @p is a root of subtree to be traversal
     * @snapshot is a collection where we place elements to
     */
    public void inorderSubtree(Position<E> p, List<Position <E> > snapshot) {
        if (getLeftChild(p) != null)
            inorderSubtree(getLeftChild(p), snapshot);
        snapshot.add(p);
        if (getRightChild(p) != null)
            inorderSubtree(getRightChild(p), snapshot);
    }
}
