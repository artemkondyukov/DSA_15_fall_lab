package MyTrees;

import java.util.Iterator;

/**
 * Created by artemka on 10/13/15.
 */
public class MyLinkedBinarySearchTree<E extends Comparable <? super E> > implements TreeIterable {
    protected MyLinkedBinaryTree<E> hidden;

    public MyLinkedBinarySearchTree() {
        hidden = new MyLinkedBinaryTree<>();        // we want to hide methods addChildren etc. for encapsulation
                                                    // reasons so we don't extend this class
    }

    /*
     * @returns the abstract position of an element with this value
     */
    public Position<E> find(E value) {
        Position<E> currentNode = hidden.root();
        return findInSubtree(currentNode, value);
    }

    /*
     * recursive implementation of finding element by value in certain subtree
     * @subtreeRoot is a root of some subtree
     * @value is what we are finding
     */
    @SuppressWarnings("unchecked")
    private Position<E> findInSubtree(Position<E> subtreeRoot, E value) {
        if (subtreeRoot == null) return null;
        if (value.equals(subtreeRoot.getElement())) {
            return subtreeRoot;
        }
        if (hidden.numChildren(subtreeRoot) == 0) return null;
        if (value.compareTo(subtreeRoot.getElement()) > 0) {
            return findInSubtree(hidden.getRightChild(subtreeRoot), value);
        }
        if (value.compareTo(subtreeRoot.getElement()) < 0) {
            return findInSubtree(hidden.getLeftChild(subtreeRoot), value);
        }
        return null;
    }


    /*
     * Adds value to a proper (in terms of BST) place
     * @value value to add
     * @returns abstract position
     */
    @SuppressWarnings("unchecked")
    public Position<E> add(E value) {
        if (find(value) != null) return null;
        Position<E> currentNode = hidden.root();
        if (currentNode == null) {
            hidden.addRoot(value);
            return hidden.root();
        }
        while((value.compareTo(currentNode.getElement()) > 0 &&
                hidden.getRightChild(currentNode) != null) ||
                (value.compareTo(currentNode.getElement()) < 0 &&
                hidden.getLeftChild(currentNode) != null)) {
            // We don't care about compareTo returning 0, because we are sure that
            // there is no element in tree with getElement returning value
            if (value.compareTo(currentNode.getElement()) > 0)
                currentNode = hidden.getRightChild(currentNode);
            else
                currentNode = hidden.getLeftChild(currentNode);
        }
        if (value.compareTo(currentNode.getElement()) > 0)
            return hidden.addRightChild(currentNode, value);
        else
            return hidden.addLeftChild(currentNode, value);
    }

    /*
     * removes an element with a certain value from tree
     * @value value to delete
     * @returns abstract position which is no more in tree, so
     * cannot be used for getting children etc.
     */
    @SuppressWarnings("unchecked")
    public Position<E> delete(E value) {
        Position<E> currentNode = find(value);
        if (currentNode == null) return null;

        Position<E> parent = hidden.parent(currentNode);

        if(hidden.numChildren(currentNode) == 0) {
            // we try to delete the root
            if (parent == null) { hidden.removeRoot(); return currentNode; }
            else {
                AbstractBinaryTree.Direction direction = AbstractBinaryTree.Direction.RIGHT;
                if (hidden.getLeftChild(parent) == currentNode)
                    direction = AbstractBinaryTree.Direction.LEFT;

                hidden.removeChild(parent, direction);
                return currentNode;
            }
        }
        else if (hidden.numChildren(currentNode) == 1) {
            Position<E> childOfCurrent = (hidden.getLeftChild(currentNode) != null)
                    ? hidden.removeLeftChild(currentNode)
                    : hidden.removeRightChild(currentNode);

            if (parent == null) {
                hidden.removeRoot();
                hidden.setRoot(childOfCurrent);
                return currentNode;
            }
            else {
                AbstractBinaryTree.Direction direction = AbstractBinaryTree.Direction.RIGHT;
                if (hidden.getLeftChild(parent) == currentNode)
                    direction = AbstractBinaryTree.Direction.LEFT;

                hidden.removeChild(parent, direction);
                hidden.appendSubtree(parent, childOfCurrent, direction);
                return currentNode;
            }
        }

        Position<E> predecessor = hidden.predecessor(currentNode);
        Position<E> parentOfPredecessor = hidden.parent(predecessor);

        AbstractBinaryTree.Direction direction = AbstractBinaryTree.Direction.RIGHT;
        if (hidden.getLeftChild(parentOfPredecessor) == predecessor)
            direction = AbstractBinaryTree.Direction.LEFT;

        predecessor = hidden.removeChild(parentOfPredecessor, direction);
        Position<E> leftChildOfPredecessor = hidden.removeLeftChild(predecessor);
        hidden.appendSubtree(parentOfPredecessor, leftChildOfPredecessor, direction);

        Position<E> leftChildOfCurr = hidden.removeLeftChild(currentNode);
        Position<E> rightChildOfCurr = hidden.removeRightChild(currentNode);
        hidden.appendSubtreeToLeft(predecessor, leftChildOfCurr);
        hidden.appendSubtreeToRight(predecessor, rightChildOfCurr);

        if (parent == null) {                           // we try to delete the root
            hidden.removeRoot();
            hidden.setRoot(predecessor);
        }
        else {
            direction = hidden.getLeftChild(parent) == currentNode
                    ? AbstractBinaryTree.Direction.LEFT
                    : AbstractBinaryTree.Direction.RIGHT;
            hidden.removeChild(parent, direction);
            hidden.appendSubtree(parent, predecessor, direction);
        }

        return currentNode;
    }

    public void draw() {
        hidden.draw();
    }

    public int size() {
        return hidden.size();
    }

    @Override
    public Iterator iterator() {
        return hidden.iterator();
    }

    @Override
    public Iterable<Position <E> > positions() {
        return hidden.positions();
    }

    @Override
    public Iterable<Position <E> > preorder() {
        return hidden.preorder();
    }

    @Override
    public Iterable<Position <E> > postorder() {
        return hidden.postorder();
    }

    public Iterable<Position <E> > inorder() {
        return hidden.inorder();
    }

    @Override
    public Iterable<Position <E> > breadthFirst() {
        return hidden.breadthFirst();
    }
}
