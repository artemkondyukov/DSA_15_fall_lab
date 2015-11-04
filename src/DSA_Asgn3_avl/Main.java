package DSA_Asgn3_avl;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by artemka on 11/3/15.
 */
public class Main {
    public interface Position <E> {
        public E getElement();
    }

    public interface Tree <E> extends Iterable<E>{
        Position<E> root();
        Position<E> setRoot(Position<E> p) throws IllegalStateException;
        Position<E> addRoot(E value) throws IllegalStateException;
        Position<E> parent(Position<E> p) throws IllegalArgumentException; //throws if p is not from the tree
        Position<E> validate(Position<E> p);

        Iterable<Position<E>> children(Position<E> p)
                throws IllegalArgumentException; //throws if p is not from the tree
        int numChildren(Position<E> p)
                throws IllegalArgumentException; //throws if p is not from the tree
        Position<E> remove(Position<E> p)
                throws IllegalArgumentException; //throws if p is not from the tree

        boolean isInternal(Position<E> p)
                throws IllegalArgumentException;//throws if p is not from the tree
        boolean isExternal(Position<E> p)
                throws IllegalArgumentException;//throws if p is not from the tree
        boolean isRoot(Position<E> p)
                throws IllegalArgumentException;//throws if p is not from the tree

        int height();
        int heightOfSubtree(Position<E> p);

        int size();
        boolean isEmpty();
        Iterator<E> iterator();
        Iterable<Position<E>> positions();
        Iterable<Position<E>> preorder();
        Iterable<Position<E>> postorder();
        Iterable<Position<E>> breadthFirst();

        void draw();
    }

    public static abstract class AbstractTree<E> implements Tree<E> {

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
        public void preorderSubtree(Position<E> p, List<Position<E>> snapshot) {
            snapshot.add(p);
            for (Position<E> child: children(p)) {
                preorderSubtree(child, snapshot);
            }
        }

        @Override
        public Iterable<Position<E>> postorder() {
            if (isEmpty()) return null;
            List<Position<E>> result = new ArrayList<>();
            postorderSubtree(root(), result);
            return result;
        }

        /*
         * Recursive function for convenient preorder traversal
         * @p is a root of subtree to be traversal
         * @snapshot is a collection where we place elements to
         */
        public void postorderSubtree(Position<E> p, List<Position<E>> snapshot) {
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
            List<Position<E>> result = new ArrayList<>();
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

    public abstract static class AbstractBinaryTree<E> extends AbstractTree<E> {
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
        public abstract Position<E> removeRoot();
    }

    public static class MyLinkedBinaryTree<E> extends AbstractBinaryTree<E> {
        protected static class Node<E> implements Position<E> {
            private Node<E> left;
            private Node<E> right;
            private Node<E> parent;
            private E value;
            private int height;

            public Node(E value, Node<E> parent) {          // If Node is a root we set parent = null
                this.value = value;                         // And if parent is equals to this, it means DEFUNCT
                this.parent = parent;
                this.height = 0;
            }

            public int getHeight() {
                return height;
            }

            public void increaseHeight(int val) {
                this.height += val;
                Node<E> tmp = parent;
                while (tmp != null && tmp != this) {
                    tmp.height += val;
                    tmp = tmp.parent;
                }
            }

            @Override
            public E getElement() { return this.value; }
        }

        private Node<E> root;
        private int size;

        @Override
        public int height() {
            return root.getHeight() + 1;
        }

        @Override
        public Position<E> getLeftChild(Position<E> p) {
            if (validate(p) == null) throw new IllegalArgumentException("The position is not from the tree");
            Node<E> currentNode = (Node<E>)p;
            return currentNode.left;
        }

        @Override
        public Position<E> getRightChild(Position<E> p) {
            if (validate(p) == null) throw new IllegalArgumentException("The position is not from the tree");
            Node<E> currentNode = (Node<E>)p;
            return currentNode.right;
        }

        @Override
        public Position<E> getChild(Position<E> p, Direction d) {
            switch (d) {
                case LEFT: return getLeftChild(p);
                case RIGHT: return getRightChild(p);
            }
            return null;                                // I hope this won't happen
        }

        @Override
        public Position<E> addLeftChild(Position<E> p, E value) {
            if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
            if (getLeftChild(p) != null) throw new IllegalArgumentException("The node already has a left child");
            Node<E> currentNode = (Node<E>)p;
            currentNode.increaseHeight(1);
            Node<E> child = new Node<>(value, currentNode);
            size++;
            return currentNode.left = child;
        }

        @Override
        public Position<E> addRightChild(Position<E> p, E value) {
            if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
            if (getRightChild(p) != null) throw new IllegalArgumentException("The node already has a right child");
            Node<E> currentNode = (Node<E>)p;
            Node<E> child = new Node<>(value, currentNode);
            currentNode.increaseHeight(1);
            size++;
            return currentNode.right = child;
        }


        @Override
        public Position<E> addChild(Position<E> p, E value, Direction d) {
            return null;
        }

        @Override
        public Position<E> removeLeftChild(Position<E> p) {
            if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
            Node<E> currentNode = (Node<E>)p;
            if (currentNode.right != null)
                currentNode.increaseHeight(currentNode.right.height < currentNode.left.height
                        ? currentNode.right.height - currentNode.left.height
                        : 0);
            else
                currentNode.increaseHeight(-currentNode.height);
            size -= sizeOfSubtree(currentNode.left);
            currentNode.left.parent = currentNode.left;         // Defunct
            Node<E> result = currentNode.left;
            currentNode.left = null;
            return result;
        }

        @Override
        public Position<E> removeRightChild(Position<E> p) {
            if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
            Node<E> currentNode = (Node<E>)p;
            if (currentNode.left != null)
                currentNode.increaseHeight(currentNode.left.height < currentNode.right.height
                        ? currentNode.left.height - currentNode.right.height
                        : 0);
            else
                currentNode.increaseHeight(-currentNode.height);
            size -= sizeOfSubtree(currentNode.right);
            currentNode.right.parent = currentNode.right;         // Defunct
            Node<E> result = currentNode.right;
            currentNode.right = null;
            return result;
        }

        @Override
        public Position<E> removeChild(Position<E> p, Direction d) {
            switch (d) {
                case LEFT: return removeLeftChild(p);
                case RIGHT: return removeRightChild(p);
            }
            return null;                                // I hope this won't happen
        }

        private int sizeOfSubtree(Position<E> p) {
            if (validate(p) == null) throw new IllegalArgumentException("Position is not from tree");
            Node<E> currentNode = (Node<E>)p;
            int result = 0;
            if (currentNode.left != null)
                result += sizeOfSubtree(currentNode.left);
            if (currentNode.right != null)
                result += sizeOfSubtree(currentNode.right);
            return result + 1;
        }

        @Override
        public Position<E> remove(Position<E> p) throws IllegalArgumentException {
            if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
            Node<E> currentNode = (Node<E>)p;
            if (currentNode.left != null)
                currentNode.left.parent = currentNode.left;         // Defunct
            if (currentNode.right != null)
                currentNode.right.parent = currentNode.right;         // Defunct
            size--;
            if (currentNode == root()) {root = null; return currentNode;}

            if (getLeftChild(currentNode.parent) == currentNode) {
                currentNode.parent.left = null;
            }
            if (getRightChild(currentNode.parent) == currentNode) {
                currentNode.parent.right = null;
            }
            return currentNode;
        }

        public void appendSubtreeToLeft(Position<E> parent, Position<E> subtree) {
            if (subtree == null) return;
            if (validate(parent) == null || validate(subtree) == null)
                throw  new IllegalArgumentException("The position is not from the tree");

            if (getLeftChild(parent) != null)
                throw new IllegalArgumentException("The node already has a left child");

            Node<E> parentNode = (Node<E>) validate(parent);
            Node<E> subtreeNode = (Node<E>) validate(subtree);
            if (parentNode.right != null)
                parentNode.increaseHeight((parentNode.right.height > subtreeNode.getHeight()
                        ? 0
                        : subtreeNode.getHeight() - parentNode.right.height));
            else
                parentNode.increaseHeight(subtreeNode.getHeight() + 1);
            parentNode.left = subtreeNode;
            subtreeNode.parent = parentNode;
            size += sizeOfSubtree(subtreeNode);
        }

        public void appendSubtreeToRight(Position<E> parent, Position<E> subtree) {
            if (subtree == null) return;
            if (validate(parent) == null || validate(subtree) == null)
                throw  new IllegalArgumentException("The position is not from the tree");

            if (getRightChild(parent) != null)
                throw new IllegalArgumentException("The node already has a right child");

            Node<E> parentNode = (Node<E>) validate(parent);
            Node<E> subtreeNode = (Node<E>) validate(subtree);
            if (parentNode.left != null)
                parentNode.increaseHeight((parentNode.left.height > subtreeNode.getHeight()
                        ? 0
                        : subtreeNode.getHeight() - parentNode.left.height));
            else
                parentNode.increaseHeight(subtreeNode.getHeight() + 1);
            parentNode.right = subtreeNode;
            subtreeNode.parent = parentNode;
            size += sizeOfSubtree(subtreeNode);
        }

        public void appendSubtree(Position<E> parent, Position<E> subtree, Direction d) {
            switch (d) {
                case LEFT: appendSubtreeToLeft(parent, subtree); return;
                case RIGHT: appendSubtreeToRight(parent, subtree);
            }
        }

        @Override
        public Position<E> removeRoot() {
            Node<E> tmp = root;
            root = null;
            size = 0;
            return tmp;
        }

        @Override
        public Position<E> root() {
            return root;
        }

        @Override
        public Position<E> setRoot(Position<E> p) throws IllegalStateException {
            if (root != null) throw new IllegalStateException("You try to add root to the tree with root");
            Node<E> newRoot = (Node<E>) validate(p);
            newRoot.parent = null;
            root = newRoot;
            size = sizeOfSubtree(p);
            return newRoot;
        }

        @Override
        public Position<E> addRoot(E value) throws IllegalStateException {
            if (root != null) throw new IllegalStateException("You try to add root to the tree with root");
            Node<E> newRoot = new Node<>(value, null);      // null is the parent of root
            root = newRoot;
            size++;
            return newRoot;
        }

        @Override
        public Position<E> parent(Position<E> p) throws IllegalArgumentException {
            if (validate(p) == null) throw new IllegalArgumentException("The position is not from the tree");
            Node<E> currentNode = (Node<E>)p;
            return currentNode.parent;                          // null if position is root
        }

        @Override
        public Position<E> validate(Position<E> p) {
            if(!(p instanceof Node)) {
                return null;
            }
//        Node<E> currentNode = (Node<E>)p;
//        if (currentNode.parent == currentNode)                      // DEFUNCT
//            return null;
            return p;
        }

        @Override
        public Iterable<Position<E>> children(Position<E> p) throws IllegalArgumentException {
            if (validate(p) == null) throw new IllegalArgumentException("Position is not from the tree");
            List<Position<E>> result = new ArrayList<>();
            if (getLeftChild(p) != null) {
                result.add(getLeftChild(p));
            }
            if (getRightChild(p) != null) {
                result.add(getRightChild(p));
            }
            return result;
        }

        @Override
        public int numChildren(Position p) throws IllegalArgumentException {
            if (validate(p) == null) throw new IllegalArgumentException("Position is not from the tree");
            int result = 0;
            if (getLeftChild(p) != null) {
                result++;
            }
            if (getRightChild(p) != null) {
                result++;
            }
            return result;
        }

        @Override
        public int size() {
            return size;
        }

        private void drawRecursive(String[][] drawPos, Node<E> s, int level, int pos) {
            if(drawPos[level] == null) drawPos[level] = new String[(int)Math.pow(2, level)];
            drawPos[level][pos] = s.value.toString();
            if (s.left != null)
                drawRecursive(drawPos, s.left, level + 1, pos * 2);
            if (s.right != null)
                drawRecursive(drawPos, s.right, level + 1, pos * 2 + 1);
        }

        @Override
        public void draw() {
            String[][] drawPositions = new String[height() + 1][];
            drawRecursive(drawPositions, root, 0, 0);
            int height = height();
            for (int i = 0; i < height; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < (int)(Math.pow(2, height - i - 1)); j++)
                    sb.append("   ");
                System.out.print(sb.toString());
                for (int j = 0; j < drawPositions[i].length; j++) {
                    if (drawPositions[i][j] == null)
                        System.out.print(" ");
                    else
                        System.out.print(drawPositions[i][j]);
                    System.out.print(sb.toString());
                    System.out.print(sb.toString());
                }
                System.out.println();
            }
        }
    }

    public static class MyLinkedBinarySearchTree<E extends Comparable> {
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
            if (parent == null) {                           // we try to delete the root
                Position<E> tmp = hidden.getLeftChild(currentNode);
                while(hidden.getRightChild(tmp) != null) tmp = hidden.getRightChild(tmp);
                Position<E> leftChildOfTmp = (hidden.getLeftChild(tmp) != null) ? hidden.removeLeftChild(tmp) : null;
                Position<E> parentOfTmp = hidden.parent(tmp);
                Position<E> rightChildOfCurrent = hidden.removeRightChild(currentNode);

                if (hidden.getLeftChild(parentOfTmp) == tmp) {
                    tmp = hidden.removeLeftChild(parentOfTmp);
                    hidden.removeRoot();
                    hidden.setRoot(tmp);
                    hidden.appendSubtreeToRight(tmp, rightChildOfCurrent);
                    return currentNode;
                }
                else {
                    tmp = hidden.removeRightChild(parentOfTmp);
                    hidden.appendSubtreeToRight(parentOfTmp, leftChildOfTmp);
                    Position<E> leftChildOfCurrent = hidden.removeLeftChild(currentNode);
                    hidden.removeRoot();
                    hidden.setRoot(tmp);
                    hidden.appendSubtreeToLeft(tmp, leftChildOfCurrent);
                    hidden.appendSubtreeToRight(tmp, rightChildOfCurrent);
                    return currentNode;
                }
            }
            boolean isLeftChild = hidden.getLeftChild(parent) == currentNode;
            if (hidden.numChildren(currentNode) == 1) {
                if (hidden.getLeftChild(currentNode) != null) {
                    // Take the only child of deleting node
                    Position<E> tmp = (hidden.getLeftChild(currentNode) != null) ?
                            hidden.getLeftChild(currentNode) :
                            hidden.getRightChild(currentNode);

                    if (isLeftChild) {
                        hidden.removeLeftChild(parent);
                        hidden.appendSubtreeToLeft(parent, tmp);
                    }
                    else {
                        hidden.removeRightChild(parent);
                        hidden.appendSubtreeToRight(parent, tmp);
                    }
                }
            }
            else if (hidden.numChildren(currentNode) == 0) {
                hidden.remove(currentNode);
            }
            else {
                // Here we take an next to deleting item in inorder traversal
                Position<E> tmp = hidden.getLeftChild(currentNode);
                while(hidden.getRightChild(tmp) != null) tmp = hidden.getRightChild(tmp);
                Position<E> leftChildOfTmp = (hidden.getLeftChild(tmp) != null) ? hidden.removeLeftChild(tmp) : null;
                Position<E> parentOfTmp = hidden.parent(tmp);
                Position<E> rightChildOfCurrent = hidden.removeRightChild(currentNode);

                if (hidden.getLeftChild(parentOfTmp) == tmp) {
                    tmp = hidden.removeLeftChild(parentOfTmp);
                    if (isLeftChild) {
                        hidden.removeLeftChild(parent);
                        hidden.appendSubtreeToLeft(parent, tmp);
                    }
                    else {
                        hidden.removeRightChild(parent);
                        hidden.appendSubtreeToRight(parent, tmp);
                    }
                    hidden.appendSubtreeToRight(tmp, rightChildOfCurrent);
                    return currentNode;
                }
                else {
                    tmp = hidden.removeRightChild(parentOfTmp);
                    hidden.appendSubtreeToRight(parentOfTmp, leftChildOfTmp);
                    Position<E> leftChildOfCurrent = hidden.removeLeftChild(currentNode);
                    if (isLeftChild) {
                        hidden.removeLeftChild(parent);
                        hidden.appendSubtreeToLeft(parent, tmp);
                    }
                    else {
                        hidden.removeRightChild(parent);
                        hidden.appendSubtreeToRight(parent, tmp);
                    }
                    hidden.appendSubtreeToLeft(tmp, leftChildOfCurrent);
                    hidden.appendSubtreeToRight(tmp, rightChildOfCurrent);
                    return currentNode;
                }
//
//            if (isLeftChild) {
//                hidden.removeLeftChild(parent);
//                hidden.appendSubtreeToLeft(parent, tmp);
//            }
//            else {
//                hidden.removeRightChild(parent);
//                hidden.appendSubtreeToRight(parent, tmp);
//            }
//
//            // If currentNode != parentOfTmp
//            if (hidden.getRightChild(parentOfTmp) == null) {
//                hidden.appendSubtreeToLeft(parentOfTmp, leftChildOfTmp);
//                hidden.appendSubtreeToRight(tmp, hidden.getRightChild(currentNode));
//            }
//            else
//                hidden.appendSubtreeToRight(tmp, leftChildOfTmp);
//
//            hidden.appendSubtreeToLeft(tmp, hidden.getLeftChild(currentNode));
            }
            return currentNode;
        }

        public void draw() {
            hidden.draw();
        }

        public int size() {
            return hidden.size();
        }
    }

    public static class MyAVLLinkedTree<E extends Comparable> extends MyLinkedBinarySearchTree<E> {

        @SuppressWarnings("unchecked")
        private Position<E> addRecursive(Position<E> p, E value) {
//        Node<E> n = new Node<>(value, (MyLinkedBinaryTree.Node<E>) hidden.root());
//        hidden.heightOfSubtree(n);
//        hidden.heightOfSubtree();
            if (value.compareTo(p.getElement()) < 0) {
                if (hidden.getLeftChild(p) == null) {
                    hidden.addLeftChild(p, value);
                    return p;
                }
                Position<E> subtree = hidden.removeLeftChild(p);
                addRecursive(subtree, value);
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
//        hidden.addRoot(newRoot.getElement());
//        hidden.appendSubtreeToLeft(hidden.root(), hidden.getLeftChild(newRoot));
//        hidden.appendSubtreeToRight(hidden.root(), hidden.getRightChild(newRoot));
            hidden.setRoot(newRoot);
            return find(value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Position<E> delete(E value) {
            Position<E> parent = super.find(value);
            parent = hidden.parent(parent);
            Position<E> result = super.delete(value);
            if (parent != null) {
                if (hidden.getLeftChild(parent) == result)
                    balance(hidden.getLeftChild(parent));
                else
                    balance(hidden.getRightChild(parent));
            }
            Position<E> newRoot = balance(hidden.root());
            hidden.removeRoot();
            hidden.setRoot(newRoot);
            return result;
        }

        private int disbalanceFactor(Position<E> p) {
            if(hidden.validate(p) == null) throw new IllegalArgumentException("The subtree is not from the tree");
            return hidden.heightOfSubtree(hidden.getRightChild(p)) - hidden.heightOfSubtree(hidden.getLeftChild(p));
        }

        private Position<E> rotateRight(Position<E> p) {
            if(hidden.validate(p) == null) throw new IllegalArgumentException("The subtree is not from the tree");
            Position<E> q = hidden.getLeftChild(p) != null ? hidden.removeLeftChild(p) : null;
            Position<E> qright = hidden.getRightChild(q) != null ? hidden.removeRightChild(q) : null;
//        Position<E> pleft = hidden.removeLeftChild(p);
            hidden.appendSubtreeToLeft(p, qright);
            hidden.appendSubtreeToRight(q, p);
            return q;
        }

        private Position<E> rotateLeft(Position<E> q) {
            if(hidden.validate(q) == null) throw new IllegalArgumentException("The subtree is not from the tree");
            Position<E> p = hidden.getRightChild(q) != null ? hidden.removeRightChild(q) : null;
            Position<E> pleft = hidden.getLeftChild(p) != null ? hidden.removeLeftChild(p) : null;
//        Position<E> qright = hidden.removeRightChild(q);
            hidden.appendSubtreeToRight(q, pleft);
            hidden.appendSubtreeToLeft(p, q);
            return p;
        }

        private Position<E> balance(Position<E> p) {
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

        public E getRightChild(Position<E> position) {
            if (hidden.getRightChild(position) != null)
                return hidden.getRightChild(position).getElement();
            return null;
        }
//
//    @Override
//    public int heightOfSubtree(Position<E> p) {
//
//    }
    }

    public static void main(String[] args) {
        MyAVLLinkedTree<Integer> myAVLLinkedTree = new MyAVLLinkedTree<>();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            is = new FileInputStream("avl.in");
            isr = new InputStreamReader(is, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            fos = new FileOutputStream("avl.out");
            bw = new BufferedWriter(new OutputStreamWriter(fos));
        }
        catch (Exception e) {

        }

        String line;
        assert (br != null);
        assert (bw != null);
        try {
            line = br.readLine();
            for (String word: line.split(" ")) {
                myAVLLinkedTree.add(Integer.parseInt(word));
            }

            line = br.readLine();
            for (String word: line.split(" ")) {
                try {
                    myAVLLinkedTree.delete(Integer.parseInt(word));
                } catch (NumberFormatException e) {

                }
            }

            line = br.readLine();
            for (String word: line.split(" ")) {
                Position<Integer> p = myAVLLinkedTree.find(Integer.parseInt(word));
                Integer c = myAVLLinkedTree.getRightChild(p);
                if (c != null)
                    bw.write(c + " ");
                else
                    bw.write("null ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
