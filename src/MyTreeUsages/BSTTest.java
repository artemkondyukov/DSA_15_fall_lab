package MyTreeUsages;

import MyTrees.MyLinkedBinarySearchTree;

import java.util.Random;

/**
 * Created by artemka on 10/13/15.
 */
public class BSTTest {
    public static void treeFill(MyLinkedBinarySearchTree t, int[] arr, int start, int end) {
        if (start > end) return;
        int mid = start + (end - start) / 2;
        t.add(arr[mid]);
        treeFill(t, arr, mid + 1, end);
        treeFill(t, arr, start, mid - 1);
    }

    public static void run() {
        MyLinkedBinarySearchTree<String> myTree = new MyLinkedBinarySearchTree<>();
//        int[] arr = {1, 3, 4, 5, 6, 8, 10, 11, 12, 13, 17, 21};
//        treeFill(myTree, arr, 0, arr.length - 1);
//        myTree.draw();
//        System.out.println(myTree.size());
//        System.out.println(myTree.find(3));
//        System.out.println(myTree.find(12).getElement());
//        System.out.println(myTree.find(1).getElement());
////        System.out.println(myTree.find(-1).getElement());
//        System.out.println(myTree.find(21).getElement());
//        System.out.println(myTree.size());
////        myTree.draw();
//        System.out.println(myTree.delete(12));
//        System.out.println(myTree.delete(13));
//        System.out.println(myTree.delete(11));
//        myTree.draw();
//        System.out.println(myTree.size());
        myTree.add("March");
        myTree.add("May");
        myTree.add("November");
        myTree.add("August");
        myTree.add("April");
        myTree.add("January");
        myTree.add("December");
        myTree.add("July");
        myTree.add("February");
        myTree.add("June");
        myTree.add("October");
        myTree.add("September");

        myTree.draw();

        myTree.delete("August");
//        myTree.delete("December");
//        myTree.delete("April");
        myTree.draw();
    }
}
