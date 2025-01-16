package pds;

public class Test {
    public static void main(String[] args) {
        PHashMap<String, Integer> map = new PHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        System.out.println(map.toList());
        System.out.println(map.toString());
    }
}
