package MyTrees;

import java.util.*;

/**
 * Created by artemka on 10/12/15.
 */
public abstract class AbstractTree<E> implements Tree<E> {

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
        List<Position<E> > result = new ArrayList<>();
        preorderSubtree(root(), result);
        return result;
    }

    /*
     * Recursive function for convenient preorder traversal
     * @p is a root of subtree to be traversal
     * @snapshot is a collection where we place elements to
     */
    public void preorderSubtree(Position<E> p, List<Position<E> > snapshot) {
        snapshot.add(p);
        for (Position<E> child: children(p)) {
            preorderSubtree(child, snapshot);
        }
    }

    @Override
    public Iterable<Position<E>> postorder() {
        if (isEmpty()) return null;
        List<Position<E> > result = new ArrayList<>();
        postorderSubtree(root(), result);
        return result;
    }

    /*
     * Recursive function for convenient preorder traversal
     * @p is a root of subtree to be traversal
     * @snapshot is a collection where we place elements to
     */
    public void postorderSubtree(Position<E> p, List<Position <E> > snapshot) {
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
        List<Position<E> > result = new ArrayList<>();
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
