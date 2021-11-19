# Assignment 2 Instructions

**Deadline: March 29th 11:59pm EST**

## Overview

In this project, you will be required to design a working software application based on specifications provided (mimicking the sort of specifications you may receive from a real-life client).
Link for video explanation: [A2 Explanation](https://youtu.be/6oj9oQn79Ks)

## Part 0: Group formation

You will be required to be in **groups of 4** for this assignment, so your first task will be to find good and reliable group members.
You will have 1 week to finalize your groups.
On **March 8th, 2021**, group formation will end. If by this time you do not have a group, we will pair you up with other students who do not have a group.

As this is a group project, everyone will be required to pull their own weight.
To make sure everyone is doing this, you will be filling out two peer feedback/evaluation forms. One midway at the end of the first two weeks of this project (you can think of this as the end of your first sprint), and one at the end of the project (the end of your second and final sprint).

**How to find a group:**

You can use the **#looking-for-assignment-group** channel on Discord to find some friends to pair up with.

Or if you have **Big-O energy** you can slide into the DMs through Discord ;).

## Part 1: Read the project specifications below

You will be creating the back-end of a digital distribution system for video games (similar to Valve Corporation's Steam) that allows users to buy or sell access to games.

Your program should work alongside a theoretical front-end interface that provides the back-end with data in the form of daily transaction text files, which contain a day's worth of transactions and user commands.

Your system will be used by four types of users: buyers, sellers, full-standard users (who can buy and sell), and system staff (admin users). Each user, including administators, will have a username, account balance, and inventory of games that they own or have put up for purchase.

Like most customer requirements in real-life, the ones listed in this document are **notoriously unreliable.** That is, everything stated below is a correct requirement of the system, but there may be other considerations that were omitted (for example, there is no mention as to whether users can have a negative account balance [they cannot]). If there are other edge cases or unclear requirements not addressed in this document, it is your responsibility to seek clarification by asking questions on Piazza before the assignment due date.

The customers of this system are the CSC207 teaching team, who reserve the right to make adjustments to the requirements when they deem it necessary. Because of this, it is a good idea not to hard code values into your program in case the requirements change partway through the project.

### Transactions:

The Front End is capable of handling the following transactions, which will then be processed by your Back End:

**login** - start a Front End session

**logout** - end a Front End session

**create** - add a user with the ability to buy/sell games (privileged transaction)

**delete** - remove a user (privileged transaction)

**sell** - add a game to the user's inventory and to the list of games for sale

**buy** - purchase a game being sold by another user and add it to the user's inventory

**refund** - issue a credit to a buyer’s account from a seller’s account (privileged transaction)

**addcredit** - add credit directly into the system

**auctionsale** - change the prices of all games for sale to incorporate a seasonal discount (privileged transaction)

### Transaction Code Details:

**login** - start a Front End session

-   The front end will handle all of the login functionality, including passwords and security. You will not need to implement anything in your system to support this.

**logout** - end a Front End session

-   The front end will handle all of the logout functionality. You will not need to implement anything in your system to support this.

**create** – creates a new user with buying and/or selling privileges.

-   The front end will ask for the new username
-   The front end will ask for the type of user (admin or full-standard, buy-standard, sell-standard)
-   The front end will ask for the initial account balance of the new user
-   This information is saved to the daily transaction file
-   Constraints:
    -   privileged transaction - only accepted when logged in as admin user
    -   new user name is limited to at most 15 characters
    -   new user names must be different from all other current users
    -   maximum credit can be 999,999

**delete** - cancel any games for sale by the user and remove the user account.

-   The front end will ask for the username
-   This information is saved to the daily transaction file
-   Constraints:
    -   privileged transaction - only accepted when logged in as admin user
    -   username must be the name of an existing user but not the name of the current user
    -   no further transactions should be accepted on a deleted user’s behalf, nor should other users be able to purchase their games for sale

**sell** – put up a game for sale

-   The front end will ask for the game name
-   The front end will ask for the price of the game in dollars (e.g. 15.00)
-   The front end will ask for the sale discount when an auctionsale is taking place (e.g. 25.00 percent deducted)
-   This information is saved to the daily transaction file
-   Constraints:
    -   Semi-privileged transaction - only accepted when logged in any type of account except standard-buy.
    -   the maximum price for an game is 999.99
    -   the maximum length of an game name is 25 characters
    -   the maximum sale discount is 90 percent
    -   a game that was just put up for sale cannot be purchased until the following day.

**buy** – purchase an available game for sale

-   The front end will ask for the game name and the seller’s username
-   The price of the game should be deducted from the buyer's account balance and added to the seller's account balance
-   The game should be added to the buyer's inventory
-   This information is saved to the daily transaction file
-   Constraints:
    -   Semi-privileged transaction - only accepted when logged in any type of account except standard-sell.
    -   game name must be an existing game in the seller's inventory that is available for sale
    -   cannot purchase a game already in the user's inventory
    -   user must have enough available funds to purchase the game

**refund** - issue a credit to a buyer’s account from a seller’s account (privileged transaction)

-   The front end will ask for the buyer’s username, the seller’s username and the amount of credit to transfer.
-   The specified amount of credit should be transferred from the seller’s credit balance to the buyer’s credit balance.
-   This information is saved to the daily transaction file
-   Constraints:
    -   Buyer and seller both must be current users

**addcredit** - add credit into the system for the purchase of accounts

-   In admin mode, should ask for the amount of credit to add and the username of the account to which credit is being added.
-   In a standard account, should ask for the amount of credit to add to the user's own account.
-   This information is saved to the daily transaction file
-   Constraints:
    -   In admin mode, the username has to be an existing username in the system.
    -   A maximum of $1000.00 can be added to an account in a given day.

**auctionsale** - change the prices of all games for sale to incorporate a seasonal discount (privileged transaction)

-   Activate the discounts on all games for sale, changing the amount transferred during buy transactions
-   If an auctionsale is already on, this transaction should conclude the auctionsale and disable the discounts
-   Constraints:
    -   privileged transaction - only accepted when logged in as admin user

### Daily Transaction File:

At the end of each day, the front end provides a daily transaction file called daily.txt, listing every transaction made in the day.
Contains variable-length text lines of the following formats:

XX UUUUUUUUUUUUUUU TT CCCCCCCCC

Where:

-   XX
    -   is a two-digit transaction code: 00-login, 01-create, 02-delete, 06-addcredit, 10-logout
-   UUUUUUUUUUUUUUU
    -   is the username
-   TT
    -   is the user type (AA=admin, FS=full-standard, BS=buy-standard, SS=sell-standard)
-   CCCCCCCCC
    -   is the available credit

XX UUUUUUUUUUUUUUU SSSSSSSSSSSSSSS CCCCCCCCC

Where:

-   XX
    -   is a two-digit transaction code: 05-refund
-   UUUUUUUUUUUUUUU
    -   is the buyer’s username
-   SSSSSSSSSSSSSSS
    -   is the seller’s username
-   CCCCCCCCC
    -   is the refund credit

XX IIIIIIIIIIIIIIIIIII SSSSSSSSSSSSS DDDDD PPPPPP

Where:

-   XX
    -   is a two-digit transaction code: 03-sell.
-   IIIIIIIIIIIIIIIIIII
    -   is the game name
-   SSSSSSSSSSSSSS
    -   is the seller’s username
-   DDDDD
    -   Is the discount percentage
-   PPPPPP
    -   is the sale price

XX IIIIIIIIIIIIIIIIIII SSSSSSSSSSSSSSS UUUUUUUUUUUUUU

Where:

-   XX
    -   is a two-digit transaction code: 04-buy.
-   IIIIIIIIIIIIIIIIIII
    -   is the game name
-   SSSSSSSSSSSSSSS
    -   is the seller’s username
-   UUUUUUUUUUUUUUU
    -   is the buyer's username

Constraints:

-   numeric fields are right justified, filled with zeroes
    (e.g., 005.00 for a 5$ game)
-   alphabetic fields are left justified, filled with spaces
    (e.g. John Doe for account holder John Doe)
-   unused numeric fields are filled with zeros
    (e.g., 0000)
-   In a numeric field that is used to represent a monetary value or percentage, “.00” is appended to the end of the value (e.g. 00110.00 for 110)
-   unused alphabetic fields are filled with spaces (blanks)
    (e.g., Mike M         )
-   all sequences of transactions begin with a login (00) transaction code and end with a logout (10) transaction code

### Back End Error Recording:

All recorded errors should be of the form: ERROR: \<msg\>

-   For failed constraint errors, \<msg\> should contain the type and description of
    the error and the transaction that caused it to occur.
-   For fatal errors, \<msg\> should contain the type and description and the file that
    caused the error.

### Data output structure:

All output should be written to the screen using text. For example, your program can have println statements saying things like "$20.00 has been added to the balance of user Mike Miljanovic".

## Part 2: Create a product backlog

See `scrum_data.md` for a link to the spreadsheet template you should use for this.

Your product backlog must list all the tasks you need to do for this project, based on the specifications above. Part of software design in a real world setting is identifying development tasks based on specifications that might be imprecise, like some of the information provided to you in Part 1. In your copy of the spreadsheet we provided, write down each "user story" (requirement from the specifications that you need to complete), with an "estimation" and a "priority" ranking for each story. In the included file `"scrum_data.md"` in this repository, copy/paste the link to your product backlog (follow the instructions in the `"scrum_data.md"` file to do this), then add and commit the scrum_data file. Do this before doing anything further in this assignment.

Note: Product backlogs change over time. Don't expect to get everything right the first time.

More information about each entry in the backlog:

1. User story: The user stories should specify the user which the task is for (in this case, buyer, seller, both, or admin), and what it is that the user wants to do (follow the example format in the given spreadsheet)
2. Estimation: The estimation should be how much effort you estimate a task will take (for particularly large tasks, break it up into smaller 'sub-tasks' -- you can make their IDs be in the format 1.1, 1.2, ...). Within the Scrum Framework the estimation is usually not actual time estimates - a more abstracted metric to quantify effort is used. For this project, we want you to use sizes like XS (really tiny task), S (small task), M (medium task), L (large task), or XL (very large task).
3. Priority: Rank each user story to be either "low", "medium" or "high" priority

## Part 3: Test Suite

A small portion of this assignment's marks will be dedicated to
ensuring you have created a proper test suite. Specifically,
you will be required to conduct **White Box testing** by using junit
tests.

We have covered a little bit of white box testing with code reviews
and paraphrasing but not to a large extent.

White box testing should test every possible input which leads to a
different output. This means, you should be checking that loops, conditionals (ifs, else ifs, else, switch + case, ...) and other statements relying on input, function as intended.

Here are some resources you might find helpful:

-   White Box: https://www.guru99.com/white-box-testing.html
-   JUnit 5: https://junit.org/junit5/docs/current/user-guide/

In addition to these, a TA has made a video demonstrating how to
setup JUnit 5 in IntelliJ, as well as a guide on making basic test cases for a simple program.

Video: [How to make JUnit5.4 Test Cases!](https://youtu.be/_sVVBGHISnE)

You should be testing all of the important methods + constructors in your program to get full marks for this part.
By doing this, you will also ensure that your program works as you
want it to work! If you find any test cases which should be passing,
but are failing, you will have found yourself a bug that you can fix!

Making test cases is something you will spend a lot of time doing in
the workfield to ensure software is robust and can't be broken. This is
something we want to introduce you to early on for your own benefit :).

## Part 4: Do a code sprint

Go through the product backlog and choose which tasks will be completed in the current sprint. Drag the chosen tasks in your spreadsheet from the "unassigned to sprint" heading to be under the current sprint's heading instead. Add a "Task Owner" -- the team member who will be in charge of that task for this sprint, and a "Status" as "In progress" (change this to "Complete" once a task is done).

We suggest one-week long code sprints. So, for this assignment, as a simulation of a Scrum project, you will have about three code sprints.

We realize you have other courses and don't expect you to hold daily scrum meetings. However, aim to have a scrum meeting at least every 2-3 days or so. Remember, attendance at these meetings is mandatory.

### Getting started with coding

To get started with the code, in IntelliJ choose File > Open, and then navigate to the your a2 repository you cloned from Github classroom. All your .java files should be created within the "src" directory inside a2. Feel free to make your own packages within "src" to organize your files.

### Git Workflow during code sprint

We will be learning to use a few new git commands through this assignment -- git branch, git checkout, and git merge. Read this nice tutorial for an overview and some examples of each of these commands (use the sidebar to navigate through the "Using branches" section): https://www.atlassian.com/git/tutorials/using-branches

For this project, each task will have its own "branch" within your repository where it is coded, and one person in your team (the "team manager") can be in charge of merging each branch to the "master" branch as tasks get completed. (Note: You do not need to make new branches for sub-tasks of each user story task if you have them (such as 3.1.x))

So, anytime you begin working on a new task, you should do the following:

-   git pull: this is very important to do before you start coding! This will pull the latest history for this branch from the remote (origin) and merge with the latest commit. Do this on a clean workspace (before you make any local changes), otherwise things get complicated.

-   git branch NEW_BRANCH where NEW_BRANCH is an identifying name for the task this branch will be dedicated to; this command branches off the latest version of working code from the remote

-   git checkout NEW_BRANCH: this command switches your working area to the branch that you want to checkout. Do NOT forget to do this after creating the new branch! or else you will still remain in master. (Note: You can type 'git branch' without anything after the command to get a list of all the available branches, and the current branch that you are working on will be highlighted).

Then, modify the files relevant to the task for this branch. Once you are done:

`git add --all .`

`git commit -m "COMMIT MESSAGE"`

`git push`

(you may have to do `git push --set-upstream origin NEW_BRANCH` to configure the new branch the first time you're pushing it)

Once you are done working on a branch and you want to merge the work you did back to master:

`git checkout master`

`git pull (gets all latest development from master branch)`

`git merge NEW_BRANCH`

If there are conflicts: resolve conflicts by opening the files and keeping what you want, deleting what you don't want, and then again: add, commit and push.

Now, on Github you need to submit a pull request. Navigate to your branch and click "Compare & Pull request" and let your team manager know that you made some changes that need to be pulled into master. (See the video linked below for a demo of this.)

See this video for an example of this workflow:
https://www.youtube.com/watch?v=oFYyTZwMyAg&ab_channel=LearnCode.academy

_Note: If you want to use different tools like Github Desktop or IntelliJ's git menu instead of directly using the git commands like we suggested above, that's okay, as long as you are still following the same workflow._

## Part 5: Repeat parts 2-4

Revisit the product backlog, updating it as necessary, create test cases for the new functionality and do code sprints. Repeat until the deadline.

## Part 6: Changing Requirements

During the course of this project, it is possible that some requirements may be altered or added in addition to those included in the document. You must keep track of these changes using a file called requirements.txt, which will store any clarifications or assumptions made about the operation of the system. Be sure to keep this file up to date and be aware that there may need to be adjustments to your code even if you finish early!

## Part 7 (OPTIONAL): Adding bonus features

For bonus marks (up to 5% extra), you can add your own extra features to make your program unique and more marketable. e.g. implementing the front-end using a GUI or command prompt to handle transactions rather than using daily.txt would count as an example of a 'bonus feature'. Your extra features could also be statistical in nature, letting the user and admin user view useful information e.g. current items available for sale, list of current users, etc.

**NOTE:**

Do NOT hand in half-finished bonus features - if you want us to see your attempt at completing one, you can describe this in your FEATURES.txt file or comment out the relevant, incomplete code with a note stating this was an attempt at a bonus feature. Your submitted program must be fully functional and able to run when you hand it in to avoid getting a grade of 0 on the "functionality" portion of this assignment. If we can't run your code, you will get no marks for "functionality".

## Part 8 (IMPORTANT): Tell us about your code and how to use your software

We will mainly use this file to grade your code. This means, if you do NOT complete these files, you will receive a grade of 0 in this assignment.

`FEATURES.md`

Complete the file `FEATURES.md` that explains how to run your code, and lists and describes all the features of your project, and which part of the provided specifications they fulfill. The description should mention any important design decisions you made in coding that feature (e.g. if you used inheritance, or a design pattern, etc. clearly let us know in this file, and briefly explain why you made this decision). If you have any bonus features you added in, clearly include this here with a title "Bonus feature".

This file should instruct the marker on how to run your program and any other information we need to set up and run your code. If you use other configuration files besides daily.txt (such as files for items or users) please include detailed instructions so that we know how we can and cannot modify those files in order to get your program to run.

## Notes

### Citing Code:

If you use any code you find, cite it according to the format described in the "Examples of citing code sources" section of the "Writing Code" page of the MIT Academic Integrity handbook here: https://integrity.mit.edu/handbook/writing-code

### Some other helpful git commands:

Check the current status of all the files in the repository:
git status

Tools for undo-ing all your local changes:
git reset --hard: put tracked files back as they were
git clean -fd: remove untracked files

### Grading:

Each group's project should have similar functionality, even if each design is different from one group to the next. Try to make your design encapsulated so that a change to one part of the program does not have much impact on other parts of the program. Consider each of the SOLID principles (https://www.baeldung.com/solid-principles) when making design decisions. Likewise, we will be discussing design patterns during the next few lectures. Keep in mind if any of the design patterns can improve your design.

There is more than one possible design that will work well with this project. You only have to develop one of these designs. If you are having trouble making major decisions about how the overall design of your project should look, feel free to ask your TA during lab, attend office hours as a group or book an appointment with an instructor to discuss it. There may also be clarifications posted to the message board (Piazza). Make sure that at least one group member is monitoring Piazza on any given day.

This is a course called Software Design. Therefore, most of your mark will come from your design.

Here is a _tentative_ grading scheme:

-   Code Architecture (use of object-oriented programming principles and design patterns, no code smells (no repetitive code, overall code is clean and easy to read), and a clear discussion of these design decisions in FEATURES.txt): ~35\%
-   Functionality (based on all your working features): ~25\%
-   Proper Test Suite (full and robust JUnit test cases to test your software): ~10\%
-   Requirements (Listing and addressing the missing requirements and clarifications): ~10\%
-   Documentation (complete, well-written Javadocs): ~10\%
-   Use of Scrum (based on your product backlog): ~5\%
-   Use of git (following the workflow detailed in this handout, using properly named branches for each task, having useful commit messages, having multiple commits throughout the assignment work period rather than all at the end): ~5\%

#### Extensibility:

Keep in mind that your imaginary client may ask to add further requirements over the next month or so. The requests might involve expanding your software to include more features or handle more inputs. Be sure to design your code with this in mind.

In real life, you would be able to ask a contact with the distribution company for further clarification regarding the software they want from you. For the purposes of this project, you can direct such questions to the discussion board. Any response from instructors is to be taken as the company's response. You are also invited to do your own research regarding digital distribution systems. For example, what really happens when a game is put up for sale on Steam?

## What To Submit

As you work, regularly commit and push your changes. We will be checking the git logs to make sure everyone is making a significant contribution. Try to make your last changes to the code at least one day before the due date. That will give you enough time to finalize your code, update your FEATURES.txt file and check that your program still works after everyone's work has been merged for the last time.

We will be looking for:

-   A completely functional program that meets the above specifications
-   `daily.txt` (if relevant)
-   `scrum_data.md`
-   `requirements.md`
-   `FEATURES.md`
-   Any other configuration files that you need for us to run your code

**Note:** The files have the `.md` extensions because [markdown](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet) looks nice. If you wish, you can just write plain-text inside the markdown files.

## Part 9 - Relax!

**Congratulations on finishing your last assignment, A2!!!**

![congratulations](https://media1.tenor.com/images/fbbd906d9cb5624fbafd7f536aec5cc3/tenor.gif?itemid=16786818)

Take some time to relax and do some things you enjoy, since we know that university is stressful at times (we were all in your position not too long ago).

Go play some games, watch shows/anime, read a book, draw something, #BlameDeval or do literally anything that you love to do so you can destress!

Here's a [great video](https://www.youtube.com/watch?v=dQw4w9WgXcQ) I watch to celebrate completing assignments!

And here's **another** [great video](https://streamable.com/t9587) I like to watch, trust me, all UTM CS kids will relate to this, credits to TheRaghavSharma for making this masterpiece.
