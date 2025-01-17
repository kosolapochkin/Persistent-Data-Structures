package pds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pds.SubClasses.PHashMapClasses.MapNode;
import pds.SubClasses.UndoRedoClasses.UndoRedoDataStructure;
import pds.SubClasses.UndoRedoClasses.UndoRedoStack;

/**
 * Персистентный ассоциативный массив на основе хэш-таблицы.
 * @param <K> тип ключей ассоциативного массива
 * @param <V> тип значений ассоциативного массива
 */
@SuppressWarnings("unchecked")
public class PHashMap<K, V> extends UndoRedoDataStructure {

    /* Размер хэш-таблицы */
    private int width;
    /* Массив, используемый для реализации хэш-таблицы */
    private Object[] hashTable; 

    /**
     * Конструктор класса.
     * @param width размер хэш-таблицы
     */
    public PHashMap(int width) {
        super();
        this.width = width;
        this.hashTable = new Object[width];
        for (int i = 0; i < width; i++) {
            this.hashTable[i] = new PDoublyLinkedList<MapNode<K, V>>();
        }
        this.versions = new UndoRedoStack<>();
        this.changes = new UndoRedoStack<>();
    }

    /**
     * Конструктор класса со значением хэш-таблицы по умолчанию (width = 32).
     */
    public PHashMap() {
        this(64);
    }

    /**
     * Конструктор класса.
     * @param other объект класса PHashTable
     */
    public PHashMap(PHashMap<K, V> other) {
        this(other.width);
        for (int i = 0; i < width; i++) {
            this.hashTable[i] = new PDoublyLinkedList<MapNode<K, V>>((PDoublyLinkedList<MapNode<K, V>>) other.hashTable[i]);
        }
        this.versions.clone(other.versions);
        this.changes.clone(other.changes);
    }

    /**
     * Возвращает к предыдущей версии структуры данных.
     */
    @Override
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
                ((UndoRedoDataStructure) peek).undo();
            }
            this.changes.getRedo().push(this.changes.getUndo().pop());
        }
    }

    /**
     * Возврат к версии структуры данных до выполнения {@link #undo() undo}.
     */
    @Override
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
                ((UndoRedoDataStructure) peek).redo();
            }
            this.changes.getUndo().push(this.changes.getRedo().pop());
        }
    }

    /**
     * Преобразует персистентный ассоциатный массив в список.
     * @return список, содержащий элементы персистентного ассоциативного массива
     */
    public List<Object> toList() {
        List<Object> list = new ArrayList<>();
        list.addAll(entrySet());
        return list;
    }

    /**
     * Возвращает список всех пар "ключ-значение" ассоциативного массива.
     * @return список всех пар "ключ-значение" ассоциативного массива
     */
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

    /**
     * Возвращает список всех ключей в ассоциативном массиве.
     * @return список всех ключей в ассоциативном массиве
     */
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

    /**
     * Возвращает список всех значений в ассоциативном массиве.
     * @return список всех значений в ассоциативном массиве
     */
    public List<V> values() {
        ArrayList<V> values = new ArrayList<>();
        for (int index = 0; index < this.width; index++) {
            PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) hashTable[index];
            for (int i = 0; i < list.size(); i++) {
                values.add(list.get(i).getValue());
            }
        }
        return values;
    }

    /**
     * Возвращает true если ассоциативный массив содержит ключ.
     * @param key ключ
     * @return true, если ассоциативный массив содержит ключ; false, иначе
     */
    public boolean containsKey(K key) {
        return keySet().contains(key);
    }

    /**
     * Возвращает true если ассоциативный массив содержит значение.
     * @param value значение
     * @return true, если ассоциативный массив содержит значение; false, иначе
     */
    public boolean containsValue(V value) {
        return values().contains(value);
    }

    /**
     * Возвращает число пар "ключ-значение" в ассоциативном массиве.
     * @return число пар "ключ-значение" в ассоциативном массиве
     */
    public int size() {
        return keySet().size();
    }

    /**
     * Возвращает true, если ассоциативный массив пустой.
     * @return true, если ассоциативный массив пустой; false, если иначе
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Возвращает из ассоциативного массива значение по ключу.
     * @param key ключ
     * @return значение, если содержится; null иначе
     */
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

    /**
     * Добавляет пару "ключ-значение" в асссоциативный массив.
     * @param key ключ
     * @param value значение
     */
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

    /**
     * Удаляет пару "ключ-значение" в асссоциативном массиве по ключу.
     * @param key ключ
     * @return удаленное значение, если удалено; null иначе
     */
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

    /**
     * Удаляет все элементы из персистентного ассоциативного массива.
     */
    public void clear() {
        ArrayList<PDoublyLinkedList<MapNode<K, V>>> changed = new ArrayList<>(this.width);
        for (int i = 0; i < this.width; i++) {
            PDoublyLinkedList<MapNode<K, V>> list = (PDoublyLinkedList<MapNode<K, V>>) this.hashTable[i];
            list.clear();
            changed.add(list);
        }
        newVersion(changed);
    }

    /**
     * Возвращает размер хэш-таблицы.
     * @return размер хэш-таблицы
     */
    public int getWidth() {
        return this.width;
    }

    private int getHash(K key) {
        return Math.abs(key.toString().hashCode()) % this.width;
    }
}
