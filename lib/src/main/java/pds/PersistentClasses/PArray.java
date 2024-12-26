package pds.PersistentClasses;

import java.util.ArrayList;
import java.util.List;

import pds.UtilClasses.VersionStack;
import pds.UtilClasses.Head;
import pds.UtilClasses.Node;
import pds.UtilClasses.UndoRedoDataStructure;

/**
 * Персистентный массив
 */
@SuppressWarnings("unchecked")
public class PArray<E> implements UndoRedoDataStructure {

    private int depth;
    private int bitsPerNode;
    private int mask;
    private int maxSize;
    private int nodeSize;
    private VersionStack<E> thisVersionStack;
    private VersionStack<E> nestedVersionStack;

    private PArray<E> parent;

    public PArray() {
        this(3, 2);
    }

    public PArray(int depth, int bitsPerNode) {
        this.depth = depth;
        this.bitsPerNode = bitsPerNode;
        this.maxSize = (int) Math.pow(2, bitsPerNode * depth);
        this.nodeSize = (int) Math.pow(2, bitsPerNode);
        this.mask = this.nodeSize - 1;
        Head<E> head = new Head<>(this.bitsPerNode);
        this.thisVersionStack = new VersionStack<>(head);
        this.nestedVersionStack = new VersionStack<>();
    }

    public PArray(PArray<E> other) {
        this(other.depth, other.bitsPerNode);
        this.thisVersionStack.copy(other.thisVersionStack);
        this.nestedVersionStack.copy(other.nestedVersionStack);
    }

    public void newVersion(Object head) {
        if (this.parent != null) {
            parent.nestedVersionStack.getUndo().push(this);
        }
        this.nestedVersionStack.getUndo().push(null);
        this.nestedVersionStack.getRedo().clear();
        this.thisVersionStack.newVersion(head);
    }

    /**
     * Возвращает номер текущей версии массива
     * 
     * @return версия массива
     */
    public int getCurrentVersion() {
        return this.thisVersionStack.getCurrentVersion();
    }

    /**
     * Возвращает число версий массива
     * 
     * @return число версий массива
     */
    public int getVersionCount() {
        return this.thisVersionStack.getVersionCount();
    }

    /**
     * Совершает возврат к предыдущей версии массива
     */
    public void undo() {
        if (!this.nestedVersionStack.getUndo().isEmpty()) {
            Object peek = this.nestedVersionStack.getUndo().peek();
            if (peek == null) {
                this.thisVersionStack.undo();
            } else {
                ((PArray<E>) peek).undo();
            }
            this.nestedVersionStack.getRedo().push(this.nestedVersionStack.getUndo().pop());
        }
    }

    /**
     * Совершает переход к следующей версии массива
     */
    public void redo() {
        if (!this.nestedVersionStack.getRedo().isEmpty()) {
            Object peek = this.nestedVersionStack.getRedo().peek();
            if (peek == null) {
                this.thisVersionStack.redo();
            } else {
                ((PArray<E>) peek).redo();
            }
            this.nestedVersionStack.getUndo().push(this.nestedVersionStack.getRedo().pop());
        }
    }

    public E get(int index) {
        Head<E> head = getHead();
        return get(head, index);
    }

    public void add(E value) {
        setParent(value);
        Head<E> head = new Head<>(this.bitsPerNode);
        head.copy(getHead());
        checkIfFull(head);
        newVersion(head);
        add(head, value);
    }

    public void add(int index, E value) {
        setParent(value);
        Head<E> oldHead = getHead();
        checkIfFull(oldHead);
        checkIndex(oldHead, index);
        Node<E> leafNode = copyPathForIncrement(oldHead, index);
        leafNode.set(index & this.mask, value);
        Head<E> newHead = getHead();
        for (int i = index; i < oldHead.size(); i++) {
            add(newHead, (E) get(oldHead, i));
        }
    }

    public void addAll(List<E> values) {
        Head<E> head = new Head<>(this.bitsPerNode);
        checkIfFull(head, values.size());
        head.copy(getHead());
        newVersion(head);
        for (int i = 0; i < values.size(); i++) {
            E value = values.get(i);
            setParent(value);
            add(head, value);
        }
    }

    public void set(int index, E value) {
        setParent(value);
        Head<E> oldHead = getHead();
        checkIfEmpty(oldHead);
        checkIndex(oldHead, index);
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.copy(oldHead);
        newVersion(newHead);
        set(newHead, index, value);
    }

    
    public E pop() {
        Head<E> oldHead = getHead();
        checkIfEmpty(oldHead);
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.copy(oldHead);
        newHead.setSize(newHead.size() - 1);
        newVersion(newHead);
        Node<E> node = copyPath(newHead, newHead.size());
        E value = (E) node.pop();
        clearPath(newHead);
        return value;
    }

    public E remove(int index) {
        Head<E> oldHead = getHead();
        checkIndex(oldHead, index);
        checkIfEmpty(oldHead);
        Node<E> leafNode = copyPathForIncrement(oldHead, index);
        E value = (E) leafNode.pop();
        Head<E>newHead = getHead();
        newHead.setSize(newHead.size() - 1);
        for (int i = index + 1; i < oldHead.size(); i++) {
            add(newHead, (E) get(oldHead, i));
        }
        return value;
    }

    public void clear() {
        Head<E> head = new Head<>(this.bitsPerNode);
        newVersion(head);
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
        for (int level = this.bitsPerNode * (this.depth - 1); level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            node = (Node<E>) node.get(id);
        }
        return node;
    }

    private Node<E> copyPath(Head<E> Head, int index) {
        Node<E> currentNode;
        Node<E> newNode;
        currentNode = Head.getRoot();
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

                    newNode.copy((Node<E>) currentNode.get(id));
                    currentNode.set(id, newNode);
                }
            }
            currentNode = newNode;
        }
        return currentNode;
    }

    private Node<E> copyPathForIncrement(Head<E> Head, int index) {
        int level = this.bitsPerNode * (depth - 1);
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.partCopy(Head, (index >> level) & this.mask);
        newHead.setSize(index + 1);
        newVersion(newHead);
        Node<E> currentNode = newHead.getRoot();
        for (; level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            int idNext = (index >> (level - this.bitsPerNode)) & this.mask;
            Node<E> newNode = new Node<>(this.bitsPerNode);
            newNode.partCopy((Node<E>) currentNode.get(id), idNext);
            currentNode.set(id, newNode);
            currentNode = newNode;
        }
        return currentNode;
    }

    private void clearPath(Head<E> Head) {
        Node<E> node;
        int index = Head.size();
        List<Node<E>> pathNodes = new ArrayList<>();
        node = Head.getRoot();
        pathNodes.add(node);
        for (int level = this.bitsPerNode * (this.depth - 1); level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & mask;
            node = (Node<E>) node.get(id);
            pathNodes.add(node);
        }
        for (int i = pathNodes.size() - 1; i > 0; --i) {
            if ((pathNodes.get(i) == null) || (pathNodes.get(i).getCount() == 0)) {
                pathNodes.get(i-1).pop();
            }
        }
    }

    public int size() {
        return getHead().size();
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

    private Head<E> getHead() {
        return (Head<E>) this.thisVersionStack.getCurrent();
    }

    public void setParent(Object object) {
        if (isPersistent(object)) {
            ((PArray<E>) object).parent = this;
        }
    }

    private boolean isPersistent(Object object) {
        if (object instanceof PArray) {
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return isFull(getHead());
    }

    private boolean isFull(Head<E> head) {
        return head.size() == this.maxSize;
    }

    private void checkIfFull(Head<E> head) {
        if (isFull(head)) {
            throw new IllegalStateException("Array is full");
        }
    }

    private void checkIfFull(Head<E> head, int delta) {
        if (head.size() + delta > this.maxSize) {
            throw new IllegalStateException("Array is full");
        }
    }
    
    public void checkIfEmpty(Head<E> head) {
        if (head.size() == 0) {
            throw new IllegalStateException("Array is empty");
        }
    }

    private void checkIndex(Head<E> head, int index) {
        if ((index < 0) || (index >= head.size())) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
    }

    public List<E> toList() {
        List<E> values;
        Head<E> Head = getHead();
        if (Head != null) {
            Object[] leafNodeValues;
            values = new ArrayList<>(Head.size());
            for (int i = 0; i < Head.size(); i = i + this.nodeSize) {
                leafNodeValues = getLeafNodeValues(Head, i);
                for (int j = 0; j < this.nodeSize; j++) {
                    if (leafNodeValues[j] != null) {
                        Object value = leafNodeValues[j];
                        if (value instanceof PArray) {
                            value = ((PArray<E>) value).toList();
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

}