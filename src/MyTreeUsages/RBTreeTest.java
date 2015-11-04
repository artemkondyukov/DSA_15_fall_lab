package MyTreeUsages;

import MyTrees.*;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by artemka on 11/2/15.
 */
public class RBTreeTest {
    public static void treeFill(MyRBLinkedTree t, int[] arr, int start, int end) {
        if (start > end) return;
        int mid = start + (end - start) / 2;
        t.add(arr[mid]);
        t.draw();
        treeFill(t, arr, mid + 1, end);
        treeFill(t, arr, start, mid - 1);
    }

    public static void run() {
        MyRBLinkedTree<Integer> tree = new MyRBLinkedTree<>();
        long startTime = System.nanoTime();
        for(int i = 0; i < 100000; i++) {
            tree.add(i);
            if (i % 1 == 0)
                tree.delete(i);
        }
        System.out.println(System.nanoTime() - startTime);
        System.out.println(tree.height());
//        MyRBLinkedTree<Integer> myRBLinkedTree = new MyRBLinkedTree<>();
//        InputStream is = null;
//        InputStreamReader isr = null;
//        BufferedReader br = null;
//        FileOutputStream fos = null;
//        BufferedWriter bw = null;
//        try {
//            is = new FileInputStream("rbt.in");
//            isr = new InputStreamReader(is, Charset.forName("UTF-8"));
//            br = new BufferedReader(isr);
//            fos = new FileOutputStream("rbt.out");
//            bw = new BufferedWriter(new OutputStreamWriter(fos));
//        }
//        catch (Exception e) {
//
//        }
//
//        String line;
//        assert (br != null);
//        assert (bw != null);
//        try {
//            line = br.readLine();
//            for (String word: line.split(" ")) {
//                myRBLinkedTree.add(Integer.parseInt(word));
//            }
//
//            line = br.readLine();
//            for (String word: line.split(" ")) {
//                Position<Integer> p = myRBLinkedTree.find(Integer.parseInt(word));
//                Integer c = myRBLinkedTree.getChildValue(p, AbstractBinaryTree.Direction.RIGHT);
//                if (c != null)
//                    bw.write(c + " ");
//                else
//                    bw.write("null ");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            bw.close();
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
