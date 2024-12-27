package pds;

import java.util.ArrayList;
import java.util.List;

import pds.SubClasses.HeadList;
import pds.SubClasses.ListNode;
import pds.SubClasses.Node;
import pds.SubClasses.UndoRedoDataStructure;
import pds.SubClasses.UndoRedoStack;

/**
 * Персистентный двусвязный список
 * @value depth - глубина дерева
 * @value bitsPerNode - число бит на каждую ноду дерева
 */
@SuppressWarnings("unchecked")
public class PDoublyLinkedList<E> implements UndoRedoDataStructure {

    private int depth;
    private int bitsPerNode;
    private int mask;
    private int maxSize;
    private int nodeSize;
    private UndoRedoStack<E> versions;
    private UndoRedoStack<E> changes;
    private PDoublyLinkedList<E> parent;

    public PDoublyLinkedList() {
        this(3, 2);
    }

    public PDoublyLinkedList(int depth, int bitsPerNode) {
        this.depth = depth;
        this.bitsPerNode = bitsPerNode;
        this.maxSize = (int) Math.pow(2, bitsPerNode * depth);
        this.nodeSize = (int) Math.pow(2, bitsPerNode);
        this.mask = this.nodeSize - 1;
        HeadList<E> HeadList = new HeadList<>(this.bitsPerNode);
        this.versions = new UndoRedoStack<>(HeadList);
        this.changes = new UndoRedoStack<>();
    }

    public PDoublyLinkedList(PDoublyLinkedList<E> other) {
        this(other.depth, other.bitsPerNode);
        this.versions.clone(other.versions);
        this.changes.clone(other.changes);
    }

    public void undo() {
        if (!this.changes.getUndo().isEmpty()) {
            Object peek = this.changes.getUndo().peek();
            if (peek == null) {
                this.versions.undo();
            } else {
                ((PDoublyLinkedList<E>) peek).undo();
            }
            this.changes.getRedo().push(this.changes.getUndo().pop());
        }
    }

    public void redo() {
        if (!this.changes.getRedo().isEmpty()) {
            Object peek = this.changes.getRedo().peek();
            if (peek == null) {
                this.versions.redo();
            } else {
                ((PDoublyLinkedList<E>) peek).redo();
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

    public boolean add(E value) {
        setParent(value);
        HeadList<ListNode<E>> head = new HeadList<>(this.bitsPerNode);
        head.clone(getHead());
        checkIfFull(head, 1);
        newVersion(head);
        add(head, value);
        return true;
    }

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

    public void clear() {
        HeadList<ListNode<E>> head = new HeadList<>(this.bitsPerNode);
        newVersion(head);
    }

    public E get(int index) {
        index = getWidthIndex(index);
        return getListNode(index).getValue();
    }

    public boolean isEmpty() {
        return isEmpty(getHead());
    }

    private boolean isFull(HeadList<ListNode<E>> head, int extra) {
        return head.getSize() + extra >= maxSize;
    }

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

    public E set(int index, E value) {
        setParent(value);
        HeadList<ListNode<E>> oldHead = getHead();
        checkIfEmpty(oldHead);
        checkWidthIndex(oldHead, index);
        HeadList<ListNode<E>> newHead = new HeadList<>(this.bitsPerNode);
        newHead.clone(oldHead);
        newVersion(newHead);
        E prevValue = get(index);
        set(newHead, index, value);
        return prevValue;
    }

    public int size() {
        return getHead().getSize();
    }
    
    public int maxSize() {
        return this.maxSize;
    }

    public int getBitsPerNode() {
        return this.bitsPerNode;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getNodeSize() {
        return this.nodeSize;
    }

    public List<E> toList() {
        List<E> values;
        HeadList<ListNode<E>> head = getHead();
        if (head != null) {
            values = new ArrayList<>(head.getSize());
            Integer index = head.getFirst();
            for (int i = 0; i < head.getSize(); i++) {
                ListNode<E> listElement = getListNode(index);
                Object value = listElement.getValue();
                if (value instanceof PDoublyLinkedList) {
                    value = ((PDoublyLinkedList<E>) value).toList();
                }
                values.add((E) value);
                index = listElement.getNext();
            }
        } else {
            values = new ArrayList<>(0);
        }
        return values;  
    }

    public List<E> toArray() {
        List<E> values;
        HeadList<ListNode<E>> head = getHead();
        if (head != null) {
            Object[] leafNodeValues;
            values = new ArrayList<>(head.getWidth());
            for (int i = 0; i < head.getWidth(); i = i + this.nodeSize) {
                leafNodeValues = getLeafNodeValues(head, i);
                for (int j = 0; j < this.nodeSize; j++) {
                    if (leafNodeValues[j] == null) {
                        values.add(null);
                    } else {
                        Object value = ((ListNode<E>) leafNodeValues[j]).getValue();
                        if (value instanceof PDoublyLinkedList) {
                            value = ((PDoublyLinkedList<E>) value).toList();
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

    public String toString() {
        return this.toList().toString();
    }

    private void newVersion(Object newHead) {
        if (this.parent != null) {
            parent.changes.getUndo().push(this);
        }
        this.changes.getUndo().push(null);
        this.changes.getRedo().clear();
        this.versions.newVersion(newHead);
    }

    private void setParent(Object object) {
        if (isPersistent(object)) {
            ((PDoublyLinkedList<E>) object).parent = this;
        }
    }

    private boolean isPersistent(Object object) {
        if (object instanceof PDoublyLinkedList) {
            return true;
        }
        return false;
    }

    private Node<ListNode<E>> copyPath(HeadList<ListNode<E>> head, int index) {
        Node<E> newNode;
        Node<ListNode<E>> currentNode = head.getRoot();
        for (int level = (this.depth - 1) * this.bitsPerNode; level > 0; level -= this.bitsPerNode) {
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
            checkWidthIndex(head, index);
            Node<ListNode<E>> node = head.getRoot();
        for (int level = this.bitsPerNode * (this.depth - 1); level > 0; level -= this.bitsPerNode) {
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

    private void checkIfFull() {
        if (isFull(getHead(), 0)) {
            throw new IllegalStateException("List is full");
        }
    }

    private void checkIfFull(HeadList<ListNode<E>> HeadArray, int delta) {
        if (HeadArray.getSize() + delta > this.maxSize) {
            throw new IllegalStateException("List is full");
        }
    }

    public void checkIfEmpty(HeadList<ListNode<E>> head) {
        if (head.getSize() == 0) {
            throw new IllegalStateException("List is empty");
        }
    }

    private void checkWidthIndex(HeadList<ListNode<E>> head, int index) {
        if ((index < 0) || (index >= head.getWidth())) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
    }

    private void checkListIndex(int index) {
        checkWidthIndex(getHead(), index);
    }

    private void checkListIndex(HeadList<ListNode<E>> head, int index) {
        if ((index < 0) || (index >= head.getSize())) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
    }

}