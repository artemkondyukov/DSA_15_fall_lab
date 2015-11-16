package MyTrees;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by artemka on 11/4/15.
 */
public class MyRBLinkedTreeTest {
    MyRBLinkedTree<Integer> integerTree;

    @Before
    public void setUp() {
        // Arrange
        integerTree = new MyRBLinkedTree<>();
    }

    @After
    public void tearDown() {
        // Arrange
        integerTree = new MyRBLinkedTree<>();
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

    @Test
    public void Integer_ascSortedFilling_correctRBisResult() throws NoSuchFieldException, IllegalAccessException {

        // Act
        int n = 1000000;
        for (int i = 1; i < n; i++) {
            integerTree.add(i);
        }
        Assert.assertTrue(integerTree.isRB());
        Assert.assertEquals(n - 1, integerTree.size());
        Assert.assertTrue(integerTree.height() <= 2 * Math.log(n) / Math.log(2));
        Assert.assertTrue(integerTree.height() >= Math.log(n) / Math.log(2));

        // Assert
        checkInOrderSorted();
    }
}