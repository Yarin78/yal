package yarin.yal;

public class PrintFormat {
    public static void main(String[] args) {
        System.out.println(String.format("[%.2f]", 1.2345)); // [1.23]
        System.out.println(String.format("[%f]", 1.2345));  // [1.234500]
        System.out.println(String.format("[%6.2f]", 1.2345));  // [  1.23]
        System.out.println(String.format("[%04d]", 3));  // [0003]
        System.out.println(String.format("[%s]", Integer.toHexString(27 | 0x10000).substring(1)));  // [001b]
        System.out.println(String.format("[%3H}]", 255));  // [ FF]
    }
}
