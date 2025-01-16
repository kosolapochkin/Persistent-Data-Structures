package pds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TestPDoublyLinkedList {
    PDoublyLinkedList<Integer> lst;

    @BeforeEach
    void initPDoublyLinkedList() {
        lst = new PDoublyLinkedList<>(3, 1);
    }

    @Test
    void testAdd() {
        lst.add(1);
        assertEquals("[1]", lst.toString());
        assertEquals("[1, null]", lst.toArray().toString());
    }

    @Test
    void testAddByIndex() {
        lst.add(1);
        lst.add(3);
        lst.add(1, 2);
        assertEquals("[1, 2, 3]", lst.toString());
        assertEquals("[1, 3, 2, null]", lst.toArray().toString());
    }

    @Test
    void testAddAll() {
        lst.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", lst.toString());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", lst.toArray().toString());
        assertThrowsExactly(IllegalStateException.class, () -> lst.add(9));
    }

    @Test
    void testGet() {
        lst.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals(2, lst.get(1));
    }

    @Test
    void testSize() {
        lst.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals(4, lst.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(lst.isEmpty());
        lst.add(1);
        assertFalse(lst.isEmpty());
    }

    @Test
    void testUndoRedo() {
        lst.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals(2, lst.getVersionCount());
        assertEquals("[1, 2, 3, 4]", lst.toString());
        assertEquals("[1, 2, 3, 4]", lst.toArray().toString());

        lst.add(5);
        assertEquals(3, lst.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5]", lst.toString());
        assertEquals("[1, 2, 3, 4, 5, null]", lst.toArray().toString());

        lst.addAll(Arrays.asList(6, 7, 8));
        assertEquals(4, lst.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", lst.toString());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", lst.toArray().toString());

        lst.undo();
        assertEquals(4, lst.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5]", lst.toString());
        assertEquals("[1, 2, 3, 4, 5, null]", lst.toArray().toString());

        lst.redo();
        assertEquals(4, lst.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", lst.toString());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", lst.toArray().toString());

        lst.undo();
        lst.undo();
        lst.add(1);
        assertEquals(3, lst.getVersionCount());
        assertEquals("[1, 2, 3, 4, 1]", lst.toString());
        assertEquals("[1, 2, 3, 4, 1, null]", lst.toArray().toString());

        lst.redo();
        assertEquals(3, lst.getVersionCount());
        assertEquals("[1, 2, 3, 4, 1]", lst.toString());
        assertEquals("[1, 2, 3, 4, 1, null]", lst.toArray().toString());
    }

    @Test
    void testSet() {
        lst.addAll(Arrays.asList(1, 2, 3, 4));

        lst.set(0, 0);
        assertEquals("[0, 2, 3, 4]", lst.toString());
        assertEquals("[0, 2, 3, 4]", lst.toArray().toString());

        lst.set(3, 5);
        assertEquals("[0, 2, 3, 5]", lst.toString());
        assertEquals("[0, 2, 3, 5]", lst.toArray().toString());
        
        lst.undo();
        assertEquals("[0, 2, 3, 4]", lst.toString());
        assertEquals("[0, 2, 3, 4]", lst.toArray().toString());
    }

    @Test
    void testRemove() {
        lst.addAll(Arrays.asList(1, 2, 3, 4, 5));

        lst.remove(1);
        assertEquals("[1, 3, 4, 5]", lst.toString());
        assertEquals("[1, null, 3, 4, 5, null]", lst.toArray().toString());

        lst.remove(3);
        assertEquals("[1, 3, 4]", lst.toString());
        assertEquals("[1, null, 3, 4, null, null]", lst.toArray().toString());

        assertThrowsExactly(IndexOutOfBoundsException.class, () -> lst.remove(3));

        lst.remove(2);
        assertEquals("[1, 3]", lst.toString());
        assertEquals("[1, null, 3, null, null, null]", lst.toArray().toString());

        lst.remove(0);
        assertEquals("[3]", lst.toString());
        assertEquals("[null, null, 3, null, null, null]", lst.toArray().toString());

        lst.remove(0);
        assertEquals("[]", lst.toString());
        assertEquals("[]", lst.toArray().toString());
    }

    @Test
    void testClear() {
        lst.addAll(Arrays.asList(1, 2, 3, 4, 5));

        lst.clear();
        assertEquals("[]", lst.toString());
        assertEquals("[]", lst.toArray().toString());

        lst.undo();
        assertEquals("[1, 2, 3, 4, 5]", lst.toString());
        assertEquals("[1, 2, 3, 4, 5, null]", lst.toArray().toString());
    }

    @Test
    void testPairs() {
        lst.addAll(Arrays.asList(1, 2, 3, 4));
        
        PDoublyLinkedList<Integer> lstCopy = new PDoublyLinkedList<>(lst);
        assertEquals("[1, 2, 3, 4]", lstCopy.toString());

        lstCopy.add(5);
        assertEquals("[1, 2, 3, 4, 5]", lstCopy.toString());
        assertEquals("[1, 2, 3, 4]", lst.toString());

        lst.add(0, 0);
        assertEquals("[1, 2, 3, 4, 5]", lstCopy.toString());
        assertEquals("[0, 1, 2, 3, 4]", lst.toString());

        lstCopy.undo();
        assertEquals("[1, 2, 3, 4]", lstCopy.toString());
        assertEquals("[0, 1, 2, 3, 4]", lst.toString());
    }

    @Test
    void testNodePlacing() {
        lst.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals("[1, 2, 3, 4]", lst.toString());
        assertEquals("[1, 2, 3, 4]", lst.toArray().toString());

        lst.remove(1);
        assertEquals("[1, 3, 4]", lst.toString());
        assertEquals("[1, null, 3, 4]", lst.toArray().toString());

        lst.add(2, 5);
        assertEquals("[1, 3, 5, 4]", lst.toString());
        assertEquals("[1, 5, 3, 4]", lst.toArray().toString());

        lst.remove(0);
        assertEquals("[3, 5, 4]", lst.toString());
        assertEquals("[null, 5, 3, 4]", lst.toArray().toString());

        lst.remove(0);
        assertEquals("[5, 4]", lst.toString());
        assertEquals("[null, 5, null, 4]", lst.toArray().toString());

        lst.add(6);
        assertEquals("[5, 4, 6]", lst.toString());
        assertEquals("[6, 5, null, 4]", lst.toArray().toString());

        lst.add(1, 7);
        assertEquals("[5, 7, 4, 6]", lst.toString());
        assertEquals("[6, 5, 7, 4]", lst.toArray().toString());

        lst.add(1, 8);
        assertEquals("[5, 8, 7, 4, 6]", lst.toString());
        assertEquals("[6, 5, 7, 4, 8, null]", lst.toArray().toString());
    }

    @Test
    void testNestedUndoRedoPDoublyLinkedList() {
        PDoublyLinkedList<PDoublyLinkedList<Integer>> parent = new PDoublyLinkedList<>();
        PDoublyLinkedList<Integer> lst1 = new PDoublyLinkedList<>();
        PDoublyLinkedList<Integer> lst2 = new PDoublyLinkedList<>();
        PDoublyLinkedList<Integer> lst3 = new PDoublyLinkedList<>();

        parent.addAll(Arrays.asList(lst1, lst2));
        assertEquals("[[], []]", parent.toString());

        parent.get(0).add(1);
        parent.get(0).add(2);
        parent.get(1).add(3);
        parent.get(1).add(4);
        assertEquals("[[1, 2], [3, 4]]", parent.toString());

        parent.undo();
        assertEquals("[[1, 2], [3]]", parent.toString());

        parent.undo();
        parent.undo();
        assertEquals("[[1], []]", parent.toString());

        parent.redo();
        parent.redo();
        assertEquals("[[1, 2], [3]]", parent.toString());

        parent.add(1, lst3);
        assertEquals("[[1, 2], [], [3]]", parent.toString());

        parent.get(1).add(4);
        assertEquals("[[1, 2], [4], [3]]", parent.toString());

        parent.get(2).add(5);
        assertEquals("[[1, 2], [4], [3, 5]]", parent.toString());

        parent.undo();
        parent.undo();
        assertEquals("[[1, 2], [], [3]]", parent.toString());

        parent.undo();
        assertEquals("[[1, 2], [3]]", parent.toString());
    }

    @Test
    void testNestedUndoRedoPArray() {
        PDoublyLinkedList<PArray<Integer>> parent = new PDoublyLinkedList<>();
        PArray<Integer> arr1 = new PArray<>();
        PArray<Integer> arr2 = new PArray<>();
        PArray<Integer> arr3 = new PArray<>();

        parent.addAll(Arrays.asList(arr1, arr2));
        assertEquals("[[], []]", parent.toString());

        parent.get(0).add(1);
        parent.get(0).add(2);
        parent.get(1).add(3);
        parent.get(1).add(4);
        assertEquals("[[1, 2], [3, 4]]", parent.toString());

        parent.undo();
        assertEquals("[[1, 2], [3]]", parent.toString());

        parent.undo();
        parent.undo();
        assertEquals("[[1], []]", parent.toString());

        parent.redo();
        parent.redo();
        assertEquals("[[1, 2], [3]]", parent.toString());

        parent.add(1, arr3);
        assertEquals("[[1, 2], [], [3]]", parent.toString());

        parent.get(1).add(4);
        assertEquals("[[1, 2], [4], [3]]", parent.toString());

        parent.get(2).add(5);
        assertEquals("[[1, 2], [4], [3, 5]]", parent.toString());

        parent.undo();
        parent.undo();
        assertEquals("[[1, 2], [], [3]]", parent.toString());

        parent.undo();
        assertEquals("[[1, 2], [3]]", parent.toString());
    }

    @Test
    void testNestedUndoRedoPHashMap() {
        PDoublyLinkedList<PHashMap<String, Integer>> parent = new PDoublyLinkedList<>();
        PHashMap<String, Integer> map1 = new PHashMap<>();
        PHashMap<String, Integer> map2 = new PHashMap<>();
        PHashMap<String, Integer> map3 = new PHashMap<>();

        parent.addAll(Arrays.asList(map1, map2));
        assertEquals("[[], []]", parent.toString());

        parent.get(0).put("A", 1);
        parent.get(0).put("B", 2);
        parent.get(1).put("C", 3);
        assertTrue(parent.get(0).toString().contains("A:1"));
        assertTrue(parent.get(0).toString().contains("B:2"));
        assertTrue(parent.get(1).toString().contains("C:3"));

        parent.undo();
        assertTrue(parent.get(0).toString().contains("A:1"));
        assertTrue(parent.get(0).toString().contains("B:2"));
        assertFalse(parent.get(1).toString().contains("C:3"));

        parent.redo();
        assertTrue(parent.get(0).toString().contains("A:1"));
        assertTrue(parent.get(0).toString().contains("B:2"));
        assertTrue(parent.get(1).toString().contains("C:3"));

        parent.add(map3);
        parent.get(2).put("D", 4);
        assertTrue(parent.get(0).toString().contains("A:1"));
        assertTrue(parent.get(0).toString().contains("B:2"));
        assertTrue(parent.get(1).toString().contains("C:3"));
        assertTrue(parent.get(2).toString().contains("D:4"));

        parent.get(2).put("E", 5);
        assertTrue(parent.get(0).toString().contains("A:1"));
        assertTrue(parent.get(0).toString().contains("B:2"));
        assertTrue(parent.get(1).toString().contains("C:3"));
        assertTrue(parent.get(2).toString().contains("D:4"));
        assertTrue(parent.get(2).toString().contains("E:5"));

        parent.undo();
        parent.undo();
        assertTrue(parent.get(0).toString().contains("A:1"));
        assertTrue(parent.get(0).toString().contains("B:2"));
        assertTrue(parent.get(1).toString().contains("C:3"));
        assertTrue(parent.size() == 3);
        assertFalse(parent.get(2).toString().contains("D:4"));
        assertFalse(parent.get(2).toString().contains("E:5"));

        parent.undo();
        assertTrue(parent.get(0).toString().contains("A:1"));
        assertTrue(parent.get(0).toString().contains("B:2"));
        assertTrue(parent.get(1).toString().contains("C:3"));
        assertFalse(parent.size() == 3);
    }
}