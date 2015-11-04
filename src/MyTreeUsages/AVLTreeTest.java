package MyTreeUsages;

import MyTrees.MyAVLLinkedTree;

/**
 * Created by artemka on 10/20/15.
 */
public class AVLTreeTest {
    public static void run() {
        MyAVLLinkedTree<Integer> tree = new MyAVLLinkedTree<>();
        long startTime = System.nanoTime();
        for(int i = 0; i < 10000; i++) {
            tree.add(i);
            if (i % 3 == 0)
                tree.delete(i);
        }
        System.out.println(System.nanoTime() - startTime);
        System.out.println(tree.height());
//        child = tree.getRightChild(tree.find(3));
//        tree.
//        tree.add("March");
//        tree.add("May");
//        tree.add("November");
//        tree.add("August");
//        tree.add("April");
//        tree.add("January");
//        tree.add("December");
//        tree.add("July");
//        tree.add("February");
//        tree.add("June");
//        tree.add("October");
//        tree.add("September");
//        tree.draw();
//
//        tree.delete("August");
////        tree.delete("December");
////        tree.delete("April");
//        tree.draw();
    }
}
