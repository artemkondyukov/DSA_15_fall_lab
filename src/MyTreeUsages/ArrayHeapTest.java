package MyTreeUsages;

import MyTrees.MyArrayHeap;

import javax.sound.midi.SysexMessage;
import java.util.Random;

/**
 * Created by artemka on 11/10/15.
 */
public class ArrayHeapTest {
    public static void run() {
        MyArrayHeap<Integer, String> myArrayHeap = new MyArrayHeap<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            myArrayHeap.add(random.nextInt() % 2500, "123");
        }
        for (int i = 0; i < 1000; i++) {
            System.out.println(myArrayHeap.removeMost().getElement().toString());
        }
    }
}
