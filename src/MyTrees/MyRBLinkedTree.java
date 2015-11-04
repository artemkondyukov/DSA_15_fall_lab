package MyTrees;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by artemka on 10/27/15.
 */
public class MyRBLinkedTree<E extends Comparable <? super E >> extends MyLinkedBinarySearchTree<E> {

    private enum Color {
        RED, BLACK
    }
    private Map<Position<E>, Color> colorMap;

    public MyRBLinkedTree() {
        colorMap = new HashMap<>();
    }

    private Position<E> grandparent(Position <E> p) {
        if (hidden.validate(p) == null)
            throw new IllegalArgumentException("The node is not from the tree");
        if (hidden.parent(p) == null)
            return null;
        return hidden.parent(hidden.parent(p));
    }

    private Position<E> sibling(Position<E> p) {
        if (hidden.validate(p) == null)
            throw new IllegalArgumentException("The node is not from the tree");
        if (hidden.root() == p) return null;
        Position<E> parent = hidden.parent(p);
        return hidden.getChild(parent, p == hidden.getLeftChild(parent)
                ? AbstractBinaryTree.Direction.RIGHT
                : AbstractBinaryTree.Direction.LEFT);
    }

    public Position<E> add(E value) {
        Position <E> newPosition = super.add(value);
        if (newPosition == null) return null;
        if (newPosition != hidden.root())
            colorMap.put(newPosition, Color.RED);
        else
            colorMap.put(newPosition, Color.BLACK);

        if (colorMap.get(hidden.parent(newPosition)) == Color.RED) {
            Position<E> parent = hidden.parent(newPosition);
            if (sibling(parent) == null || colorMap.get(sibling(parent)) == Color.BLACK)
                restructure(newPosition);
            else
                recolor(parent);
        }

        return newPosition;
    }

    private Position<E> restructure(Position<E> x) {
        Position<E> z = grandparent(x);
        Position<E> ggp = hidden.parent(z);
        assert (z != null);                       // It's impossible, if all is implemented correctly
        if (hidden.root() != z)
            z = hidden.getLeftChild(ggp) == z ? hidden.removeLeftChild(ggp) : hidden.removeRightChild(ggp);

        Position<E> y = hidden.parent(x);

        // Temporarily change color of the three nodes to red, and further we change color of b
        colorMap.put(x, Color.RED);
        colorMap.put(y, Color.RED);
        colorMap.put(z, Color.RED);

        AbstractBinaryTree.Direction direction_1 =
                (y == hidden.getLeftChild(z)
                        ? AbstractBinaryTree.Direction.LEFT
                        : AbstractBinaryTree.Direction.RIGHT);

        AbstractBinaryTree.Direction direction_2 =
                (x == hidden.getLeftChild(y)
                        ? AbstractBinaryTree.Direction.LEFT
                        : AbstractBinaryTree.Direction.RIGHT);

        y = hidden.removeChild(z, direction_1);
        x = hidden.removeChild(y, direction_2);

        if (direction_1 == direction_2) {
            if (hidden.numChildren(y) != 0) {
                Position<E> r = hidden.removeChild(y,
                        (direction_1 == AbstractBinaryTree.Direction.LEFT
                                ? AbstractBinaryTree.Direction.RIGHT
                                : AbstractBinaryTree.Direction.LEFT));
                if (direction_1 == AbstractBinaryTree.Direction.LEFT)
                    hidden.appendSubtreeToLeft(z, r);
                else
                    hidden.appendSubtreeToRight(z, r);
            }
        }

        Position<E> b = direction_1 == direction_2 ? y : x;
        hidden.appendSubtree(b, (direction_1 == AbstractBinaryTree.Direction.LEFT
                                    && direction_2 == AbstractBinaryTree.Direction.LEFT
                                ? x
                                : (direction_1 == AbstractBinaryTree.Direction.LEFT ? y : z)),
                AbstractBinaryTree.Direction.LEFT);

        hidden.appendSubtree(b, (direction_1 == AbstractBinaryTree.Direction.RIGHT
                        && direction_2 == AbstractBinaryTree.Direction.RIGHT
                        ? x
                        : (direction_1 == AbstractBinaryTree.Direction.RIGHT ? y : z)),
                AbstractBinaryTree.Direction.RIGHT);

        if (ggp == null) {
            hidden.removeRoot();
            hidden.setRoot(b);
        }
        else {
            if (b.getElement().compareTo(ggp.getElement()) < 0) {
                hidden.appendSubtreeToLeft(ggp, b);
            }
            else {
                hidden.appendSubtreeToRight(ggp, b);
            }
        }

        colorMap.put(b, Color.BLACK);
        return b;
    }

    private void recolor(Position<E> p) {
        assert (sibling(p) != null);         // We assume sibling is red, so it could not be null

        colorMap.put(p, Color.BLACK);
        colorMap.put(sibling(p), Color.BLACK);
        if (hidden.root() != hidden.parent(p))
            colorMap.put(hidden.parent(p), Color.RED);

        if (grandparent(p) != null &&
                colorMap.get(grandparent(p)) == Color.RED) {
            if (colorMap.get(sibling(grandparent(p))) == Color.RED) {
                recolor(grandparent(p));
            }
            else {
                restructure(hidden.parent(p));
            }
        }
    }

    private void drawRecursive(String[][] drawPos, Position<E> s, int level, int pos) {
        if(drawPos[level] == null) drawPos[level] = new String[(int)Math.pow(2, level)];
        drawPos[level][pos] = s.getElement().toString() + " " + colorMap.get(s).toString();
        if (hidden.getLeftChild(s) != null)
            drawRecursive(drawPos, hidden.getLeftChild(s), level + 1, pos * 2);
        if (hidden.getRightChild(s) != null)
            drawRecursive(drawPos, hidden.getRightChild(s), level + 1, pos * 2 + 1);
    }

    public void draw() {
        String[][] drawPositions = new String[hidden.height() + 1][];
        drawRecursive(drawPositions, hidden.root(), 0, 0);
        int height = hidden.height();
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
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public int height () {
        return hidden.height();
    }

    /*
     * It's just for task
     */
    public E getChildValue(Position<E> p, AbstractBinaryTree.Direction direction) {
        switch (direction) {
            case LEFT: return (hidden.getLeftChild(p) != null) ? hidden.getLeftChild(p).getElement() : null;
            case RIGHT: return (hidden.getRightChild(p) != null) ? hidden.getRightChild(p).getElement() : null;
        }
        return null;
    }

    public Position<E> delete(E value) {
        return null;
    }
}
