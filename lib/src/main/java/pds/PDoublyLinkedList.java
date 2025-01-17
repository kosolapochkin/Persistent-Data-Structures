package pds;

import java.util.ArrayList;
import java.util.List;

import pds.SubClasses.CopyPathClasses.HeadList;
import pds.SubClasses.CopyPathClasses.Node;
import pds.SubClasses.PDoublyLinkedListClasses.ListNode;
import pds.SubClasses.UndoRedoClasses.UndoRedoDataStructure;
import pds.SubClasses.UndoRedoClasses.UndoRedoStack;

/**
 * Персистентный двусвязный список
 * @param <E> тип элементов в списке
 */
@SuppressWarnings("unchecked")
public class PDoublyLinkedList<E> extends UndoRedoDataStructure {

    /* Высота (глубина) двоичного дерева персистентного списка */
    private int height;
    /* Число бит на каждую ноду двоичного дерева персистентного списка */
    private int bitsPerNode;
    /* Максимальный размер персистентного списка */
    private int maxSize;
    /* Маска для реализации алгоритма bit partitioning */
    private int mask;

    /**
     * Конструктор класса.
     * @param height высота (глубина) двоичного дерева персистентного списка
     * @param bitsPerNode число бит на каждую ноду двоичного дерева персистентного списка
     */
    public PDoublyLinkedList(int height, int bitsPerNode) {
        super();
        this.height = height;
        this.bitsPerNode = bitsPerNode;
        this.maxSize = (int) Math.pow(2, bitsPerNode * height);
        this.mask = (int) Math.pow(2, bitsPerNode) - 1;
        HeadList<E> HeadList = new HeadList<>(this.bitsPerNode);
        this.versions = new UndoRedoStack<>(HeadList);
        this.changes = new UndoRedoStack<>();
    }

    /**
     * Конструктор класса со значениями по умолчанию (heigth = 3, bitsPerNode = 2).
     */
    public PDoublyLinkedList() {
        this(3, 2);
    }

    /**
     * Конструктор класса.
     * @param other объект класса PDoublyLinkedList
     */
    public PDoublyLinkedList(PDoublyLinkedList<E> other) {
        this(other.height, other.bitsPerNode);
        this.versions.clone(other.versions);
        this.changes.clone(other.changes);
    }

    /**
     * Преобразует персистентный список в список.
     * @return список, содержащий элементы персистентного списка
     */
    public List<Object> toList() {
        List<Object> values;
        HeadList<ListNode<E>> head = getHead();
        if (head != null) {
            values = new ArrayList<>(head.getSize());
            Integer index = head.getFirst();
            for (int i = 0; i < head.getSize(); i++) {
                ListNode<E> listElement = getListNode(index);
                Object value = listElement.getValue();
                if (value instanceof UndoRedoDataStructure) {
                    value = ((UndoRedoDataStructure) value).toList();
                }
                values.add((E) value);
                index = listElement.getNext();
            }
        } else {
            values = new ArrayList<>(0);
        }
        return values;  
    }

    /**
     * Возвращает true, если элемент присутствует в персистентом списке.
     * @param value элемент
     * @return true, если элемент присутствует в персистентом списке; false, иначе
     */
    public boolean contains(Object value) {
        if (toList().contains(value)) {
            return true;
        }
        return false;
    }

    /**
     * Возвращает индекс элемента в персистентном списке.
     * @param value элемент
     * @return индекс элемента в персистентом списке, если присутствует; -1, иначе
     */
    public int indexOf(Object value) {
        List<Object> values = toList(); 
        if (values.contains(value)) {
            return values.indexOf(value);
        }
        return -1;
    }

    /**
     * Возвращает количество элементов в персистентном списке.
     * @return количество элементов в персистентном списке
     */
    public int size() {
        return getHead().getSize();
    }

    /**
     * Возврщает true, если персистентный список пустой.
     * @return true, если персистентный список пустой; false, если иначе
     */
    public boolean isEmpty() {
        return isEmpty(getHead());
    }

    /**
     * Возвращает true, если персистентный список полный.
     * @return true, если персистентный список полный; false, если иначе
     */
    public boolean isFull() {
        return isFull(getHead(), 0);
    }

    /**
     * Возвращает из персистентного списка элемент по индексу.
     * @param index индекс элемента в персистентном списке
     * @return элемент из персистентного списка
     */
    public E get(int index) {
        index = getWidthIndex(index);
        return getListNode(index).getValue();
    }

    /**
     * Добавляет элемент в конец персистентного списка.
     * @param value добавляемый элемент
     * @return true, если элемент был добавлен в персистентный список
     */
    public boolean add(E value) {
        setParent(value);
        HeadList<ListNode<E>> head = new HeadList<>(this.bitsPerNode);
        head.clone(getHead());
        checkIfFull(head, 1);
        newVersion(head);
        add(head, value);
        return true;
    }

    /**
     * Добавляет элемент в персистентный список по индексу.
     * @param index индекс в персистентном списке
     * @param value добавляемый элемент
     */
    public void add(int index, E value) {
        HeadList<ListNode<E>> newHead;
        Node<ListNode<E>> node;
        setParent(value);
        HeadList<ListNode<E>> oldHead = getHead();
        checkIfFull();
        checkListIndex(index);
        if (oldHead.getSize() == 0) {
            add(value);
        } else {
            Integer befIndex = null;
            Integer aftIndex = null;
            Integer freeIndex = oldHead.pollEmptyIndex();
            newHead = new HeadList<>(this.bitsPerNode);
            newHead.clone(oldHead);
            if (index != 0) {
                befIndex = getWidthIndex(newHead, index - 1);
            }
            if (index != newHead.getSize()) {
                aftIndex = getWidthIndex(newHead, index);
            }
            if (befIndex != null) {
                node = copyPath(newHead, befIndex);
                ListNode<E> befValue = new ListNode<>();
                befValue.copy((ListNode<E>) node.get()[befIndex & this.mask]);
                befValue.setNext(freeIndex);
                node.set(befIndex & this.mask, befValue);
            } else {
                newHead.setFirst(freeIndex);
            }
            if (aftIndex != null) {
                oldHead = newHead;
                newHead = new HeadList<>(this.bitsPerNode);
                newHead.clone(oldHead);
                node = copyPath(newHead, aftIndex);
                ListNode<E> aftValue = new ListNode<>();
                aftValue.copy((ListNode<E>) node.get()[aftIndex & this.mask]);
                aftValue.setPrev(freeIndex);
                node.set(aftIndex & this.mask, aftValue);
            }
            ListNode<E> listElement = new ListNode<>(value, befIndex, aftIndex);
            node = copyPath(newHead, freeIndex);
            node.set(freeIndex & this.mask, listElement);
            if (freeIndex == newHead.getWidth()) {
                newHead.setSize(newHead.getSize() + 1);
                newHead.setWidth(newHead.getWidth() + 1);
            } else {
                newHead.setSize(newHead.getSize() + 1);
            }
            newVersion(newHead); 
        }
    }

    /**
     * Добавляет все элементы из списка в конец персистентного списка.
     * @param values список, содержащий добавляемые элементы
     * @return true, если все элементы из списка были добавлены в персистентный список
     */
    public boolean addAll(List<E> values) {
        HeadList<ListNode<E>> head = new HeadList<>(this.bitsPerNode);
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
     * Заменяет элемент из персистентного списка на новый.
     * @param index индекс элемента
     * @param value новый элемент
     * @return элемент до замены
     */
    public E set(int index, E value) {
        setParent(value);
        HeadList<ListNode<E>> oldHead = getHead();
        checkIfEmpty(oldHead);
        checkListIndex(oldHead, index);
        HeadList<ListNode<E>> newHead = new HeadList<>(this.bitsPerNode);
        newHead.clone(oldHead);
        newVersion(newHead);
        E prevValue = get(index);
        set(newHead, index, value);
        return prevValue;
    }

    /**
     * Удаляет элемент из персистентного списка по индексу.
     * @param index индекс элемента
     * @return удаленный из персистентного списка элемент
     */
    public E remove(int index) {
        HeadList<ListNode<E>> oldHead = getHead();
        checkListIndex(oldHead, index);
        HeadList<ListNode<E>> newHead;
        Node<ListNode<E>> node;
        E result = get(index);
        if (oldHead.getSize() == 1) {
            clear();
        } else {
            Integer befIndex = null;
            Integer aftIndex = null;
            ListNode<E> befValue;
            ListNode<E> aftValue;
            newHead = new HeadList<>(this.bitsPerNode);
            newHead.clone(oldHead);
            Integer widthIndex = getWidthIndex(newHead, index);
            if (index != 0) {
                befIndex = getWidthIndex(newHead, index - 1);
            }
            if (index != newHead.getSize() - 1) {
                aftIndex = getWidthIndex(newHead, index + 1);
            }
            if (befIndex != null) {
                node = copyPath(newHead, befIndex);
                befValue = new ListNode<>();
                befValue.copy((ListNode<E>) node.get()[befIndex & this.mask]);
                befValue.setNext(aftIndex);
                node.set(befIndex & this.mask, befValue);
            } else {
                newHead.setFirst(aftIndex);
            }
            if (aftIndex != null) {
                oldHead = newHead;
                newHead = new HeadList<>(this.bitsPerNode);
                newHead.clone(oldHead);
                node = copyPath(newHead, aftIndex);
                aftValue = new ListNode<>();
                aftValue.copy((ListNode<E>) node.get()[aftIndex & this.mask]);
                aftValue.setPrev(befIndex);
                node.set(aftIndex & this.mask, aftValue);
            } else {
                newHead.setLast(befIndex);
            }
            node = copyPath(newHead, widthIndex);
            node.set(widthIndex & this.mask, null);
            if (widthIndex != newHead.getWidth()) {
                newHead.addEmptyIndex(widthIndex);
                newHead.setSize(newHead.getSize() - 1);
            } else {
                newHead.setSize(newHead.getSize() - 1);
                newHead.setWidth(newHead.getWidth() - 1);
            }
            newVersion(newHead);
        }
        return result;
    }

    /**
     * Удаляет все элементы из персистентного списка.
     */
    public void clear() {
        HeadList<ListNode<E>> head = new HeadList<>(this.bitsPerNode);
        newVersion(head);
    }
    
    /**
     * Возвращает максимальный размер персистентного списка.
     * @return максимальный размер персистентного списка
     */
    public int maxSize() {
        return this.maxSize;
    }

    /**
     * Возвращает число бит на каждую ноду двоичного дерева персистентного списка.
     * @return число бит на каждую ноду двоичного дерева
     */
    public int getBitsPerNode() {
        return this.bitsPerNode;
    }

    /**
     * Возвращает высоту (глубину) двоичного дерева персистентного списка.
     * @return высота двоичного дерева
     */
    public int getheight() {
        return this.height;
    }

    /**
     * Преобразует персистентный список в список, содержащий элементы в порядке их хранения в двоичном дереве.
     * @return список, содержащий элементы персистентного списка
     */
    public List<Object> toArray() {
        List<Object> values;
        HeadList<ListNode<E>> head = getHead();
        if (head != null) {
            Object[] leafNodeValues;
            values = new ArrayList<>(head.getWidth());
            for (int i = 0; i < head.getWidth(); i = i + this.mask + 1) {
                leafNodeValues = getLeafNodeValues(head, i);
                for (int j = 0; j < this.mask + 1; j++) {
                    if (leafNodeValues[j] == null) {
                        values.add(null);
                    } else {
                        Object value = ((ListNode<E>) leafNodeValues[j]).getValue();
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

    private Node<ListNode<E>> copyPath(HeadList<ListNode<E>> head, int index) {
        Node<E> newNode;
        Node<ListNode<E>> currentNode = head.getRoot();
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
            currentNode = (Node<ListNode<E>>) newNode;
        }
        return currentNode;
    }

    private Integer getWidthIndex(int index) {
        return getWidthIndex(getHead(), index);
    }

    private Integer getWidthIndex(HeadList<ListNode<E>> head, int index) {
        if (head.getSize() == 0) {
            return null;
        }
        Integer currentIndex = head.getFirst();
        for (int i = 0; i < index; i++) {
            currentIndex = getListNode(currentIndex).getNext();
        }
        return currentIndex;
    }

    private void add(HeadList<ListNode<E>> head, E value) {
        ListNode<E> listElement;
        if (isEmpty(head)) {
            head.setFirst(0);
            head.setLast(0);
            listElement = new ListNode<>(value, null, null);
            head.setSize(head.getSize() + 1);
            head.setWidth(head.getWidth() + 1);
            Node<ListNode<E>> node = copyPath(head, head.getLast());
            node.add(listElement);
        } else {
            Integer lastIndex = head.getLast();
            listElement = new ListNode<>(value, lastIndex, null);
            Node<ListNode<E>> node = copyPath(head, lastIndex);
            ListNode<E> last = new ListNode<>();
            last.copy((ListNode<E>) node.get()[lastIndex & this.mask]);
            node.set(lastIndex & this.mask, last);
            Integer newIndex = head.pollEmptyIndex();
            last.setNext(newIndex);
            node = copyPath(head, newIndex);
            head.setLast(newIndex);
            if (newIndex == head.getWidth()) {
                node.add(listElement);
                head.setSize(head.getSize() + 1);
                head.setWidth(head.getWidth() + 1);
            } else {
                node.set(newIndex & this.mask, listElement);
                head.setSize(head.getSize() + 1);
            }
        }
    }

    private void set(HeadList<ListNode<E>> head, int index, E value) {
        Node<ListNode<E>> leafNode = copyPath(head, getWidthIndex(head, index));
        ListNode<E> newNode = new ListNode<>();
        newNode.copy((ListNode<E>) leafNode.get()[index & this.mask]);
        newNode.setValue(value);
        leafNode.set(index & this.mask, newNode);
    }

    private ListNode<E> getListNode(int index) {
        HeadList<ListNode<E>> head = getHead();
        return getListNode(head, index);
    }

    private ListNode<E> getListNode(HeadList<ListNode<E>> head, int index) {
        return (ListNode<E>) getLeafNodeValues(head, index)[index & this.mask];
    }

    private Object[] getLeafNodeValues(HeadList<ListNode<E>> head, int index) {
        return getLeafNode(head, index).get();
    }

    private Node<ListNode<E>> getLeafNode(HeadList<ListNode<E>> head, int index) {
            Node<ListNode<E>> node = head.getRoot();
        for (int level = this.bitsPerNode * (this.height - 1); level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            node = (Node<ListNode<E>>) node.get(id);
        }
        return node;
    }

    private HeadList<ListNode<E>> getHead() {
        return (HeadList<ListNode<E>>) this.versions.getCurrent();
    }

    private boolean isEmpty(HeadList<ListNode<E>> head) {
        return head.getSize() == 0;
    }

    private boolean isFull(HeadList<ListNode<E>> head, int extra) {
        return head.getSize() + extra >= maxSize;
    }


    private void checkIfFull() {
        if (isFull(getHead(), 0)) {
            throw new IllegalStateException("Достигнуто максимальное число элементов в персистентном списке");
        }
    }

    private void checkIfFull(HeadList<ListNode<E>> HeadArray, int delta) {
        if (HeadArray.getSize() + delta > this.maxSize) {
            throw new IllegalStateException("Достигнуто максимальное число элементов в персистентном списке");
        }
    }

    public void checkIfEmpty(HeadList<ListNode<E>> head) {
        if (head.getSize() == 0) {
            throw new IllegalStateException("Персистентный список пуст");
        }
    }

    private void checkListIndex(int index) {
        checkListIndex(getHead(), index);
    }

    private void checkListIndex(HeadList<ListNode<E>> head, int index) {
        if ((index < 0) || (index >= head.getSize())) {
            throw new IndexOutOfBoundsException("Неверный индекс элемента в персистентном массиве");
        }
    }

}