# Gitlet Design Document

**Name**: Brendon Lin

## Classes and Data Structures
Repository - Contains everything from the staging area, the commits, etc. Will have a blobs folder,
and a staging area folder. 

Commit - an object that will have the timestamp, the content, and the message

StagingArea - an object with two LinkedHashSets, one for addition, one for deletion.



## Algorithms
Init - Create the .gitlet repository. Created from the CWD, and a .gitlet folder. Also has a initial commit that is hard
coded because I couldn't find how to make it minimum time

add - Look through the files in the CWD and see if there's a valid file with the name passed into the parameter. If 
there is, add it to the staging area, otherwise error with no file found. Staging area will be a list of Files.

commit - check to make sure the staging area is not empty, and if it is not, create new Commit objects based on the 
content in the staging area, making sure that it is pointing to parents in the correct order, and clearing the staging 
area after everything. Needs persistence.

rm - check inside the staging area to make sure that there is something that can be removed, and then from there, look 
through the staging area to see if there is a file with the name specified, and if there is, remove it from the staging 
area. if it is being tracked already by the current commit, add it to the removal staging area and remove it from the 
current working directory.

log - make sure to add three ===, a new line, then "commit" with the SHA hash, the date, and the message respectively 
all on separate lines. needs to be in order. Special case for merge commits that will show two hexidecimal numerals 
following "Merge"

global-log - exactly as described above, except order does not matter, check gitlet.Utils for the exact method that can
be super helpful.

find - look through each of the commits' messages to see if there is one that matches with the current message being 
passed into the function. If it does find that message, print out the id of the commit. Also needs a check to make sure 
that the commit exists.

status - should print out with === in between the words of "Branches", "Staged Files", "Removed Files", "Modifications
not staged for commit", and "Untracked Files". Everything that is in the respective name earlier should also print out 
the status of each. e.g.
=== Modifications Not Staged For Commit ===
junk.txt (deleted)
wug3.txt (modified)

checkout - has three different versions. 1. takes the version of the file (passed in as a parameter) as it exists in the
head commit, the front of the current branch, puts it in the current working directory, overwriting the file if needed, 
and the new file is not added to the staging area. 2. Same behavior as described in 1, but the main difference being 
that it takes in a commit id and a file name. 3. Takes in a branch name (needs a check to make sure branch exists), and
takes ALL files in the commit at the head of the inputted branch into the current working directory, overwriting if
needed. The inputted branch will now also be considered the HEAD, or the current branch.

branch - creates a new branch (remember that it's a pointer) and points it as the current head node. "This command does
NOT immediately switch to the newly created branch." BEFORE you ever call branch, your code should be running with a 
default branch called "master"

rm-branch - deletes the branch with the name passed into the function. Should only be deleting the pointer, since all 
branches should just be pointers.

reset - Takes in a commit id, and checks out all files associated with the commit in the commits. Moves the current 
branch's head to that commit node. Staging area will be cleared. "The command is essentially checkout of an arbitrary
 commit that also changes the current branch head"

merge - I don't even know..



## Persistence
The entire repository needs to persistent. This includes the commits, the staging area for both addition and removal,
and the pointers and branches. In order to do this, many of these need to be serialized and then written using the Utils
methods. Keep in mind that some of them may need to the SHA'd first before being serialized. 


