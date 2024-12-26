package pds.UtilClasses;

public interface UndoRedoDataStructure {

    void newVersion(Object head);
    void undo();
    void redo();
    void setParent(Object object);
    int getCurrentVersion();
    int getVersionCount();

}