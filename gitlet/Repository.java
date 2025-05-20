package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * the beginning.
 * @author Brendon Lin
 */
public class Repository {
    /**
     * stuff.
     */
    private String hEAD;
    /**
     * stuff.
     */
    private String master;
    /**
     * stuff.
     */
    private String currentBranch;
    /**
     * stuff.
     */
    private String currentWorkingDirectory = System.getProperty("user.dir");
    /**
     * stuff.
     */
    private StagingArea stagingArea;

    /**
     * nothing happens when repository make.
     */
    public Repository() {

    }

    /**
     * make gitlet stuff.
     */
    public void init() {
        File newRepo = Utils.join(currentWorkingDirectory, ".gitlet");
        if (!newRepo.mkdir()) {
            System.out.println("Gitlet version-control system "
                    +
                    "already exists in the current directory.");
            System.exit(0);
        }
        Commit initialCommit = new Commit("initial commit", null);
        initialCommit.dateSwap();
        byte[] serializedStuff = Utils.serialize(initialCommit);
        hEAD = Utils.sha1(serializedStuff);
        master = Utils.sha1(serializedStuff);
        currentBranch = "master";
        Utils.writeObject(Utils.join(newRepo, "HEAD"), hEAD);
        File newBlobs = Utils.join(newRepo, "blobs");
        newBlobs.mkdir();
        File commits = Utils.join(newRepo, "commits");
        commits.mkdir();
        File branches = Utils.join(newRepo, "branches");
        branches.mkdir();
        Utils.writeObject(Utils.join(branches, "master"), master);
        Utils.writeObject(Utils.join(commits, Utils.sha1(serializedStuff)),
                initialCommit);
        Utils.writeObject(Utils.join(".gitlet", "currentBranch"),
                currentBranch);
        stagingArea = new StagingArea();
        Utils.writeObject(Utils.join(newRepo, "StagingArea"), stagingArea);

    }
    /**
     * make gitlet stuff.
     * @param fileName stuff.
     */
    public void add(String fileName) {
        if (Utils.join(currentWorkingDirectory, fileName).exists()) {
            byte[] toAdd = Utils.serialize(Utils.readContents(
                    Utils.join(currentWorkingDirectory, fileName)));
            String addSHA = Utils.sha1(toAdd);
            stagingArea = Utils.readObject(
                    Utils.join(".gitlet", "StagingArea"), StagingArea.class);
            hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"), String.class);
            Commit currentCommit = Utils.readObject(
                    Utils.join(".gitlet/commits", hEAD), Commit.class);
            if (currentCommit.exists(fileName)) {
                if (addSHA.equals(currentCommit.getHashFromFile(fileName))) {
                    stagingArea.removeFromAdd(fileName);
                    stagingArea.removeFromDelete(fileName);
                    Utils.writeObject(Utils.join(".gitlet", "StagingArea"),
                            stagingArea);
                    return;
                }
            }
            File newBlob = Utils.join(".gitlet/blobs", addSHA);
            Utils.writeContents(newBlob,
                    Utils.readContentsAsString(Utils.join(
                            currentWorkingDirectory,
                            fileName)));
            stagingArea.putAdd(fileName, addSHA);
            Utils.writeObject(Utils.join(".gitlet", "StagingArea"),
                    stagingArea);
            return;
        }
        System.out.println("File does not exist.");
        System.exit(0);
    }

    /**
     * make gitlet stuff.
     * @param message message.
     */
    public void commit(String message) {
        if (message.length() < 1) {
            System.out.println("Please enter a commit message");
            System.exit(0);
        }
        stagingArea = Utils.readObject(
                Utils.join(".gitlet", "StagingArea"),
                StagingArea.class);
        if (stagingArea.checkEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"),
                String.class);
        Commit parentCommit = Utils.readObject(Utils.join(
                ".gitlet/commits", hEAD), Commit.class);
        Commit newCommit = new Commit(message, parentCommit);
        newCommit.setDate();
        if (parentCommit.getParent() != null) {
            newCommit.newMap(parentCommit.getAllCurrentTracked());
        } else {
            newCommit.newMap(new LinkedHashMap<String, String>());
        }
        newCommit.addToTracked(stagingArea.getAddingArea());
        byte[] serializedNewCommit = Utils.serialize(newCommit);
        List<String> branchesList = Utils.plainFilenamesIn(
                Utils.join(".gitlet", "branches"));
        currentBranch = Utils.readObject(Utils.join(
                ".gitlet", "currentBranch"), String.class);
        String currentBranchHash = Utils.readContentsAsString(
                Utils.join(".gitlet/branches", currentBranch));

        if (branchesList != null) {
            for (String branchName : branchesList) {
                if (branchName.equals(currentBranch)) {
                    hEAD = Utils.sha1(serializedNewCommit);
                    Utils.writeObject(Utils.join(
                            ".gitlet/branches", branchName), hEAD);
                    stagingArea.clear();
                    Utils.writeObject(Utils.join(
                            ".gitlet", "HEAD"), hEAD);
                    Utils.writeObject(Utils.join(
                            ".gitlet/commits", hEAD), newCommit);
                    Utils.writeObject(Utils.join(
                            ".gitlet", "StagingArea"), stagingArea);
                    return;
                }
            }
        }
        hEAD = Utils.sha1(serializedNewCommit);
        master = Utils.sha1(serializedNewCommit);
        stagingArea.clear();
        Utils.writeObject(Utils.join(".gitlet", "StagingArea"),
                stagingArea);
        Utils.writeObject(Utils.join(".gitlet/commits", hEAD), newCommit);
        Utils.writeObject(Utils.join(".gitlet", "HEAD"), hEAD);
        Utils.writeObject(Utils.join(".gitlet", "master"), master);
    }

    /**
     * make gitlet stuff.
     * @param args arguments.
     */
    public void checkout(String[] args) {
        if (args.length == 3) {
            hEAD = Utils.readObject
                    (Utils.join(".gitlet/HEAD"), String.class);
            Commit headCommit = Utils.readObject(
                    Utils.join(".gitlet/commits", hEAD), Commit.class);
            String fileName = args[2];
            String headBlobHash = headCommit.lookForFile(fileName);
            if (headBlobHash == null) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            String headBlobContent = Utils.readContentsAsString(
                    Utils.join(".gitlet/blobs", headBlobHash));
            Utils.writeContents(Utils.join(currentWorkingDirectory, fileName),
                    headBlobContent);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            if (!Utils.join(".gitlet/commits", args[1]).exists()) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            Commit lookingCommit = Utils.readObject(
                    Utils.join(".gitlet/commits", args[1]), Commit.class);
            String fileName = args[3];
            String lookingBlobHash = lookingCommit.lookForFile(fileName);
            if (!lookingCommit.exists(fileName)) {
                System.out.println("File does not exist in that commit");
                System.exit(0);
            }
            String lookingBlobContent = Utils.readContentsAsString(
                    Utils.join(".gitlet/blobs", lookingBlobHash));
            Utils.writeContents(Utils.join(currentWorkingDirectory, fileName),
                    lookingBlobContent);

        } else if (args.length == 2) {
            checkout3(args);

        } else {
            System.out.println("wrong number of arguments.");
            System.exit(0);
        }
    }

    /**
     * stuff.
     * @param branchName stuff.
     */
    public void checkout3check(String branchName) {
        currentBranch = Utils.readObject(
                Utils.join(".gitlet", "currentBranch"), String.class);
        if (currentBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        if (!Utils.join(".gitlet/branches", branchName).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
    }

    /**
     *
     * @param args args
     */
    public void checkout3(String[] args) {
        String branchName = args[1];
        checkout3check(branchName);
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"), String.class);
        Commit headCommit = Utils.readObject(
                Utils.join(".gitlet/commits", hEAD), Commit.class);

        List<String> cwdFiles = Utils.plainFilenamesIn(currentWorkingDirectory);

        for (String fileName: cwdFiles) {
            if (fileName.contains(".txt")) {
                File currentFile = Utils.join(currentWorkingDirectory,
                        fileName);
                Utils.restrictedDelete(currentFile);
            }
        }
        String branchHash = Utils.readObject(
                Utils.join(".gitlet/branches", branchName),
                String.class);
        Commit branchCommit = Utils.readObject(
                Utils.join(".gitlet/commits", branchHash),
                Commit.class);
        for (String file: cwdFiles) {
            if (!headCommit.exists(file)) {
                System.out.println("There is an untracked file "
                        +
                        "in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (Map.Entry<String, String> entry
                :
                branchCommit.getAllCurrentTracked().entrySet()) {
            String blobHash = entry.getValue();
            String blobContent = Utils.readContentsAsString(
                    Utils.join(".gitlet/blobs", blobHash));
            String fileName = entry.getKey();
            Utils.writeContents(Utils.join(currentWorkingDirectory, fileName),
                    blobContent);
        }
        stagingArea = Utils.readObject(
                Utils.join(".gitlet", "StagingArea"),
                StagingArea.class);
        if (!branchHash.equals(hEAD)) {
            stagingArea.clear();
        }
        byte[] serializedStuff = Utils.serialize(branchCommit);
        String currentBranchLiteral = Utils.sha1(serializedStuff);

        Utils.writeObject(Utils.join(".gitlet", "HEAD"),
                currentBranchLiteral);
        Utils.writeObject(Utils.join(".gitlet", "currentBranch"),
                branchName);
    }

    /**
     * make gitlet stuff.
     */
    public void status() {
        StringBuilder result = new StringBuilder();
        result.append("=== Branches ===\n");
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"),
                String.class);
        List<String> allBranches = Utils.plainFilenamesIn(
                Utils.join(".gitlet/branches"));
        if (allBranches == null) {
            System.exit(0);
        }
        List<String> needSorting = new ArrayList<>(allBranches);
        Collections.sort(needSorting);
        currentBranch = Utils.readObject(
                Utils.join(".gitlet", "currentBranch"),
                String.class);
        for (String sorted: needSorting) {
            if (sorted.equals(currentBranch)) {
                result.append("*");
                result.append(sorted);
                result.append("\n");
                continue;
            }
            result.append(sorted);
            result.append("\n");
        }
        result.append("\n");
        stagingArea = Utils.readObject(Utils.join(
                ".gitlet", "StagingArea"),
                StagingArea.class);
        needSorting = new ArrayList<>();
        result.append("=== Staged Files ===\n");
        LinkedHashMap<String, String> trackedStuff =
                stagingArea.getAddingArea();
        for (Map.Entry<String, String> entry : trackedStuff.entrySet()) {
            needSorting.add(entry.getKey());
        }
        Collections.sort(needSorting);
        for (String sorted: needSorting) {
            result.append(sorted);
            result.append("\n");
        }
        result.append("\n");
        needSorting = new ArrayList<>();
        result.append("=== Removed Files ===\n");
        LinkedHashMap<String, String> removedStuff =
                stagingArea.getRemovingArea();
        for (Map.Entry<String, String> entry : removedStuff.entrySet()) {
            needSorting.add(entry.getKey());
        }
        Collections.sort(needSorting);
        for (String sorted: needSorting) {
            result.append(sorted);
            result.append("\n");
        }
        result.append("\n");
        result.append("=== Modifications Not Staged For Commit ===\n\n");
        result.append("=== Untracked Files ===\n");
        System.out.println(result.toString());
    }

    /**
     * make gitlet stuff.
     */
    public void log() {
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"), String.class);
        Commit iter = Utils.readObject(
                Utils.join(".gitlet/commits", hEAD), Commit.class);
        while (iter.getParent() != null) {
            String theStart = "===\ncommit "
                    +
                    hEAD
                    +
                    "\n"
                    +
                    "Date: "
                    +
                    iter.getDate()
                    +
                    "\n"
                    +
                    iter.getMessage()
                    +
                    "\n";
            System.out.println(theStart);
            iter = iter.getParent();
            byte[] serializeFirst = Utils.serialize(iter);
            hEAD = Utils.sha1(serializeFirst);
        }
        String theStart = "===\ncommit "
                +
                hEAD
                +
                "\n"
                +
                "Date: "
                +
                iter.getDate()
                +
                "\n"
                +
                iter.getMessage()
                +
                "\n";
        System.out.println(theStart);

    }

    /**
     * make gitlet stuff.
     */
    public void globallog() {
        List<String> allCommits =
                Utils.plainFilenamesIn(".gitlet/commits");
        for (String commitName: allCommits) {
            Commit iter = Utils.readObject(
                    Utils.join(".gitlet/commits",
                            commitName), Commit.class);
            String theStart = "===\ncommit "
                    +
                    commitName
                    +
                    "\n"
                    +
                    "Date: "
                    +
                    iter.getDate()
                    +
                    "\n"
                    +
                    iter.getMessage()
                    +
                    "\n";
            System.out.println(theStart);
        }

    }

    /**
     * make gitlet stuff.
     * @param fileName filename.
     */
    public void rm(String fileName) {
        stagingArea = Utils.readObject(
                Utils.join(".gitlet", "StagingArea"),
                StagingArea.class);
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"),
                String.class);
        Commit headCommit = Utils.readObject(
                Utils.join(".gitlet/commits", hEAD),
                Commit.class);
        if (!stagingArea.exists(fileName)
                &&
                headCommit.lookForFile(fileName) == null) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (stagingArea.exists(fileName)) {
            stagingArea.removeFromAdd(fileName);
        }
        if (headCommit.lookForFile(fileName) != null) {
            stagingArea.putDelete(fileName,
                    headCommit.lookForFile(fileName));
            if (Utils.join(currentWorkingDirectory, fileName).exists()) {
                Utils.join(currentWorkingDirectory, fileName).delete();
            }
        }
        Utils.writeObject(
                Utils.join(".gitlet", "stagingArea"), stagingArea);
    }

    /**
     * make gitlet stuff.
     * @param message message.
     */
    public void find(String message) {
        List<String> allCommits = Utils.plainFilenamesIn(".gitlet/commits");
        boolean foundSomething = false;
        for (String commit : allCommits) {
            Commit iter = Utils.readObject(
                    Utils.join(".gitlet/commits", commit), Commit.class);
            if (iter.getMessage().equals(message)) {
                System.out.println(commit);
                foundSomething = true;
            }
        }
        if (!foundSomething) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * make gitlet stuff.
     * @param branchName branch.
     */
    public void branch(String branchName) {
        if (Utils.join(".gitlet/branches", branchName).exists()) {
            System.out.println("A branch with that name already exists");
            System.exit(0);
        }
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"), String.class);
        Utils.writeObject(Utils.join(".gitlet/branches", branchName), hEAD);
    }

    /**
     * make gitlet stuff.
     * @param branchName branchName.
     */
    public void merge(String branchName) {
        stagingArea = Utils.readObject(
                Utils.join(".gitlet",
                        "StagingArea"), StagingArea.class);
        if (!stagingArea.checkEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        } else if (!Utils.join(
                ".gitlet/branches", branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        hEAD = Utils.readObject(
                Utils.join(".gitlet/HEAD"), String.class);
        Commit headCommit = Utils.readObject
                (Utils.join(".gitlet/commits", hEAD), Commit.class);
        String branchHash = Utils.readContentsAsString(
                Utils.join(".gitlet/branches", branchName));
        Commit branchCommit = Utils.readObject(
                Utils.join(".gitlet/commits", branchHash), Commit.class);
        Commit splitPoint = null;

        Set<Commit> allBranchCommits = new HashSet<Commit>();

        while (branchCommit.getParent() != null) {
            allBranchCommits.add(branchCommit);
            branchCommit = branchCommit.getParent();
        }

        Queue<Commit> workQueue = new LinkedList<Commit>();
        workQueue.add(headCommit);
        while (!workQueue.isEmpty()) {
            Commit iter = workQueue.peek();
            if (allBranchCommits.contains(iter)) {
                splitPoint = iter;
                break;
            }
            workQueue.remove();
            workQueue.add(iter.getParent());
            if (iter.getMergeParent() != null) {
                workQueue.add(iter.getMergeParent());
            }
        }


    }

    /**
     * make gitlet stuff.
     * @param splitPoint split1.
     * @param branchCommit branch1.
     * @param headCommit branch2.
     * @return a commit that splits
     */
    public Commit checkMerge(Commit splitPoint,
                             Commit headCommit, Commit branchCommit) {
        stagingArea = Utils.readObject(
                Utils.join(".gitlet",
                        "StagingArea"), StagingArea.class);
        String tempMessage = "CHANGE THE GODDAMN MESSAGE";
        Commit mergedCommit = new Commit(tempMessage, null);
        List<String> allFiles = Utils.plainFilenamesIn(currentWorkingDirectory);
        for (String fileName: allFiles) {
            String splitBlob = Utils.readContentsAsString(
                    Utils.join(".gitlet/blobs",
                    splitPoint.getHashFromFile(fileName)));
            String headBlob = Utils.readContentsAsString(
                    Utils.join(".gitlet/blobs",
                    headCommit.getHashFromFile(fileName)));
            String branchBlob = Utils.readContentsAsString(
                    Utils.join(".gitlet/blobs",
                    branchCommit.getHashFromFile(fileName)));
            if (splitBlob.equals(headBlob)
                    &&
                    !branchBlob.equals(splitBlob)) {
                mergedCommit.addOneToTracked(fileName, branchBlob);
            } else if (splitBlob.equals(branchBlob)
                    &&
                    !headBlob.equals(splitBlob)) {
                mergedCommit.addOneToTracked(fileName, headBlob);
            } else if (!splitBlob.equals(branchBlob)
                    &&
                    !splitBlob.equals(headBlob)) {
                if (splitBlob.equals(headBlob)) {
                    mergedCommit.addOneToTracked(fileName, headBlob);
                } else {
                    System.out.println("Encountered a merge conflict.");
                    System.exit(0);
                }
            } else if (!splitPoint.exists(fileName)
                    &&
                    !branchCommit.exists(fileName)
                    &&
                    headCommit.exists(fileName)) {
                mergedCommit.addOneToTracked(fileName, headBlob);
            } else if (!splitPoint.exists(fileName)
                    &&
                    headCommit.exists(fileName)
                    &&
                    branchCommit.exists(fileName)) {
                mergedCommit.addOneToTracked(fileName, branchBlob);
            } else if (headBlob.equals(splitBlob)
                    &&
                    !branchCommit.exists(fileName)) {
                mergedCommit.removeFromTracked(fileName);

            } else if (branchBlob.equals(splitBlob)
                    &&
                    headCommit.exists(fileName)) {
                mergedCommit.removeFromTracked(fileName);
            }
        }
        return null;

    }

    /**
     * make gitlet stuff.
     * @param commitHash commitHash.
     */
    public void reset(String commitHash) {
        if (!Utils.join(".gitlet/commits", commitHash).exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        stagingArea = Utils.readObject(Utils.join(
                ".gitlet", "StagingArea"), StagingArea.class);
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"), String.class);
        Commit headCommit = Utils.readObject(Utils.join(
                ".gitlet/commits", hEAD), Commit.class);
        Commit inputCommit = Utils.readObject(Utils.join(
                ".gitlet/commits", commitHash), Commit.class);
        List<String> cwdFiles =
                Utils.plainFilenamesIn(currentWorkingDirectory);

        for (String file: cwdFiles) {
            if (!headCommit.exists(file) && !stagingArea.exists(file)) {
                System.out.println("There is an untracked file in the way; "
                        +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String fileName: cwdFiles) {
            if (fileName.contains(".txt")) {
                File currentFile =
                        Utils.join(currentWorkingDirectory, fileName);
                Utils.restrictedDelete(currentFile);
            }
        }

        for (Map.Entry<String, String> entry
                :
                inputCommit.getAllCurrentTracked().entrySet()) {
            String[] stuff = new String[4];
            stuff[0] = "nothing";
            stuff[1] = commitHash;
            stuff[2] = "--";
            stuff[3] = entry.getKey();
            checkout(stuff);
        }

        currentBranch = Utils.readObject(
                Utils.join(".gitlet", "currentBranch"), String.class);

        byte[] serializedStuff = Utils.serialize(inputCommit);
        Utils.writeContents(Utils.join(
                ".gitlet/branches", currentBranch),
                Utils.sha1(serializedStuff));

        hEAD = commitHash;
        Utils.writeObject(Utils.join(".gitlet", "HEAD"), hEAD);
        stagingArea.clear();
        Utils.writeObject(Utils.join(".gitlet", "StagingArea"), stagingArea);
    }

    /**
     * make gitlet stuff.
     * @param branchName branchname.
     */
    public void rmbranch(String branchName) {
        if (!Utils.join(".gitlet/branches", branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        hEAD = Utils.readObject(Utils.join(".gitlet/HEAD"), String.class);
        String branchRemove = Utils.readContentsAsString(
                Utils.join(".gitlet/branches", branchName));
        if (hEAD.equals(branchRemove)) {
            System.out.println("Cannot remove current branch.");
            System.exit(0);
        }
        Utils.join(".gitlet/branches", branchName).delete();

    }

}
