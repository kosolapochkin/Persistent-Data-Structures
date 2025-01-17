package pds.SubClasses.UndoRedoClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Структура данных с поддержкой механизма undo-redo.
 */
public class UndoRedoDataStructure {

    /* UndoRedoStack, содержащий версии данной структуры данных */
    protected UndoRedoStack<?> versions;
    /* UndoRedoStack, содержащий историю изменений данной структуры данных, а также вложенных структур данных */
    protected UndoRedoStack<?> changes;
    /* "Родитель" данной структуры данных (если является вложенной в родительскую структуру данных) */
    protected UndoRedoDataStructure parent;
    
    /**
     * Возвращает к предыдущей версии структуры данных.
     */
    public void undo() {
        if (!this.changes.getUndo().isEmpty()) {
            Object peek = this.changes.getUndo().peek();
            if (peek == null) {
                this.versions.undo();
            } else {
                ((UndoRedoDataStructure) peek).undo();
            }
            this.changes.getRedo().push(this.changes.getUndo().pop());
        }
    }
    
    /**
     * Возвращает к версии структуры данных до выполнения {@link #undo() undo}.
     */
    public void redo() {
        if (!this.changes.getRedo().isEmpty()) {
            Object peek = this.changes.getRedo().peek();
            if (peek == null) {
                this.versions.redo();
            } else {
                ((UndoRedoDataStructure) peek).redo();
            }
            this.changes.getUndo().push(this.changes.getRedo().pop());
        }
    }

    /**
     * Преобразует структуру данных в строковое представление.
     * @return строковое представление структуры данных
     */
    public String toString() {
        return toList().toString();
    }
    
    /**
     * Возвращает номер текущей версии структуры данных.
     * @return номер текущей версии
     */
    public int currentVersion() {
        return this.versions.currentVersion();
    }

    /**
     * Возвращает количество версий структуры данных.
     * @return количество версий структуры данных
     */
    public int countVersions() {
        return this.versions.countVersions();
    }

    public List<Object> toList() {
        return new ArrayList<>();
    }

    protected void newVersion(Object head) {
        if (this.parent != null) {
            parent.changes.getUndo().push(this);
        }
        this.changes.getUndo().push(null);
        this.changes.getRedo().clear();
        this.versions.newVersion(head);
    }

    protected void setParent(Object object) {
        if (isPersistent(object)) {
            ((UndoRedoDataStructure) object).parent = this;
        }
    }

    private boolean isPersistent(Object object) {
        if (object instanceof UndoRedoDataStructure) {
            return true;
        }
        return false;
    }

}