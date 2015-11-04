package MyTrees;

/**
 * Created by artemka on 10/20/15.
 */
public class MyAVLLinkedTree<E extends Comparable <? super E> > extends MyLinkedBinarySearchTree<E> {

    @SuppressWarnings("unchecked")
    private Position<E> addRecursive(Position<E> p, E value) {
        if (value.compareTo(p.getElement()) < 0) {
            if (hidden.getLeftChild(p) == null) {
                hidden.addLeftChild(p, value);
                return p;
            }
            Position<E> subtree = hidden.removeLeftChild(p);
            subtree = addRecursive(subtree, value);
            hidden.appendSubtreeToLeft(p, subtree);
        }
        else {
            if (hidden.getRightChild(p) == null) {
                hidden.addRightChild(p, value);
                return p;
            }
            Position<E> subtree = hidden.removeRightChild(p);
            subtree = addRecursive(subtree, value);
            hidden.appendSubtreeToRight(p, subtree);
        }
        return balance(p);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Position<E> add(E value) {
        if (find(value) != null) return null;
        Position<E> currentNode = hidden.root();
        if (currentNode == null) {
            hidden.addRoot(value);
            return hidden.root();
        }

        Position<E> newRoot = addRecursive(hidden.root(), value);
        hidden.removeRoot();
        hidden.setRoot(newRoot);
        return find(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Position<E> delete(E value) {
        Position<E> current = super.find(value);
        Position<E> parent = hidden.parent(current);
        Position<E> parentOfPredecessor = hidden.predecessor(super.find(value)) != null
                ? hidden.parent(hidden.predecessor(super.find(value)))
                : hidden.parent(super.find(value));
//        if (parentOfPredecessor == find(value)) parentOfPredecessor = hidden.parent(parentOfPredecessor);

        AbstractBinaryTree.Direction direction = AbstractBinaryTree.Direction.LEFT;
        if (parent != null)
            direction = (hidden.getLeftChild(parent) != null &&
                    hidden.getLeftChild(parent).getElement().compareTo(value) == 0)
                    ? AbstractBinaryTree.Direction.LEFT
                    : AbstractBinaryTree.Direction.RIGHT;

        Position<E> result = super.delete(value);
        if (parentOfPredecessor == current) {
            parentOfPredecessor = hidden.getChild(parent, direction);
        }

        while (hidden.parent(parentOfPredecessor) != null) {
            direction = hidden.getLeftChild(hidden.parent(parentOfPredecessor)) == parentOfPredecessor
                    ? AbstractBinaryTree.Direction.LEFT
                    : AbstractBinaryTree.Direction.RIGHT;

            Position<E> grandpa = hidden.parent(parentOfPredecessor);
            Position<E> tmp = hidden.removeChild(grandpa, direction);
            tmp = balance(tmp);
            hidden.appendSubtree(grandpa, tmp, direction);
            parentOfPredecessor = hidden.parent(parentOfPredecessor);
        }

//        if (parent != null) {
//            if (hidden.getChild(parent, direction) != null) {
//                Position<E> balancedChild = hidden.removeChild(parent, direction);
//                balancedChild = balance(balancedChild);
//                hidden.appendSubtree(parent, balancedChild, direction);
//            }
//            Position<E> child = parent;
//            parent = hidden.parent(parent);
//            while (parent != null) {
//                direction = hidden.getLeftChild(parent) == child
//                        ? AbstractBinaryTree.Direction.LEFT
//                        : AbstractBinaryTree.Direction.RIGHT;
//                Position<E> balancedChild = hidden.removeChild(parent, direction);
//                balancedChild = balance(balancedChild);
//                hidden.appendSubtree(parent, balancedChild, direction);
//                child = parent;
//                parent = hidden.parent(parent);
//            }
//        }
        if (hidden.root() != null) {
            Position<E> newRoot = balance(hidden.root());
            hidden.removeRoot();
            hidden.setRoot(newRoot);
        }
        return result;
    }

    public int disbalanceFactor(Position<E> p) {
        if(hidden.validate(p) == null) throw new IllegalArgumentException("The subtree is not from the tree");
        return hidden.heightOfSubtree(hidden.getRightChild(p)) - hidden.heightOfSubtree(hidden.getLeftChild(p));
    }

    private Position<E> rotateRight(Position<E> p) {
        if(hidden.validate(p) == null) throw new IllegalArgumentException("The subtree is not from the tree");
        Position<E> q = hidden.getLeftChild(p) != null ? hidden.removeLeftChild(p) : null;
        Position<E> qright = hidden.getRightChild(q) != null ? hidden.removeRightChild(q) : null;
        hidden.appendSubtreeToLeft(p, qright);
        hidden.appendSubtreeToRight(q, p);
        return q;
    }

    private Position<E> rotateLeft(Position<E> q) {
        if(hidden.validate(q) == null) throw new IllegalArgumentException("The subtree is not from the tree");
        Position<E> p = hidden.getRightChild(q) != null ? hidden.removeRightChild(q) : null;
        Position<E> pleft = hidden.getLeftChild(p) != null ? hidden.removeLeftChild(p) : null;
        hidden.appendSubtreeToRight(q, pleft);
        hidden.appendSubtreeToLeft(p, q);
        return p;
    }

    private Position<E> balance(Position<E> p) {
        if (p == null) return null;
        if (disbalanceFactor(p) == 2) {
            if (disbalanceFactor(hidden.getRightChild(p)) < 0) {
                Position<E> pright = hidden.removeRightChild(p);
                pright = rotateRight(pright);
                hidden.appendSubtreeToRight(p, pright);
            }
            return rotateLeft(p);
        }
        if (disbalanceFactor(p) == -2) {
            if (disbalanceFactor(hidden.getLeftChild(p)) > 0) {
                Position<E> pleft = hidden.removeLeftChild(p);
                pleft = rotateLeft(pleft);
                hidden.appendSubtreeToLeft(p, pleft);
            }
            return rotateRight(p);
        }
        return p;
    }

    @Override
    public void draw() { hidden.draw(); }

    public int height () {
        return hidden.height();
    }
}
