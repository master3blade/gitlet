package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Brendon Lin
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws Exception {
        Repository newGitlet = new Repository();
        if (args[0].equals("init")) {
            newGitlet.init();
        } else if (args[0].equals("add")) {
            newGitlet.add(args[1]);
        } else if (args[0].equals("commit")) {
            newGitlet.commit(args[1]);
        } else if (args[0].equals("checkout")) {
            newGitlet.checkout(args);
        } else if (args[0].equals("log")) {
            newGitlet.log();
        } else if (args[0].equals("find")) {
            newGitlet.find(args[1]);
        } else if (args[0].equals("branch")) {
            newGitlet.branch(args[1]);
        } else if (args[0].equals("rm-branch")) {
            newGitlet.rmbranch(args[1]);
        } else if (args[0].equals("merge")) {
            newGitlet.merge(args[1]);
        } else if (args[0].equals("global-log")) {
            newGitlet.globallog();
        } else if (args[0].equals("reset")) {
            newGitlet.reset(args[1]);
        } else if (args[0].equals("rm")) {
            newGitlet.rm(args[1]);
        } else if (args[0].equals("status")) {
            newGitlet.status();
        } else {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
    }

}
