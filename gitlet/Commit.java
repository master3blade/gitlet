package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * stuff.
 * @author Brendon Lin
 */
public class Commit implements Serializable {

    /**
     * stuff.
     */
    private String message;
    /**
     * stuff.
     */
    private String pattern = "EEE MMM d HH:mm:ss yyyy Z";
    /**
     * stuff.
     */
    private Commit parent;
    /**
     * stuff.
     */
    private Commit mergeParent;
    /**
     * stuff.
     */
    private LinkedHashMap<String, String> allCurrentTracked;
    /**
     * stuff.
     */
    private String currentDate;

    /**
     *
     * @param message1 the message
     * @param parent1 the parent
     */
    public Commit(String message1, Commit parent1) {
        this.message = message1;
        SimpleDateFormat formattedDate = new SimpleDateFormat(pattern);
        Date currentDate1 = new Date();
        this.currentDate = formattedDate.format(currentDate1);
        this.parent = parent1;
        this.allCurrentTracked = new LinkedHashMap<String, String>();
    }

    /**
     * stuff.
     * @return stuff
     */
    public LinkedHashMap<String, String> getAllCurrentTracked() {
        return allCurrentTracked;
    }

    /**
     * stuff.
     * @param original tocopy
     */
    public void newMap(LinkedHashMap<String, String> original) {
        LinkedHashMap<String, String> originalDeepCopy = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : original.entrySet()) {
            originalDeepCopy.put(entry.getKey(), entry.getValue());
        }
        this.allCurrentTracked = originalDeepCopy;
    }

    /**
     * stuff.
     * @param original to copy
     */
    public void addToTracked(LinkedHashMap<String, String> original) {
        for (Map.Entry<String, String> entry : original.entrySet()) {
            this.allCurrentTracked.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * stuff.
     * @param fileName filename
     */
    public void removeFromTracked(String fileName) {
        allCurrentTracked.remove(fileName);
    }

    /**
     * stuff.
     * @param fileName bloblash
     * @param blobHash blobhash
     */
    public void addOneToTracked(String fileName, String blobHash) {
        this.allCurrentTracked.put(fileName, blobHash);
    }

    /**
     * stuff.
     *
     */
    public void setDate() {
        SimpleDateFormat formattedDate = new SimpleDateFormat(pattern);
        Date currentDate1 = new Date();
        this.currentDate = formattedDate.format(currentDate1);
    }

    /**
     * stuff.
     * @param newParent new
     */
    public void setParent(Commit newParent) {
        parent = newParent;
    }

    /**
     * stuff.
     * @param newMergeParent new parent
     */
    public void setMergeParent(Commit newMergeParent) {
        mergeParent = newMergeParent;
    }

    /**
     * stuff.
     * @return stuff
     */
    public String getDate() {
        return currentDate;
    }

    /**
     * stuff.
     *@return stuff
     */
    public Commit getParent() {
        return parent;
    }

    /**
     * stuff.
     *@return stuff
     */
    public Commit getMergeParent() {
        return mergeParent;
    }

    /**
     * stuff.
     *@return stuff
     */
    public String getMessage() {
        return message;
    }

    /**
     * stuff.
     * @param key key
     * @return stuff
     */
    public String lookForFile(String key) {
        return allCurrentTracked.get(key);
    }

    /**
     * stuff.
     * @param fileName filename
     * @return stuff
     */
    public boolean exists(String fileName) {
        if (allCurrentTracked == null) {
            return false;
        }
        return allCurrentTracked.containsKey(fileName);
    }

    /**
     * stuff.
     * @param fileName filename
     * @return stuff
     */
    public String getHashFromFile(String fileName) {
        return allCurrentTracked.get(fileName);
    }

    /**
     * stuff.
     *
     */
    public void dateSwap() {
        currentDate = "Thu Jan 1 00:00:00 1970 -0800";
    }


}
