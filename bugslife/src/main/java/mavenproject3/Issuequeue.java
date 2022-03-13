package mavenproject3;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

// Issuequeue class used to store issue list
public class Issuequeue {
    // instant variables
    private String queuename;
    private ArrayList<Issue> list = new ArrayList<>();
    
    // empty constructor
    public Issuequeue() {
    }
    
    // constructor for user input queue name
    public Issuequeue(String queuename) {
        this.queuename = queuename;
    }
    
    // add an issue into the arraylist of issue together with its title
    public boolean offer(String title, Integer priority, ArrayList<String> tag, String descriptionText, String createdBy, String assignee, ArrayList<Comment> comments) {
        Issue obj = new Issue(title, priority, tag, descriptionText, createdBy, assignee, comments);
        return list.add(obj);
    }
    
    // add an issue into the arraylist of issue
    public void offer(Issue a) {
        list.add(a);
    }

    // store a list of issues into the arraylist of issue
    public void offer(List<Issue> a) {
        for (int i = 0; i < a.size(); i++) {
            list.add(a.get(i));
        }
    }

    // remove an issue
    public boolean remove(Issue a) {
        return list.remove(a);
    }

    // get the first issue of the arraylist
    public Issue peek() {
        return list.get(0);
    }
    
    // remove the first issue of the arraylist
    public Issue poll() {
        return list.remove(0);
    }

    // delete all the issue of the arraylist
    public void clear() {
        list.clear();
    }
    
    // return the size of the arraylist
    public int getSize() {
        return list.size();
    }

    // check the existance of an issue from the arraylist
    public boolean contains(Issue a) {
        return list.contains(a);
    }

    // check either the arraylist is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }

    // display the issue board 
    public void display() {
        int index = 0;
        System.out.println("Issue board");
        System.out.println("+------+-------------------------------------------+----------------+-------------------------------------+--------------+-------------------+----------------------+---------------------+");
        System.out.printf("%1s%3s%4s%21s%23s%11s%6s%20s%18s%11s%4s%11s%9s%15s%8s%15s%7s", "|", "ID", "|", "Title", "|", " Status", "|", "    Tag", "|", "Priority", "|", "Time", "|", "Assignee", "|", "CreatedBy", "|");
        System.out.println("\n+------+-------------------------------------------+----------------+-------------------------------------+--------------+-------------------+----------------------+---------------------+");
        if (list.size() == 0) {
            System.out.printf("%-3s%-4s%-4s%-40s%-4s%-13s%-4s%-34s%-10s%-5s%-3s%-17s%-2s%-21s%-2s%-20s%-3s", "|", "-", "|", "-", "|", "-", "|", "-", "|", "-", "|", "-", "|", "-", "|", "-", "|");
            System.out.println("\n+------+-------------------------------------------+----------------+-------------------------------------+--------------+-------------------+----------------------+---------------------+");
        } else {
            while (index < list.size()) {
                Issue temp = list.get(index);
                System.out.printf("%-3s%-4s%-4s%-40s%-4s%-13s%-4s%-34s%-10s%-5s%-3s%-17s%-2s%-21s%-2s%-20s%-3s", "|", temp.getId(), "|", temp.getTitle(), "|", temp.getStatus(), "|", temp.getTag(), "|", temp.getPriority(), "|", temp.changeDateFormat(), "|", temp.getAssignee(), "|", temp.getCreatedBy(), "|");
                index++;
                System.out.println("\n+------+-------------------------------------------+----------------+-------------------------------------+--------------+-------------------+----------------------+---------------------+");
            }
        }
    }
    
    // display selected index issue details and following actions
    public void displayIssueDetails(int issueNum) throws IOException, ParseException {
        int index = 0;
        while (index < list.size()) {
            Issue temp = list.get(index);
            if (temp.getId() == issueNum) {
                if (User.loginName.equals(temp.getAssignee())) {
                    Issue.setIssueStatus("In Progress");
                }
                System.out.println("Issue ID: " + (temp.getId()) + "\tStatus: " + temp.getStatus());
                System.out.println("Tag: " + temp.getTag() + "\tPriority: " + temp.getPriority() + "\tCreated On: " + temp.changeDateFormat());
                System.out.println(temp.getTitle());
                System.out.println("Assigned to: " + temp.getAssignee() + "\tCreated By: " + temp.getCreatedBy());
                System.out.println("\nIssue Description");
                System.out.println("-----------------");
                System.out.println(temp.getDescriptionText());
                System.out.println("\nComments");
                System.out.println("---------");
                for (int i = 0; i < temp.getComments().size(); i++) {
                    System.out.print("#" + (i + 1));
                    System.out.println(temp.getComments().get(i));
                }
                Scanner in = new Scanner(System.in);
                if (User.getLoginName().equals(temp.getCreatedBy())) {
                    System.out.println("Enter\n'r' to react\nor 'c' to comment\nor 'b' to issue dashboard");
                    System.out.println("or 's' to change status \nor 'e' to edit the issue ");
                    String input = in.next();
                    switch (input) {
                        case "r":
                            React.addReact();
                            break;
                        case "c":
                            Comment.addComment();
                            break;
                        case "b":
                            break;
                        case "s":
                            if (User.getLoginName().equals(temp.getAssignee())) {
                                System.out.println("Enter '1' to Open, '2' to Close, '3' to Resolved");
                                int a = in.nextInt();
                                switch (a) {
                                    case 1:
                                        Issue.setIssueStatus("Open");
                                        break;
                                    case 2:
                                        Issue.setIssueStatus("Close");
                                        break;
                                    case 3:
                                        Issue.setIssueStatus("Resolved");
                                        break;
                                }
                            } else if (User.getLoginName().equals(temp.getCreatedBy())) {
                                System.out.println("Enter '1' to Open, '2' to Close");
                                int a = in.nextInt();
                                switch (a) {
                                    case 1:
                                        Issue.setIssueStatus("Open");
                                        break;
                                    case 2:
                                        Issue.setIssueStatus("Close");
                                        break;
                                }
                            }
                            break;
                        case "e":
                            System.out.println("Which part you want to edit ? ");
                            System.out.println("Enter '1'--Title '2'--Description '3'--Assignee Name '4'--Priority '5--Tag'");
                            int editoption = in.nextInt();
                            in.nextLine();
                            switch (editoption) {
                                case 1:
                                    System.out.println("Enter new title : ");
                                    String newTitle = in.nextLine();
                                    Issue.setNewIssueTitle(newTitle);
                                    break;
                                case 2:
                                    String newDescription = "";
                                    System.out.println("Enter new description text : (Enter '$undo' for undo, '$redo' for redo, '$end' for end)");
                                    UndoRedoStack<String> a = new UndoRedoStack<>();
                                    while (in.hasNext()) {
                                        String s1 = in.nextLine();
                                        if (s1.equals("$end")) {
                                            break;
                                        } else if (s1.equals("$undo")) {
                                            a.undo();
                                            System.out.println(a);
                                        } else if (s1.equals("$redo")) {
                                            a.redo();
                                            System.out.println(a);
                                        } else {
                                            a.push(s1);
                                            System.out.println(a);
                                        }
                                    }
                                    System.out.println("------------------------------");
                                    System.out.println("Description text");
                                    System.out.println("------------------------------");
                                    if (a.size() > 1) {
                                        newDescription = newDescription + a.get(0) + "\n";
                                        for (int i = 1; i < a.size() - 1; i++) {
                                            newDescription = newDescription + a.get(i) + "\n";
                                        }
                                        newDescription = newDescription + a.get(a.size() - 1);
                                    } else {
                                        newDescription = newDescription + a.get(0);
                                    }
                                    System.out.println(newDescription);
                                    System.out.println("------------------------------");
                                    Issue.setNewDescription(newDescription);
                                    break;
                                case 3:
                                    System.out.println("Enter new assignee name : ");
                                    String newAssignee = in.nextLine();
                                    Issue.setNewAssignee(newAssignee);
                                    break;
                                case 4:
                                    System.out.println("Enter new priority : ");
                                    int newPriority = in.nextInt();
                                    Issue.setNewPriority(newPriority);
                                    break;
                                case 5:
                                    ArrayList<String> tag = new ArrayList<>();

                                    while (true) {
                                        try {
                                            System.out.println("Enter number of tags: ");
                                            int userInput = in.nextInt();
                                            in.nextLine(); //buffer
                                            for (int i = 0; i < userInput; i++) {
                                                System.out.println("Enter new tag : ");
                                                String newtag = in.nextLine();
                                                tag.add(newtag);
                                            }
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.out.println("Invalid value!\n");
                                            in.next();
                                        }
                                    }
                                    Issue.setNewTag(tag);
                                    break;
                            }
                    }
                    Issue.displayIssueBoard();
                } else if (User.getLoginName().equals(temp.getAssignee()) || User.getLoginName().equals(temp.getCreatedBy())) {
                    System.out.println("Enter\n'r' to react\nor 'c' to comment\nor 'b' to issue dashboard");
                    System.out.println("or 's' to change status: ");
                    String input = in.next();
                    switch (input) {
                        case "r":
                            React.addReact();
                            break;
                        case "c":
                            Comment.addComment();
                            break;
                        case "b":
                            break;
                        case "s":
                            if (User.getLoginName().equals(temp.getAssignee())) {
                                System.out.println("Enter '1' to Open, '2' to Close, '3' to Resolved");
                                int a = in.nextInt();
                                switch (a) {
                                    case 1:
                                        Issue.setIssueStatus("Open");
                                        break;
                                    case 2:
                                        Issue.setIssueStatus("Close");
                                        break;
                                    case 3:
                                        Issue.setIssueStatus("Resolved");
                                        break;
                                }
                            } else if (User.getLoginName().equals(temp.getCreatedBy())) {
                                System.out.println("Enter '1' to Open, '2' to Close");
                                int a = in.nextInt();
                                switch (a) {
                                    case 1:
                                        Issue.setIssueStatus("Open");
                                        break;
                                    case 2:
                                        Issue.setIssueStatus("Close");
                                        break;
                                }
                            }
                    }
                    Issue.displayIssueBoard();
                } else {
                    System.out.println("Enter\n'r' to react\nor 'c' to comment\nor 'b' to issue dashboard");
                    String input = in.next();
                    switch (input) {
                        case "r":
                            React.addReact();
                            break;
                        case "c":
                            Comment.addComment();
                            break;
                        case "b":
                            break;
                    }
                    Issue.displayIssueBoard();
                }
                break;
            }
            index++;
        }
    }
}
