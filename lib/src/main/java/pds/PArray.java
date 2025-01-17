package pds;

import java.util.ArrayList;
import java.util.List;

import pds.SubClasses.CopyPathClasses.Head;
import pds.SubClasses.CopyPathClasses.Node;
import pds.SubClasses.UndoRedoClasses.UndoRedoDataStructure;
import pds.SubClasses.UndoRedoClasses.UndoRedoStack;

/**
 * Персистентный массив.
 * @param <E> тип элементов в массиве
 */
@SuppressWarnings("unchecked")
public class PArray<E> extends UndoRedoDataStructure {
    
    /* Высота (глубина) двоичного дерева персистентного массива */
    private int height;
    /* Число бит на каждую ноду двоичного дерева персистентного массива */
    private int bitsPerNode;
    /* Максимальный размер персистентного массива */
    private int maxSize;
    /* Маска для реализации алгоритма bit partitioning */
    private int mask;

    /**
     * Конструктор класса.
     * @param height высота (глубина) двоичного дерева персистентного массива
     * @param bitsPerNode число бит на каждую ноду двоичного дерева персистентного массива
     */
    public PArray(int height, int bitsPerNode) {
        super();
        this.height = height;
        this.bitsPerNode = bitsPerNode;
        this.maxSize = (int) Math.pow(2, bitsPerNode * height);
        this.mask = (int) Math.pow(2, bitsPerNode) - 1;
        Head<E> head = new Head<>(this.bitsPerNode);
        this.versions = new UndoRedoStack<>(head);
        this.changes = new UndoRedoStack<>();
    }

    /**
     * Конструктор класса со значениями по умолчанию (heigth = 3, bitsPerNode = 2).
     */
    public PArray() {
        this(3, 2);
    }

    /**
     * Конструктор класса.
     * @param other объект класса PArray
     */
    public PArray(PArray<E> other) {
        this(other.height, other.bitsPerNode);
        this.versions.clone(other.versions);
        this.changes.clone(other.changes);
    }

    /**
     * Преобразует персистентный массив в список.
     * @return список, содержащий элементы персистентного массива
     */
    public List<Object> toList() {
        List<Object> values;
        Head<E> Head = getHead();
        if (Head != null) {
            Object[] leafNodeValues;
            values = new ArrayList<>(Head.size());
            for (int i = 0; i < Head.size(); i = i + this.mask + 1) {
                leafNodeValues = getLeafNodeValues(Head, i);
                for (int j = 0; j < this.mask + 1; j++) {
                    if (leafNodeValues[j] != null) {
                        Object value = leafNodeValues[j];
                        if (value instanceof UndoRedoDataStructure) {
                            value = ((UndoRedoDataStructure) value).toList();
                        }
                        values.add((E) value);
                    }
                }
            }
        } else {
            values = new ArrayList<>(0);
        }
        return values;  
    }

    /**
     * Возвращает true, если элемент присутствует в персистентом массиве.
     * @param value элемент
     * @return true, если элемент присутствует в персистентом массиве; false, иначе
     */
    public boolean contains(Object value) {
        if (toList().contains(value)) {
            return true;
        }
        return false;
    }

    /**
     * Возвращает индекс элемента в персистентном массиве.
     * @param value элемент
     * @return индекс элемента в персистентом массиве, если присутствует; -1, иначе
     */
    public int indexOf(Object value) {
        List<Object> values = toList(); 
        if (values.contains(value)) {
            return values.indexOf(value);
        }
        return -1;
    }

    /**
     * Возвращает количество элементов в персистентном массиве.
     * @return количество элементов в персистентном массиве
     */
    public int size() {
        return getHead().size();
    }

    /**
     * Возвращает true, если персистентный массив пустой.
     * @return true, если персистентный массив пустой; false, если иначе
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Возвращает true, если персистентный массив полный.
     * @return true, если персистентный массив полный; false, если иначе
     */
    public boolean isFull() {
        return isFull(getHead());
    }


    /**
     * Возвращает из персистентного массива элемент по индексу.
     * @param index индекс элемента в персистентном массиве
     * @return элемент из персистентного массива
     */
    public E get(int index) {
        Head<E> head = getHead();
        return get(head, index);
    }

    /**
     * Добавляет элемент в конец персистентного массива.
     * @param value добавляемый элемент
     * @return true, если элемент был добавлен в персистентный массив
     */
    public boolean add(E value) {
        setParent(value);
        Head<E> head = new Head<>(this.bitsPerNode);
        head.clone(getHead());
        checkIfFull(head);
        newVersion(head);
        add(head, value);
        return true;
    }

    /**
     * Добавляет элемент в персистентный массив по индексу.
     * @param index индекс в персистентном массиве
     * @param value добавляемый элемент
     */
    public void add(int index, E value) {
        setParent(value);
        Head<E> oldHead = getHead();
        checkIfFull(oldHead);
        checkIndex(oldHead, index);
        Node<E> node = partCopyPath(oldHead, index);
        node.set(index & this.mask, value);
        Head<E> newHead = getHead();
        for (int i = index; i < oldHead.size(); i++) {
            add(newHead, (E) get(oldHead, i));
        }
    }

    /**
     * Добавляет все элементы из списка в конец персистентного массива.
     * @param values список, содержащий добавляемые элементы
     * @return true, если все элементы из списка были добавлены в персистентный массив
     */
    public boolean addAll(List<E> values) {
        Head<E> head = new Head<>(this.bitsPerNode);
        checkIfFull(head, values.size());
        head.clone(getHead());
        newVersion(head);
        for (int i = 0; i < values.size(); i++) {
            E value = values.get(i);
            setParent(value);
            add(head, value);
        }
        return true;
    }

    /**
     * Заменяет элемент из персистентного массива на новый.
     * @param index индекс элемента
     * @param value новый элемент
     * @return элемент до замены
     */
    public E set(int index, E value) {
        setParent(value);
        Head<E> oldHead = getHead();
        checkIfEmpty(oldHead);
        checkIndex(oldHead, index);
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.clone(oldHead);
        newVersion(newHead);
        E prevValue = get(newHead, index);
        set(newHead, index, value);
        return prevValue;
    }

    /**
     * Удаляет элемент из персистентного массива по индексу.
     * @param index индекс элемента
     * @return удаленный из персистентного массива элемент
     */
    public E remove(int index) {
        Head<E> oldHead = getHead();
        checkIndex(oldHead, index);
        checkIfEmpty(oldHead);
        Node<E> node = partCopyPath(oldHead, index);
        E value = (E) node.pop();
        Head<E>newHead = getHead();
        newHead.setSize(newHead.size() - 1);
        for (int i = index + 1; i < oldHead.size(); i++) {
            add(newHead, (E) get(oldHead, i));
        }
        return value;
    }

    /**
     * Удаляет все элементы из персистентного массива.
     */
    public void clear() {
        Head<E> head = new Head<>(this.bitsPerNode);
        newVersion(head);
    }

    /**
     * Возвращает максимальный размер персистентного массива.
     * @return максимальный размер персистентного массива
     */
    public int maxSize() {
        return this.maxSize;
    }
    
    /**
     * Возвращает число бит на каждую ноду двоичного дерева персистентного массива.
     * @return число бит на каждую ноду двоичного дерева
     */
    public int getBitsPerNode() {
        return this.bitsPerNode;
    }

    /**
     * Возвращает высоту (глубину) двоичного дерева персистентного массива.
     * @return высота двоичного дерева
     */
    public int getheight() {
        return this.height;
    }

    private Node<E> copyPath(Head<E> Head, int index) {
        Node<E> currentNode;
        Node<E> newNode;
        currentNode = Head.getRoot();
        for (int level = (this.height - 1) * this.bitsPerNode; level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            if (currentNode.isEmpty()) {
                newNode = new Node<>(this.bitsPerNode);
                currentNode.add(newNode);
            } else {
                if (id == currentNode.getCount()) {
                    newNode = new Node<>(this.bitsPerNode);
                    currentNode.add(newNode);
                } else {
                    newNode = new Node<>(this.bitsPerNode);
                    newNode.clone((Node<E>) currentNode.get(id));
                    currentNode.set(id, newNode);
                }
            }
            currentNode = newNode;
        }
        return currentNode;
    }

    private Node<E> partCopyPath(Head<E> Head, int index) {
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.partClone(Head, (index >> (this.bitsPerNode * (height - 1))) & this.mask);
        newHead.setSize(index + 1);
        newVersion(newHead);
        Node<E> currentNode = newHead.getRoot();
        for (int level = this.bitsPerNode * (height - 1); level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            int idNext = (index >> (level - this.bitsPerNode)) & this.mask;
            Node<E> newNode = new Node<>(this.bitsPerNode);
            newNode.partClone((Node<E>) currentNode.get(id), idNext);
            currentNode.set(id, newNode);
            currentNode = newNode;
        }
        return currentNode;
    }

    private E get(Head<E> head, int index) {
        return (E) getLeafNodeValues(head, index)[index & this.mask];
    }

    private void add(Head<E> head, E value) {
        head.setSize(head.size() + 1);
        Node<E> node = copyPath(head, head.size() - 1);
        node.add(value);
    }

    private void set(Head<E> head, int index, E value) {
        Node<E> node = copyPath(head, index);
        node.set(index & this.mask, value);
    }

    private Object[] getLeafNodeValues(Head<E> head, int index) {
        return getLeafNode(head, index).get();
    }

    private Node<E> getLeafNode(Head<E> head, int index) {
        Node<E> node;
        checkIndex(head, index);
        node = head.getRoot();
        for (int level = this.bitsPerNode * (this.height - 1); level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            node = (Node<E>) node.get(id);
        }
        return node;
    }

    private Head<E> getHead() {
        return (Head<E>) this.versions.getCurrent();
    }

    private boolean isFull(Head<E> head) {
        return head.size() == this.maxSize;
    }

    private void checkIfFull(Head<E> head) {
        if (isFull(head)) {
            throw new IllegalStateException("Достигнуто максимальное число элементов в персистентном массиве");
        }
    }

    private void checkIfFull(Head<E> head, int delta) {
        if (head.size() + delta > this.maxSize) {
            throw new IllegalStateException("Достигнуто максимальное число элементов в персистентном массиве");
        }
    }
    
    private void checkIfEmpty(Head<E> head) {
        if (head.size() == 0) {
            throw new IllegalStateException("Персистентный массив пуст");
        }
    }

    private void checkIndex(Head<E> head, int index) {
        if ((index < 0) || (index >= head.size())) {
            throw new IndexOutOfBoundsException("Неверный индекс элемента в персистентном массиве");
        }
    }
}