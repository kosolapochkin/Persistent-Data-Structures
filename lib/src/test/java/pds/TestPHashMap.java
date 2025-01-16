package pds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

class TestPHashMap {

    PHashMap<String, Integer> map;

    @BeforeEach
    void init() {
        map = new PHashMap<>();
    }

    @Test
    void testPutGet() {
        map.put("A", 1);
        assertEquals(1, map.get("A"));
        assertEquals(null, map.get("B"));

        map.put("B", 2);
        assertEquals(1, map.get("A"));
        assertEquals(2, map.get("B"));

        map.put("A", 3);
        assertEquals(3, map.get("A"));
        assertEquals(2, map.get("B"));
    }

    @Test
    void testPutSize() {
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        assertEquals(3, map.size());
    }

    @Test
    void testPutRemove() {
        map.put("A", 1);
        map.put("B", 2);
        assertEquals(1, map.get("A"));
        assertEquals(2, map.get("B"));

        map.remove("A");
        assertEquals(null, map.get("A"));
        assertEquals(2, map.get("B"));

        map.remove("A");
        assertEquals(null, map.get("A"));
        assertEquals(2, map.get("B"));
    }

    @Test
    void testPutClear() {
        map.put("A", 1);
        map.put("B", 2);
        assertEquals(1, map.get("A"));
        assertEquals(2, map.get("B"));

        map.clear();
        assertEquals(null, map.get("A"));
        assertEquals(null, map.get("B"));
    }

    @Test
    void testKeySet() {
        map.put("A", 1);
        map.put("B", 2);
        assertEquals(new HashSet<>(Arrays.asList("A", "B")), map.keySet());
    }

    @Test
    void testValues() {
        map.put("A", 1);
        map.put("B", 2);
        assertEquals(new LinkedList<>(Arrays.asList(1, 2)), map.values());
    }

    @Test
    void testUndoRedo() {
        map.put("A", 1);
        map.put("B", 2);
        assertTrue(map.toString().contains("A:1"));
        assertTrue(map.toString().contains("B:2"));

        map.undo();
        assertTrue(map.toString().contains("A:1"));
        assertFalse(map.toString().contains("B:2"));

        map.redo();
        assertTrue(map.toString().contains("A:1"));
        assertTrue(map.toString().contains("B:2"));

        map.undo();
        map.put("C", 3);
        assertTrue(map.toString().contains("A:1"));
        assertFalse(map.toString().contains("B:2"));
        assertTrue(map.toString().contains("C:3"));

        map.redo();
        assertTrue(map.toString().contains("A:1"));
        assertFalse(map.toString().contains("B:2"));
        assertTrue(map.toString().contains("C:3"));

        map.put("A", 4);
        map.remove("C");
        assertTrue(map.toString().contains("A:4"));
        assertFalse(map.toString().contains("B:2"));
        assertFalse(map.toString().contains("C:3"));

        map.put("D", 5);
        map.put("E", 6);
        assertTrue(map.toString().contains("A:4"));
        assertFalse(map.toString().contains("B:2"));
        assertFalse(map.toString().contains("C:3"));
        assertTrue(map.toString().contains("D:5"));
        assertTrue(map.toString().contains("E:6"));

        map.clear();
        assertEquals("[]", map.toString());

        map.undo();
        assertTrue(map.toString().contains("A:4"));
        assertFalse(map.toString().contains("B:2"));
        assertFalse(map.toString().contains("C:3"));
        assertTrue(map.toString().contains("D:5"));
        assertTrue(map.toString().contains("E:6"));
    }

    @Test
    void testNestedUndoRedoPHashMap() {
        PHashMap<String, PHashMap<String, Integer>> parent = new PHashMap<>();
        PHashMap<String, Integer> map1 = new PHashMap<>();
        PHashMap<String, Integer> map2 = new PHashMap<>();
        PHashMap<String, Integer> map3 = new PHashMap<>();

        parent.put("map1", map1);
        parent.put("map2", map2);
        assertTrue(parent.toString().contains("map1:[]"));
        assertTrue(parent.toString().contains("map2:[]"));

        parent.get("map1").put("A", 1);
        parent.get("map1").put("B", 2);
        parent.get("map2").put("C", 3);
        assertTrue(parent.get("map1").toString().contains("A:1"));
        assertTrue(parent.get("map1").toString().contains("B:2"));
        assertTrue(parent.get("map2").toString().contains("C:3"));

        parent.undo();
        assertTrue(parent.get("map1").toString().contains("A:1"));
        assertTrue(parent.get("map1").toString().contains("B:2"));
        assertFalse(parent.get("map2").toString().contains("C:3"));

        parent.redo();
        assertTrue(parent.get("map1").toString().contains("A:1"));
        assertTrue(parent.get("map1").toString().contains("B:2"));
        assertTrue(parent.get("map2").toString().contains("C:3"));

        parent.put("map3", map3);
        parent.get("map3").put("D", 4);
        assertTrue(parent.get("map1").toString().contains("A:1"));
        assertTrue(parent.get("map1").toString().contains("B:2"));
        assertTrue(parent.get("map2").toString().contains("C:3"));
        assertTrue(parent.get("map3").toString().contains("D:4"));

        parent.get("map2").put("E", 5);
        assertTrue(parent.get("map1").toString().contains("A:1"));
        assertTrue(parent.get("map1").toString().contains("B:2"));
        assertTrue(parent.get("map2").toString().contains("C:3"));
        assertTrue(parent.get("map2").toString().contains("E:5"));
        assertTrue(parent.get("map3").toString().contains("D:4"));

        parent.undo();
        parent.undo();
        assertTrue(parent.get("map1").toString().contains("A:1"));
        assertTrue(parent.get("map1").toString().contains("B:2"));
        assertTrue(parent.get("map2").toString().contains("C:3"));
        assertFalse(parent.get("map2").toString().contains("E:5"));
        assertTrue(parent.toString().contains("map3"));
        assertFalse(parent.get("map3").toString().contains("D:4"));

        parent.undo();
        assertTrue(parent.get("map1").toString().contains("A:1"));
        assertTrue(parent.get("map1").toString().contains("B:2"));
        assertTrue(parent.get("map2").toString().contains("C:3"));
        assertFalse(parent.toString().contains("map3"));
    }

    @Test
    void testNestedUndoRedoPArray() {
        PHashMap<String, PArray<Integer>> parent = new PHashMap<>();
        PArray<Integer> arr1 = new PArray<>();
        PArray<Integer> arr2 = new PArray<>();
        PArray<Integer> arr3 = new PArray<>();

        parent.put("array1", arr1);
        parent.put("array2", arr2);
        assertTrue(parent.toString().contains("array1:[]"));
        assertTrue(parent.toString().contains("array2:[]"));

        parent.get("array1").add(1);
        parent.get("array1").add(2);
        parent.get("array2").add(3);
        parent.get("array2").add(4);
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3, 4]"));

        parent.undo();
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3]"));

        parent.undo();
        parent.undo();
        assertTrue(parent.toString().contains("array1:[1]"));
        assertTrue(parent.toString().contains("array2:[]"));

        parent.redo();
        parent.redo();
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3]"));

        parent.put("array3", arr3);
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3]"));
        assertTrue(parent.toString().contains("array3:[]"));

        parent.get("array3").add(4);
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3]"));
        assertTrue(parent.toString().contains("array3:[4]"));

        parent.get("array2").add(5);
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3, 5]"));
        assertTrue(parent.toString().contains("array3:[4]"));

        parent.undo();
        parent.undo();
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3]"));
        assertTrue(parent.toString().contains("array3:[]"));

        parent.undo();
        assertTrue(parent.toString().contains("array1:[1, 2]"));
        assertTrue(parent.toString().contains("array2:[3]"));
        assertFalse(parent.toString().contains("array3:[]"));
    }

    @Test
    void testNestedUndoRedoPDoublyLinkedList() {
        PHashMap<String, PDoublyLinkedList<Integer>> parent = new PHashMap<>();
        PDoublyLinkedList<Integer> list1 = new PDoublyLinkedList<>();
        PDoublyLinkedList<Integer> list2 = new PDoublyLinkedList<>();
        PDoublyLinkedList<Integer> list3 = new PDoublyLinkedList<>();

        parent.put("list1", list1);
        parent.put("list2", list2);
        assertTrue(parent.toString().contains("list1:[]"));
        assertTrue(parent.toString().contains("list2:[]"));

        parent.get("list1").add(1);
        parent.get("list1").add(2);
        parent.get("list2").add(3);
        parent.get("list2").add(4);
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3, 4]"));

        parent.undo();
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3]"));

        parent.undo();
        parent.undo();
        assertTrue(parent.toString().contains("list1:[1]"));
        assertTrue(parent.toString().contains("list2:[]"));

        parent.redo();
        parent.redo();
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3]"));

        parent.put("list3", list3);
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3]"));
        assertTrue(parent.toString().contains("list3:[]"));

        parent.get("list3").add(4);
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3]"));
        assertTrue(parent.toString().contains("list3:[4]"));

        parent.get("list2").add(5);
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3, 5]"));
        assertTrue(parent.toString().contains("list3:[4]"));

        parent.undo();
        parent.undo();
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3]"));
        assertTrue(parent.toString().contains("list3:[]"));

        parent.undo();
        assertTrue(parent.toString().contains("list1:[1, 2]"));
        assertTrue(parent.toString().contains("list2:[3]"));
        assertFalse(parent.toString().contains("list3:[]"));
    }
}