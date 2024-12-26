package pds;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import pds.PersistentClasses.PArray;

import static org.junit.jupiter.api.Assertions.*;

class PArrayTest {
    PArray<Integer> arr;

    @BeforeEach
    void initPArray() {
        arr = new PArray<>(3, 1);
    }

    @Test
    void testAdd() {
        arr.add(1);
        assertEquals("[1]", arr.toString());
    }
    
    @Test
    void testAddByIndex() {
        arr.add(1);
        arr.add(3);
        arr.add(1, 2);
        assertEquals("[1, 2, 3]", arr.toString());
    }

    @Test
    void testAddAll() {
        arr.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", arr.toString());
        assertThrowsExactly(IllegalStateException.class, () -> arr.add(9));
    }

    @Test
    void testGet() {
        arr.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals(2, arr.get(1));
    }

    @Test
    void testSize() {
        arr.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals(4, arr.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(arr.isEmpty());
        arr.add(1);
        assertFalse(arr.isEmpty());
    }

    @Test
    void testUndoRedo() {
        arr.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals(2, arr.getVersionCount());
        assertEquals("[1, 2, 3, 4]", arr.toString());

        arr.add(5);
        assertEquals(3, arr.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5]", arr.toString());

        arr.addAll(Arrays.asList(6, 7, 8));
        assertEquals(4, arr.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", arr.toString());

        arr.undo();
        assertEquals(4, arr.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5]", arr.toString());

        arr.redo();
        assertEquals(4, arr.getVersionCount());
        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", arr.toString());

        arr.undo();
        arr.undo();
        arr.add(1);
        assertEquals(3, arr.getVersionCount());
        assertEquals("[1, 2, 3, 4, 1]", arr.toString());

        arr.redo();
        assertEquals(3, arr.getVersionCount());
        assertEquals("[1, 2, 3, 4, 1]", arr.toString());
    }

   
    @Test
    void testPop() {
        arr.addAll(Arrays.asList(1, 2, 3, 4));
        assertEquals(4, arr.pop());
        assertEquals(3, arr.pop());

        arr.undo();
        assertEquals("[1, 2, 3]", arr.toString());
    }

    @Test
    void testSet() {
        arr.addAll(Arrays.asList(1, 2, 3, 4));

        arr.set(0, 0);
        assertEquals("[0, 2, 3, 4]", arr.toString());

        arr.set(3, 5);
        assertEquals("[0, 2, 3, 5]", arr.toString());
        
        arr.undo();
        assertEquals("[0, 2, 3, 4]", arr.toString());
    }

    @Test
    void testRemove() {
        arr.addAll(Arrays.asList(1, 2, 3, 4, 5));

        arr.remove(1);
        assertEquals("[1, 3, 4, 5]", arr.toString());

        arr.remove(3);
        assertEquals("[1, 3, 4]", arr.toString());

        assertThrowsExactly(IndexOutOfBoundsException.class, () -> arr.remove(3));
    }

    @Test
    void testClear() {
        arr.addAll(Arrays.asList(1, 2, 3, 4, 5));

        arr.clear();
        assertEquals("[]", arr.toString());

        arr.undo();
        assertEquals("[1, 2, 3, 4, 5]", arr.toString());
    }

    @Test
    void testNestedUndoRedo() {
        PArray<PArray<Integer>> parent = new PArray<>();
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
    void testPairs() {
        arr.addAll(Arrays.asList(1, 2, 3, 4));
        
        PArray<Integer> arrCopy = new PArray<>(arr);
        assertEquals("[1, 2, 3, 4]", arrCopy.toString());

        arrCopy.add(5);
        assertEquals("[1, 2, 3, 4, 5]", arrCopy.toString());
        assertEquals("[1, 2, 3, 4]", arr.toString());

        arr.add(0, 0);
        assertEquals("[1, 2, 3, 4, 5]", arrCopy.toString());
        assertEquals("[0, 1, 2, 3, 4]", arr.toString());

        arrCopy.undo();
        assertEquals("[1, 2, 3, 4]", arrCopy.toString());
        assertEquals("[0, 1, 2, 3, 4]", arr.toString());
    }
}