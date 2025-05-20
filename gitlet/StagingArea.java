package gitlet;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * stuff.
 * @author Brendon Lin
 */
public class StagingArea implements Serializable {
    /**
     * stuff.
     */
    private LinkedHashMap<String, String> toAdd;
    /**
     * stuff.
     */
    private LinkedHashMap<String, String> toDelete;

    /**
     * staging area.
     */
    public StagingArea() {
        toAdd = new LinkedHashMap<String, String>();
        toDelete = new LinkedHashMap<String, String>();
    }

    /**
     * staging area.
     * @return stuff
     */
    public LinkedHashMap<String, String> getAddingArea() {
        return toAdd;
    }

    /**
     * staging area.
     * @return stuff
     */
    public LinkedHashMap<String, String> getRemovingArea() {
        return toDelete;
    }

    /**
     * staging area.
     * @return stuff
     */
    public boolean checkEmpty() {
        return toAdd.isEmpty() && toDelete.isEmpty();
    }

    /**
     * staging area.
     */
    public void clear() {
        toAdd.clear();
        toDelete.clear();
    }

    /**
     * staging area.
     * @param fileName filename.
     * @param hash hash.
     */
    public void putAdd(String fileName, String hash) {
        toAdd.put(fileName, hash);
    }

    /**
     * staging area.
     * @param fileName filename.
     * @param hash hash.
     *
     */
    public void putDelete(String fileName, String hash) {
        toDelete.put(fileName, hash);
    }

    /**
     * staging area.
     * @param fileName fileName.
     * @return stuff
     */
    public boolean exists(String fileName) {
        return toAdd.containsKey(fileName);
    }

    /**
     * staging area.
     * @param key key.
     * @return stuff
     */
    public String getFromAdd(String key) {
        return toAdd.get(key);
    }

    /**
     * staging area.
     * @param key key.
     */
    public void removeFromAdd(String key) {
        toAdd.remove(key);
    }

    /**
     * staging area.
     * @param key key.
     */
    public void removeFromDelete(String key) {
        toDelete.remove(key);
    }




}
