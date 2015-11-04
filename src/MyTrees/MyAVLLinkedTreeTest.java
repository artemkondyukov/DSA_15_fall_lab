package MyTrees;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.misc.ASCIICaseInsensitiveComparator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by artemka on 11/3/15.
 */
public class MyAVLLinkedTreeTest {
    MyAVLLinkedTree<Integer> integerTree;

    @Before
    public void setUp() {
        // Arrange
        integerTree = new MyAVLLinkedTree<>();
    }

    @After
    public void tearDown() {
        // Arrange
        integerTree = new MyAVLLinkedTree<>();
    }

    private void checkInOrderSorted() {
        // Checking whether it does not violate AVL properties
        // It means that array obtained from inorder traversal is ASC sorted
        int elementCount = 0;
        Integer prev = null;
        for (Position<Integer> value: integerTree.inorder()) {
            if (prev == null) { prev = value.getElement(); elementCount++; continue; }

            Assert.assertTrue("array is not sorted", value.getElement() >= prev);
            prev = value.getElement();
            elementCount++;
        }

        Assert.assertEquals(integerTree.size(), elementCount);
    }

    /*
     * Checks whether heights of two children differ by 1 or zero
     */
    private boolean isAVL() {
        for(Position<Integer> node: integerTree.inorder()) {
            if (Math.abs(integerTree.disbalanceFactor(node)) > 1) { System.out.println(node.getElement()); return false; }
        }
        return true;
    }

    @Test
    public void Integer_ascSortedFilling_correctAVLisResult() throws NoSuchFieldException, IllegalAccessException {

        // Act
        long time = 0;
        for (int i = 1; i < 100000; i++) {
            integerTree.add(i);

//            long start = System.nanoTime();
//            integerTree.find(i);
//            time += System.nanoTime() - start;
//
//            if (i % 1000 == 0) {
//                System.out.println(time);
//                time = 0;
//            }
        }
        Assert.assertEquals(99999, integerTree.size());
        Assert.assertEquals(17, integerTree.hidden.height());

        for(Position<Integer> p: integerTree.inorder())
            Assert.assertTrue(integerTree.disbalanceFactor(p) < 2);

        // Assert
        checkInOrderSorted();
    }

    @Test
    public void Integer_descSortedFilling_correctAVLisResult() {
        // Act
        for (int i = 999; i > 0; i--) {
            int sizeBefore = integerTree.size();
            Position<Integer> added = integerTree.add(i);
            Assert.assertEquals(sizeBefore + 1, integerTree.size());
            Assert.assertNotNull(added);
            Assert.assertEquals(i, (int)added.getElement());
        }
        Assert.assertEquals(999, integerTree.size());
        Assert.assertEquals(10, integerTree.hidden.height());

        for(Position<Integer> p: integerTree.inorder())
            Assert.assertTrue(integerTree.disbalanceFactor(p) < 2);

        // Assert
        checkInOrderSorted();
    }

    @Test
    public void Integer_fixedFilling_correctAVLisResult() {
        int[] arr = {-900, 474, -902, 697, 190, 200, 357, 456, -561};
        for (int anArr : arr) {
            System.out.println(anArr);
            integerTree.add(anArr);
            Assert.assertTrue(isAVL());
        }
    }

    @Test
    public void Integer_randFilling_correctAVLisResult() {
        // Act
        int n = 100000;
        Random random = new Random();
        List<Integer> added = new ArrayList<>();
        long time = 0;
        for (int i = 1; i < n; i++) {
            int toAdd = random.nextInt() % n;
            while(integerTree.find(toAdd) != null) toAdd = random.nextInt() % n;

//            long start = System.nanoTime();
//            integerTree.find(i);
            integerTree.add(toAdd);
//            time += System.nanoTime() - start;
//
//            if (i % 10000 == 0) {
//                System.out.println(time);
//                time = 0;
//            }

            added.add(toAdd);
//            Assert.assertTrue(isAVL());
        }

        Assert.assertEquals(n - 1, integerTree.size());
        Assert.assertTrue(integerTree.hidden.height() > Math.log(n + 1)/Math.log(2));
        Assert.assertTrue(integerTree.hidden.height() < 1.44 * Math.log(n + 2)/Math.log(2));
        System.out.println(integerTree.height());

        // Assert
        checkInOrderSorted();
    }


    @Test
    public void Integer_ascSortedDeleting_correctAVLisResult() throws NoSuchFieldException, IllegalAccessException {

        // Act
        int n = 16000;
//        long time = 0;
        List<Integer> deletedList = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i < n; i++) {

//            long start = System.nanoTime();
            integerTree.add(i);
            Position<Integer> found = integerTree.find(i);
            Assert.assertEquals(i, (int) found.getElement());
//            time += System.nanoTime() - start;
//
//            if (i % n % 10000 == 0) {
//                System.out.println(time);
//                time = 0;
//            }

            if (i % 5 == 0) {
                int toDelete = random.nextInt(i);
                while(integerTree.find(toDelete) == null) toDelete = random.nextInt(i);
                int sizeBeforeDeletion = integerTree.size();

                Position<Integer> deleted = integerTree.delete(toDelete);
                // Assert size is decreased by 1
                Assert.assertEquals(sizeBeforeDeletion, integerTree.size() + 1);

                if (deleted != null) {
                    deletedList.add(deleted.getElement());
                    Assert.assertEquals(toDelete, (int)deleted.getElement());
                    Assert.assertNull(integerTree.find(toDelete));
                }
            }

            // Assert the tree is really AVL after every iteration
            Assert.assertTrue(isAVL());
        }

        // Assert correct size and height of tree
        n -= deletedList.size();
        Assert.assertEquals(n - 1, integerTree.size());
        Assert.assertTrue(integerTree.hidden.height() > Math.log(n + 1)/Math.log(2));
        Assert.assertTrue(integerTree.hidden.height() < 1.44 * Math.log(n + 2)/Math.log(2));
        System.out.println(integerTree.height());

        // Assert
        checkInOrderSorted();
    }

    @Test
    public void Integer_randDeleting_correctAVLisResult() throws NoSuchFieldException, IllegalAccessException {

        // Act
        int n = 16000;
//        long time = 0;
        List<Integer> addedList = new ArrayList<>();
        List<Integer> deletedList = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i < n; i++) {
            int toAdd = random.nextInt() % n;
            while(integerTree.find(toAdd) != null) toAdd = random.nextInt() % n;
            addedList.add(toAdd);
            integerTree.add(toAdd);
            Position<Integer> found = integerTree.find(toAdd);
            // Assert find correct position
            Assert.assertEquals(toAdd, (int) found.getElement());
//            time += System.nanoTime() - start;
//
//            if (i % n % 10000 == 0) {
//                System.out.println(time);
//                time = 0;
//            }

            if (i % 5 == 0 && i > 100) {
                int toDelete = random.nextInt(addedList.size());
                toDelete = addedList.get(toDelete);
                while(integerTree.find(toDelete) == null) toDelete = addedList.get(random.nextInt(addedList.size()));
                int sizeBeforeDeletion = integerTree.size();

                Position<Integer> deleted = integerTree.delete(toDelete);

                // Assert size is decreased by 1
                Assert.assertEquals(sizeBeforeDeletion, integerTree.size() + 1);
                Assert.assertNull(integerTree.find(toDelete));

                Assert.assertNotNull(deleted);
                deletedList.add(deleted.getElement());
                // Assert delete returns correct position
                Assert.assertEquals(toDelete, (int) deleted.getElement());
            }

            // Assert the tree is really AVL after every iteration
            Assert.assertTrue(isAVL());
        }

        // Assert correct size and height of tree
        n -= deletedList.size();
        Assert.assertEquals(n - 1, integerTree.size());
        Assert.assertTrue(integerTree.hidden.height() > Math.log(n + 1)/Math.log(2));
        Assert.assertTrue(integerTree.hidden.height() < 1.44 * Math.log(n + 2)/Math.log(2));
        System.out.println(integerTree.height());

        // Assert
        checkInOrderSorted();
    }
}