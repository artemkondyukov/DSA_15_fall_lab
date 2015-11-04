package MyTrees;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by artemka on 11/3/15.
 */
public class MyLinkedBinarySearchTreeTest {
    private MyLinkedBinarySearchTree<Integer> integerTree;

    @Before
    public void setUp() throws Exception {
        // Arrange
        integerTree = new MyLinkedBinarySearchTree<>();
    }

    private void checkInOrderSorted(MyLinkedBinaryTree<Integer> tree) {
        // Checking whether it does not violate BST properties
        // It means that array obtained from inorder traversal is ASC sorted
        Integer prev = null;
        for (Position<Integer> value: tree.inorder()) {
            if (prev == null) { prev = value.getElement(); continue; }

            Assert.assertTrue("array is not sorted", value.getElement() >= prev);
            prev = value.getElement();
            System.out.print(value.getElement() + " ");
        }
        System.out.println();
    }

    private MyLinkedBinaryTree obtainHiddenTree(MyLinkedBinarySearchTree tree)
            throws NoSuchFieldException, IllegalAccessException {
        // Getting hidden Linked Binary Tree
        Field hiddenTreeField = tree.getClass().getDeclaredField("hidden");
        hiddenTreeField.setAccessible(true);
        MyLinkedBinaryTree<Integer> hiddenTree = null;

        Assert.assertTrue("hidden integerTree is of incorrect type!",
                hiddenTreeField.get(tree) instanceof MyLinkedBinaryTree);
        return (MyLinkedBinaryTree)hiddenTreeField.get(tree);
    }

    @Test
    public void Integer_ascSortedFilling_correctBSTisResult() throws NoSuchFieldException, IllegalAccessException {

        // Act
        long time = 0;
        for (int i = 1; i < 1000; i++) {
            integerTree.add(i);

            long start = System.nanoTime();
            integerTree.find(i);
            time += System.nanoTime() - start;

            if (i % 100 == 0) {
                System.out.println(time);
                time = 0;
            }
        }
        Assert.assertEquals(999, integerTree.size());
        Assert.assertEquals(999, integerTree.hidden.height());

        // Assert
        MyLinkedBinaryTree<Integer> hiddenTree = obtainHiddenTree(integerTree);
        checkInOrderSorted(hiddenTree);
    }

    @Test
    public void Integer_descSortedFilling_correctBSTisResult() throws NoSuchFieldException, IllegalAccessException {
        // Act
        for (int i = 999; i > 0; i--) {
            integerTree.add(i);
        }
        Assert.assertEquals(integerTree.size(), 999);
        Assert.assertEquals(integerTree.hidden.height(), 999);

        // Assert
        MyLinkedBinaryTree<Integer> hiddenTree = obtainHiddenTree(integerTree);
        checkInOrderSorted(hiddenTree);
    }

    @Test
    public void Integer_randFilling_correctBSTisResult() throws NoSuchFieldException, IllegalAccessException {
        // Act
        Random random = new Random();
        for (int i = 1; i < 1000; i++) {
            int toAdd = random.nextInt() % 1000;
            while(integerTree.find(toAdd) != null) toAdd = random.nextInt() % 1000;
                integerTree.add(toAdd);
        }
        Assert.assertEquals(integerTree.size(), 999);
        Assert.assertTrue(integerTree.hidden.height() > 10);
        Assert.assertTrue(integerTree.hidden.height() < 999);

        // Assert
        MyLinkedBinaryTree<Integer> hiddenTree = obtainHiddenTree(integerTree);
        checkInOrderSorted(hiddenTree);
    }

    private void fillWithBalancedIntegerValues(List<Integer> tree) {
        Integer[] arr = { 100, 50, 150, 25, 75, 125, 175, 12, 37, 63, 87, 112, 137,
                      163, 187, 6, 18, 31, 43, 57, 69, 81, 93, 106, 118, 131, 143,
                      157, 169, 181, 193, 5, 4, 3, 2, 1, 0, -1, -2};
        tree.addAll(Arrays.asList(arr));
    }

    @Test
    public void Integer_fixedDeleting_correctBSTisResult() throws NoSuchFieldException, IllegalAccessException {
        List<Integer> toInsert = new ArrayList<>();
        fillWithBalancedIntegerValues(toInsert);

        for (int i = 0; i < toInsert.size(); i++) {
            toInsert.forEach(integerTree::add);
            int sizeBeforeDeletion = integerTree.size();
            Position<Integer> deleted = integerTree.delete(toInsert.get(i));

            Assert.assertEquals((int)toInsert.get(i), (int) deleted.getElement());
            Assert.assertNull(integerTree.find(toInsert.get(i)));
            Assert.assertEquals(sizeBeforeDeletion, integerTree.size() + 1);

            MyLinkedBinaryTree<Integer> hiddenTree = obtainHiddenTree(integerTree);
            checkInOrderSorted(hiddenTree);

            integerTree = new MyLinkedBinarySearchTree<>();
        }
    }

    @Test
    public void Integer_randDeleting_correctBSTisResult() throws NoSuchFieldException, IllegalAccessException {
        // Act
        int deletedNum = 0;
        List<Integer> inserted = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i < 1000; i++) {
            int toAdd = random.nextInt() % 1000;
            while(integerTree.find(toAdd) != null) toAdd = random.nextInt() % 1000;
            Position<Integer> added = integerTree.add(toAdd);
            Assert.assertEquals(toAdd, (int)added.getElement());
            Assert.assertEquals(toAdd, (int) integerTree.find(toAdd).getElement());
            inserted.add(toAdd);

            if (random.nextBoolean()) {
                int toDelete = inserted.get(random.nextInt(inserted.size()));
                while(integerTree.find(toDelete) == null) toDelete = inserted.get(random.nextInt(inserted.size()));
                int sizeBeforeDeletion = integerTree.size();

                Position<Integer> deleted = integerTree.delete(toDelete);
                Assert.assertEquals(sizeBeforeDeletion, integerTree.size() + 1);
                if (deleted != null) {
                    Assert.assertEquals(toDelete, (int)deleted.getElement());
                    Assert.assertNull(integerTree.find(toDelete));
                    deletedNum++;
                }
            }
        }

        Assert.assertEquals(integerTree.size(), 999 - deletedNum);
        Assert.assertTrue(integerTree.hidden.height() > Math.log(999 - deletedNum) / Math.log(2));
        Assert.assertTrue(integerTree.hidden.height() < 999 - deletedNum);

        // Assert
        MyLinkedBinaryTree<Integer> hiddenTree = obtainHiddenTree(integerTree);
        checkInOrderSorted(hiddenTree);
    }
}