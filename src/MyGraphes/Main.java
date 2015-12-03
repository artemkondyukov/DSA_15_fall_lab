package MyGraphes;

import MyTrees.MyArrayHeap;
import com.sun.org.apache.xerces.internal.xs.StringList;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by artemka on 11/24/15.
 */
public class Main {
    public static void main(String[] args) {
        MyGraph<String, Integer> myGraph = new MyGraph<>();
        MyArrayHeap<String, Integer> myArrayHeap = new MyArrayHeap<>(MyArrayHeap.HEAP_TYPE.MIN_HEAP);

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            is = new FileInputStream("cities.txt");
            isr = new InputStreamReader(is, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            fos = new FileOutputStream("around.txt");
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
                myGraph.insertVertex(word);
            }

            line = br.readLine();
            String nextWord = null;

            Edge<String, Integer> interestingEdge = null;

            for (String word: line.split(" ")) {
                if (nextWord == null) nextWord = word;
                else {
//                    if (    (nextWord.contains("-DG") && word.contains("-R")) ||
//                            (nextWord.contains("-R") && word.contains("-DG")) ||
//                            (nextWord.contains("-DU") && word.contains("-R")) ||
//                            (nextWord.contains("-R") && word.contains("-DU"))) {
//                        // We shouldn't go through this ways
//                        nextWord = null;
//                        continue;
//                    }
                    if (nextWord.equals("Vladikavkaz-R") && word.equals("Tbilisi-G"))
                        interestingEdge = myGraph.insertEdge
                                (1, myGraph.getVertexByValue(nextWord), myGraph.getVertexByValue(word));
                    else
                        myGraph.insertEdge(1, myGraph.getVertexByValue(nextWord), myGraph.getVertexByValue(word));
                    nextWord = null;
                }
            }

//            String[] cities = {"Donetsk-DU", "Kiev-U", "Lviv-U", "Batumi-G", "Rostov-R"};
//            for (String city: cities) {
//                if (myGraph.getVertexByValue(city) == null) continue;
//                Iterator<? extends Vertex<String>> iterator = myGraph.getVertexByValue(city).getAdjVertices();
//                while(iterator.hasNext()) {
//                    myArrayHeap.add(iterator.next().getValue(), 0);
//                }
//                while(myArrayHeap.size() != 0)
//                    bw.write(myArrayHeap.removeMost().getElement().getKey() + " ");
//
//                bw.write("\n");
//            }

            List<Vertex<String>> result =
                    myGraph.shortestPath(myGraph.getVertexByValue("Melitopol-U"),
                                         myGraph.getVertexByValue("Rostov-R"));
            bw.write(result.size() - 1 + " ");
            for(Vertex<String> v: result) bw.write(v.getValue() + " ");

            bw.write("\n");
            result =
                    myGraph.shortestPath(myGraph.getVertexByValue("Sukhumi-DG"),
                            myGraph.getVertexByValue("Lugansk-DU"));
            bw.write(result.size() - 1 + " ");
            for(Vertex<String> v: result) bw.write(v.getValue() + " ");

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
