package MyTreeUsages;

import MyTrees.MyArrayTree;
import MyTrees.Position;
import MyTrees.Tree;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Stack;

/**
 * Created by artemka on 10/12/15.
 */
public class ArithmeticExpressionParser {
    private static final boolean DOUBLE_VALUE = true;
    private static final boolean OPERATOR_VALUE = false;
    private static final char SENTINEL = '\0';

    private static class PrecedingComparator implements Comparator<Character> {
        @Override
        public int compare(Character o1, Character o2) {
            if (! (isValidOperator(o1) && isValidOperator(o2)) )
                throw new IllegalArgumentException("You try to compare not the operators");
            if (o1 == '*' || o1 == '/') {
                if (o2 == '*' || o2 == '/') return 0;
                if (o2 == '+' || o2 == '-' || o2 == SENTINEL) return 1;
            }
            else if (o1 == '+' || o1 == '-'){
                if (o2 == '*' || o2 == '/') return -1;
                if (o2 == '+' || o2 == '-') return 0;
                if (o2 == SENTINEL) return 1;
            }
            else {
                if (o2 == SENTINEL) return 0;
                else return -1;
            }
            throw new IllegalStateException("Comparator does not work well");
        }

    }

    private static class Element {
        private boolean type;       // true is a number, false is an operator
        private double dValue;      // if it is a number
        private char cValue;        // if it is an operator

        public Element(double v) {
            cValue = '\0';              // some kind of DEFUNCT
            dValue = v;
            type = DOUBLE_VALUE;
        }

        public Element(char c) {
            if (!isValidOperator(c))
                throw new IllegalArgumentException("You try to create an element for unsupported operation");
            cValue = c;
            dValue = Double.MIN_VALUE;  // some kind of DEFUNCT
            type = OPERATOR_VALUE;
        }

        public boolean getType() {return this.type;}
        public char getCValue() {return this.cValue;}   // you'll get DEFUNCT if try to get cValue from a number
        public double getdValue() {return this.dValue;} // you'll get DEFUNCT if try to get dValue from an operator
    }

    private static boolean isValidOperator(char c) {
        if (c == '+' || c == '-' || c == '*' || c == '/' || c == SENTINEL) return true;
        return false;
    }

    private static boolean recursiveE(BufferedReader br, Stack<Character> operators,
                            Stack<MyArrayTree<Element>> operands) throws IOException{
        PrecedingComparator comp = new PrecedingComparator();
        recursiveP(br, operators, operands);
        String c = br.readLine();
        while (c.matches("([+/*]|-)") && c.length() == 1) {
            while(comp.compare(operators.peek(), c.charAt(0)) > 0) {
                popOperator(operators, operands);
            }
            operators.push(c.charAt(0));
            recursiveP(br, operators, operands);
            c = br.readLine();
        }
        while (operators.peek() != SENTINEL) {
            popOperator(operators, operands);
        }
        operators.pop();
        if (c.matches("[)]")) {
            return true;
        }
        return false;
    }

    private static void popOperator(Stack<Character> operators, Stack<MyArrayTree<Element>> operands) {
        MyArrayTree<Element> t1 = operands.pop();
        MyArrayTree<Element> t2 = operands.pop();
        Element newRoot = new Element(operators.pop());
        MyArrayTree<Element> tmpTree = new MyArrayTree<>(2, t1.size() + t2.size() + 1);
        tmpTree.addRoot(newRoot);
        tmpTree.addSubtree(tmpTree.root(), t1);
        tmpTree.addSubtree(tmpTree.root(), t2);
        operands.push(tmpTree);
    }

    private static void recursiveP(BufferedReader br, Stack<Character> operators,
                            Stack<MyArrayTree<Element>> operands) throws IOException{
        String str = br.readLine();
        try {
            double op = Double.parseDouble(str);
            MyArrayTree<Element> newTree = new MyArrayTree<>(2, 1);
            Element newRoot = new Element(op);
            newTree.addRoot(newRoot);
            operands.push(newTree);
        }
        catch (NumberFormatException e) {
            if (str.matches("[(]") && str.length() == 1) {
                operators.push(SENTINEL);
                if(!recursiveE(br, operators, operands))  {
                    System.out.println("Arithmetic expression is very bad. Result is unpredictable");
                }
            }
        }
    }

    private static MyArrayTree<Element> eparse(ByteArrayInputStream bis) {
        Stack<Character> operators = new Stack<>();
        Stack<MyArrayTree<Element>> operands = new Stack<>();                     // position of string
        operators.push(SENTINEL);
        InputStreamReader isr = new InputStreamReader(bis);
        BufferedReader br = new BufferedReader(isr);
        try {
            recursiveE(br, operators, operands);
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
        return operands.pop();
    }

    public static void run() {
        System.out.println("parser started");
        Scanner s = new Scanner(System.in);
        String expr = s.nextLine();
        expr = expr.replaceAll("[+]", " + ").
                replaceAll("[-]", " - ").
                replaceAll("[*]", " * ").
                replaceAll("[/]", " / ").
                replaceAll("[(]", " ( ").
                replaceAll("[)]", " ) ").
                replaceAll("([ ]+)", " ").
                replaceAll(" ", "\n");
        expr += "\n\n";
        ByteArrayInputStream bis = new ByteInputStream(expr.getBytes(), expr.getBytes().length);
        Tree<Element> parserTree = eparse(bis);
        Double v1 = null;
        Double v2 = null;
        Stack<Element> stack = new Stack<>();
        for (Position<Element> el: parserTree.postorder()) {
            if (!el.getElement().type) {
                v1 = stack.pop().getdValue();
                v2 = stack.pop().getdValue();
                switch (el.getElement().cValue) {
                    case '+':
                        v1 += v2;
                        break;
                    case '-':
                        v1 -= v2;
                        break;
                    case '*':
                        v1 *= v2;
                        break;
                    case '/':
                        v1 /= v2;
                        break;
                }
                stack.push(new Element(v1));
            }
            else {
                stack.push(el.getElement());
            }
        }
        System.out.println(v1);
    }
}
