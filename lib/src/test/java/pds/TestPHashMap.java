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
        assertEquals("{}", map.toString());

        map.undo();
        assertTrue(map.toString().contains("A:4"));
        assertFalse(map.toString().contains("B:2"));
        assertFalse(map.toString().contains("C:3"));
        assertTrue(map.toString().contains("D:5"));
        assertTrue(map.toString().contains("E:6"));
    }

    @Test
    void testNestedUndoRedo() {
        PHashMap<String, PHashMap<String, Integer>> parent = new PHashMap<>();
        PHashMap<String, Integer> map1 = new PHashMap<>();
        PHashMap<String, Integer> map2 = new PHashMap<>();
        PHashMap<String, Integer> map3 = new PHashMap<>();

        parent.put("map1", map1);
        parent.put("map2", map2);
        assertEquals("{map1:{}, map2:{}}", parent.toString());

        parent.get("map1").put("A", 1);
        parent.get("map1").put("B", 2);
        parent.get("map2").put("C", 3);
        assertEquals("{map1:{B:2, A:1}, map2:{C:3}}", parent.toString());

        parent.undo();
        assertEquals("{map1:{B:2, A:1}, map2:{}}", parent.toString());

        parent.redo();
        assertEquals("{map1:{B:2, A:1}, map2:{C:3}}", parent.toString());

        parent.put("map3", map3);
        parent.get("map3").put("D", 4);
        assertEquals("{map3:{D:4}, map1:{B:2, A:1}, map2:{C:3}}", parent.toString());

        parent.get("map2").put("E", 5);
        assertEquals("{map3:{D:4}, map1:{B:2, A:1}, map2:{C:3, E:5}}", parent.toString());

        parent.undo();
        parent.undo();
        assertEquals("{map3:{}, map1:{B:2, A:1}, map2:{C:3}}", parent.toString());

        parent.undo();
        assertEquals("{map1:{B:2, A:1}, map2:{C:3}}", parent.toString());
    }
}