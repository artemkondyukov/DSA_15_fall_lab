package MyTrees;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artemka on 10/13/15.
 */
public class MyLinkedBinaryTree<E> extends AbstractBinaryTree<E> {
    protected static class Node<E> implements MyTrees.Position<E> {
        private Node<E> left;
        private Node<E> right;
        private Node<E> parent;
        private E value;
        private int height;
        private int size;                               // size of subtree where this node is root

        public Node(E value, Node<E> parent) {          // If Node is a root we set parent = null
            this.value = value;                         // And if parent is equals to this, it means DEFUNCT
            this.parent = parent;
            this.height = 0;
            this.size = 1;
        }

        public int getHeight() {
            return height;
        }

        public void increaseHeight(int val) {
            this.height += val;
            Node<E> tmp = parent;
            while (tmp != null && tmp != tmp.parent) {
                int leftHeight = tmp.left != null ? tmp.left.height : -1;
                int rightHeight = tmp.right != null ? tmp.right.height : -1;
                tmp.height = leftHeight > rightHeight ? leftHeight + 1 : rightHeight + 1;

                tmp = tmp.parent;
            }
        }

        public void increaseSize(int val) {
            this.size += val;
            Node<E> tmp = parent;
            while (tmp != null && tmp != tmp.parent) {
                int leftSize = tmp.left != null ? tmp.left.size : 0;
                int rightSize = tmp.right != null ? tmp.right.size : 0;
                tmp.size = leftSize + rightSize + 1;
                tmp = tmp.parent;
            }
        }

        @Override
        public E getElement() { return this.value; }
    }

    private Node<E> root;

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
        if (getRightChild(p) == null)
            currentNode.increaseHeight(1);
        Node<E> child = new Node<>(value, currentNode);
        currentNode.increaseSize(1);
        return currentNode.left = child;
    }

    @Override
    public Position<E> addRightChild(Position<E> p, E value) {
        if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
        if (getRightChild(p) != null) throw new IllegalArgumentException("The node already has a right child");
        Node<E> currentNode = (Node<E>)p;
        Node<E> child = new Node<>(value, currentNode);
        if (getLeftChild(p) == null)
            currentNode.increaseHeight(1);
        currentNode.increaseSize(1);
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
        int heightDiff = heightOfSubtree(currentNode.left) - heightOfSubtree(currentNode.right);
        currentNode.increaseHeight(heightDiff > 0 ? -heightDiff : 0);
//        if (currentNode.right != null)
//            currentNode.increaseHeight(currentNode.right.height < currentNode.left.height
//                    ? currentNode.right.height - currentNode.left.height
//                    : 0);
//        else
//            currentNode.increaseHeight(-currentNode.height);

        Node<E> result = currentNode.left;
        if (result == null) return null;

//        size -= sizeOfSubtree(currentNode.left);
        currentNode.increaseSize(-currentNode.left.size);
        currentNode.left.parent = currentNode.left;         // Defunct
        currentNode.left = null;
        return result;
    }

    @Override
    public Position<E> removeRightChild(Position<E> p) {
        if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
        Node<E> currentNode = (Node<E>)p;
        int heightDiff = heightOfSubtree(currentNode.right) - heightOfSubtree(currentNode.left);
        currentNode.increaseHeight(heightDiff > 0 ? -heightDiff : 0);
//        if (currentNode.left != null)
//            currentNode.increaseHeight(currentNode.left.height < currentNode.right.height
//                    ? currentNode.left.height - currentNode.right.height
//                    : 0);
//        else
//            currentNode.increaseHeight(-currentNode.height);

        Node<E> result = currentNode.right;
        if (result == null) return null;

//        size -= sizeOfSubtree(currentNode.right);
        currentNode.increaseSize(-currentNode.right.size);
        currentNode.right.parent = currentNode.right;         // Defunct
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
        if (p == null) return 0;
        if (validate(p) == null) throw new IllegalArgumentException("Position is not from tree");
        Node<E> currentNode = (Node<E>)p;
//        int result = 0;
//        if (currentNode.left != null)
//            result += sizeOfSubtree(currentNode.left);
//        if (currentNode.right != null)
//            result += sizeOfSubtree(currentNode.right);
//        return result + 1;
        return currentNode.size;
    }

    @Override
    public Position<E> remove(Position<E> p) throws IllegalArgumentException {
        if (validate(p) == null) throw  new IllegalArgumentException("The position is not from the tree");
        Node<E> currentNode = (Node<E>)p;
        if (currentNode.left != null)
            currentNode.left.parent = currentNode.left;         // Defunct
        if (currentNode.right != null)
            currentNode.right.parent = currentNode.right;         // Defunct

        if (currentNode.parent != null) currentNode.parent.increaseHeight(-currentNode.size);

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
//        size += sizeOfSubtree(subtreeNode);

        parentNode.increaseSize(subtreeNode.size);
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
//        size += sizeOfSubtree(subtreeNode);

        parentNode.increaseSize(subtreeNode.size);
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
        return newRoot;
    }

    @Override
    public Position<E> addRoot(E value) throws IllegalStateException {
        if (root != null) throw new IllegalStateException("You try to add root to the tree with root");
        Node<E> newRoot = new Node<>(value, null);      // null is the parent of root
        root = newRoot;
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
    public Iterable<Position<E> > children(Position<E> p) throws IllegalArgumentException {
        if (validate(p) == null) throw new IllegalArgumentException("MyTrees.Position is not from the tree");
        List<Position <E> > result = new ArrayList<>();
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
        if (validate(p) == null) throw new IllegalArgumentException("MyTrees.Position is not from the tree");
        int result = 0;
        if (getLeftChild(p) != null) {
            result++;
        }
        if (getRightChild(p) != null) {
            result++;
        }
        return result;
    }

    public Position<E> predecessor(Position<E> position) {
        if (validate(position) == null)
            throw new IllegalArgumentException("You try to find predecessor of position which is not in the tree");
        Position<E> tmp = getLeftChild(position);
        if (tmp == null) return null;
        while (getRightChild(tmp) != null)
            tmp = getRightChild(tmp);
        return tmp;
    }

    public Position<E> successor(Position<E> position) {
        if (validate(position) == null)
            throw new IllegalArgumentException("You try to find predecessor of position which is not in the tree");
        Position<E> tmp = getRightChild(position);
        if (tmp == null) return null;
        while (getLeftChild(tmp) != null)
            tmp = getLeftChild(tmp);
        return tmp;
    }

    @Override
    public int size() {
        if (root == null) return 0;
        return root.size;
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
        int height = height() - 1;
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

    @Override
    public int heightOfSubtree(Position<E> p) {
        if (p == null) return -1;                    // For AVL, height of empty tree is -1
        if (validate(p) == null) throw new IllegalArgumentException("MyTrees.Position is not from the tree");
        Node<E> currentNode = (Node<E>)p;

        return currentNode.height;
    }
}
