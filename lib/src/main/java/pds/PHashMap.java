package pds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pds.SubClasses.PHashMapClasses.MapNode;
import pds.SubClasses.UndoRedoClasses.UndoRedoDataStructure;
import pds.SubClasses.UndoRedoClasses.UndoRedoStack;

/**
 * Персистентный ассоциативный массив
 * @value width - размер хэш-таблицы
 */
@SuppressWarnings("unchecked")
public class PHashMap<K, V> implements UndoRedoDataStructure {

    private int width;
    private int mask;
    private Object[] hashTable; 
    private UndoRedoStack<?> versions;
    private UndoRedoStack<?> changes;
    private PHashMap<K, V> parent;

    public PHashMap() {
        this(32);
    }

    public PHashMap(int width) {
        this.width = width;
        this.mask = width - 1;
        this.hashTable = new Object[width];
        for (int i = 0; i < width; i++) {
            this.hashTable[i] = new PDoublyLinkedList<MapNode<K, V>>();
        }
        this.versions = new UndoRedoStack<>();
        this.changes = new UndoRedoStack<>();
    }

    public void undo() {
        if (!this.changes.getUndo().isEmpty()) {
            Object peek = this.changes.getUndo().peek();
            if (peek == null) {
                Object insts = this.versions.getUndo().peek();
                if (insts instanceof List) {
                    for (PDoublyLinkedList<?> inst: (List<PDoublyLinkedList<?>>) insts) {
                        inst.undo();
                    }
                } else {
                    ((PDoublyLinkedList<?>) insts).undo();
                }
                this.versions.undo();
            } else {
                ((PHashMap<K, V>) peek).undo();
            }
            this.changes.getRedo().push(this.changes.getUndo().pop());
        }
    }

    public void redo() {
        if (!this.changes.getRedo().isEmpty()) {
            Object peek = this.changes.getRedo().peek();
            if (peek == null) {
                Object insts = this.versions.getRedo().peek();
                if (insts instanceof List) {
                    for (PDoublyLinkedList<?> inst: (List<PDoublyLinkedList<?>>) insts) {
                        inst.redo();
                    }
                } else {
                    ((PDoublyLinkedList<?>) insts).redo();
                }
                this.versions.redo();
            } else {
                ((PHashMap<K, V>) peek).redo();
            }
            this.changes.getUndo().push(this.changes.getRedo().pop());
        }
    }

    public int getCurrentVersion() {
        return this.versions.getCurrentVersion();
    }

    public int getVersionCount() {
        return this.versions.getVersionCount();
    }

    public void clear() {
        ArrayList<PDoublyLinkedList<MapNode<K, V>>> changed = new ArrayList<>(this.width);
        for (int i = 0; i < this.width; i++) {
            PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) this.hashTable[i];
            list.clear();
            changed.add(list);
        }
        newVersion(changed);
    }

    public Set<MapNode<K, V>> entrySet() {
        Set<MapNode<K, V>> entries = new HashSet<>();
        for (int index = 0; index < this.width; index++) {
            PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) hashTable[index];
            for (int i = 0; i < list.size(); i++) {
                entries.add(list.get(i));
            }
        }
        return entries;
    }

    public V get(K key) {
        int index = getHash(key);
        PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) hashTable[index];
        for (int i = 0; i < list.size(); i++) {
            MapNode<K, V> listElement = list.get(i);
            if (listElement.getKey().equals(key)) {
                return listElement.getValue();
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (int index = 0; index < this.width; index++) {
            PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) hashTable[index];
            for (int i = 0; i < list.size(); i++) {
                keys.add(list.get(i).getKey());
            }
        }
        return keys;
    }

    public void put(K key, V value) {
        setParent(value);
        int index = getHash(key);
        PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) hashTable[index];
        boolean exists = false;
        for (int i = 0; i < list.size(); i++) {
            MapNode<K, V> listElement = list.get(i);
            if (listElement.getKey().equals(key)) {
                list.set(i, new MapNode<>(key, value));
                exists = true;
            }
        }
        if (!exists) {
            list.add(new MapNode<>(key, value));
        }
        newVersion(list);
    }

    public V remove(K key) {
        int index = getHash(key);
        PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) hashTable[index];
        for (int i = 0; i < list.size(); i++) {
            MapNode<K, V> listElement = list.get(i);
            if (listElement.getKey().equals(key)) {
                V value = listElement.getValue();
                list.remove(i);
                newVersion(list);
                return value;
            }
        }
        return null;
    }

    public int size() {
        return keySet().size();
    }

    public List<V> values() {
        LinkedList<V> values = new LinkedList<>();
        for (int index = 0; index < this.width; index++) {
            PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) hashTable[index];
            for (int i = 0; i < list.size(); i++) {
                values.add(list.get(i).getValue());
            }
        }
        return values;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        String output = "{";
        for (Object entry: entrySet()) {
            output = output + entry.toString() + ", ";
        }
        output = output.substring(0, output.length() - 2) + "}";
        return output;
    }

    private void newVersion(Object object) {
        if (this.parent != null) {
            parent.changes.getUndo().push(this);
        }
        this.changes.getUndo().push(null);
        this.changes.getRedo().clear();
        this.versions.newVersion(object);
    }

    private void setParent(Object object) {
        if (isPersistent(object)) {
            ((PHashMap<K, V>) object).parent = this;
        }
    }

    private boolean isPersistent(Object object) {
        if (object instanceof PHashMap) {
            return true;
        }
        return false;
    }

    private int getHash(K key) {
        return key.toString().hashCode() & this.mask;
    }
}
