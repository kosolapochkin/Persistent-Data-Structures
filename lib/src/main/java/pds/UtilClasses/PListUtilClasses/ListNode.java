package pds.UtilClasses.PListUtilClasses;

public class ListNode<E> {
    private Integer next;
    private Integer prev;
    private E value;

    public ListNode() {
        this.next = null;
        this.prev = null;
        this.value = null;
    }

    public ListNode(E value, Integer prev, Integer next) {
        this.next = next;
        this.prev = prev;
        this.value = value;
    }

    public void copy(ListNode<E> other) {
        this.next = other.next;
        this.prev = other.prev;
        this.value = other.value;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public Integer getPrev() {
        return prev;
    }

    public void setPrev(Integer prev) {
        this.prev = prev;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }
}