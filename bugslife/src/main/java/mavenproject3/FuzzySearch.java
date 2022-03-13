package mavenproject3;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import static mavenproject3.FuzzySearch.editDistance;

// FuzzySearch class provides fuzzy seach feature
public class FuzzySearch {

    // instant variables
    @Transient
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("hibernateTest");

    // allow user to search issue 
    public static void search() throws IOException, ParseException {
        Scanner s = new Scanner(System.in);
        System.out.println("\nEnter '1'--title '2'--description text '3'--Comment");
        int searchStatus = s.nextInt();
        System.out.println("Enter keyword: ");
        s.nextLine();
        String keyword = s.nextLine();
        Issuequeue matches = new Issuequeue();
        int countMatch = 0;

        //read file
        System.out.println("==========");
        if (searchStatus == 1) {
            EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
            EntityTransaction et = null;
            String[] words;
            String[] keywords;
            Issue a = null;
            // the lowercase c refers to the object
            // :ID is a parameterized query thats value is set below
            String strQuery = "SELECT c FROM Project c WHERE c.id IS NOT NULL";
            // Issue the query and get a matching Project
            TypedQuery<Project> tq = em.createQuery(strQuery, Project.class);

            List<Project> projectList = new ArrayList<>();
            try {
                // Get matching project object and output
                projectList = tq.getResultList();
                for (int i = 0; i < projectList.get(Project.getProjectID() - 1).getIssues().size(); i++) {
                    words = projectList.get(Project.getProjectID() - 1).getIssues().get(i).getTitle().split(" ");
                    if (!keyword.contains(" ")) { //if keyword is single word
                        for (int j = 0; j < words.length; j++) { //loop through title to check with keyword
                            if (matchScore(words[j].toLowerCase(), keyword.toLowerCase()) >= 0.53) {
                                matches.offer(projectList.get(Project.getProjectID() - 1).getIssues().get(i));
                                break;
                            }
                        }
                    } else { //if keyword contains multiple word
                        keywords = keyword.split(" ");
                        outer:
                        for (int j = 0; j < keywords.length; j++) { //loop through keywords to check 
                            for (int k = 0; k < words.length; k++) {
                                if (matchScore(words[k].toLowerCase(), keywords[j].toLowerCase()) >= 0.53) {
                                    matches.offer(projectList.get(Project.getProjectID() - 1).getIssues().get(i));
                                    break outer;
                                }
                            }
                        }
                    }
                }
                sortSimilarityByTitle(matches, keyword);
                matches.display();
                System.out.println("Enter selected issue ID to check issue \nor 's' to search \nor 'c' to create issue\nor 'b' to return to project dashboard:");
                String option = s.next();
                s.nextLine();
                if (Issue.isInteger(option)) {
                    int numIndex = Integer.parseInt(option);
                    Issue.setIssueID(numIndex);
                    matches.displayIssueDetails(numIndex);
                } else if (option.equals("s")) {
                    FuzzySearch.search();
                } else if (option.equals("c")) {
                    Issue.addIssue();
                } else if (option.equals("b")) {
                    Project.displayProject();
                }
            } catch (NoResultException ex) {
                ex.printStackTrace();
            } finally {
                em.close();
            }
        } else if (searchStatus == 2) {
            EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
            EntityTransaction et = null;
            String[] words;
            String[] keywords;
            Issue a = null;
            // the lowercase c refers to the object
            // :ID is a parameterized query thats value is set below
            String strQuery = "SELECT c FROM Project c WHERE c.id IS NOT NULL";
            // Issue the query and get a matching Project
            TypedQuery<Project> tq = em.createQuery(strQuery, Project.class);
            List<Project> projectList = new ArrayList<>();
            try {
                // Get matching Project object and output
                projectList = tq.getResultList();
                for (int i = 0; i < projectList.get(Project.getProjectID() - 1).getIssues().size(); i++) {
                    words = projectList.get(Project.getProjectID() - 1).getIssues().get(i).getDescriptionText().split(" ");
                    if (!keyword.contains(" ")) { //if keyword is single word
                        for (int j = 0; j < words.length; j++) { //loop through description to check with keyword
                            if (matchScore(words[j].toLowerCase(), keyword.toLowerCase()) >= 0.53) {
                                matches.offer(projectList.get(Project.getProjectID() - 1).getIssues().get(i));
                                break;
                            }
                        }
                    } else { //if keyword contains multiple word
                        keywords = keyword.split(" ");
                        outer:
                        for (int j = 0; j < keywords.length; j++) { //loop through keywords to check 
                            for (int k = 0; k < words.length; k++) {
                                if (matchScore(words[k].toLowerCase(), keywords[j].toLowerCase()) >= 0.53) {
                                    matches.offer(projectList.get(Project.getProjectID() - 1).getIssues().get(i));
                                    break outer;
                                }
                            }
                        }
                    }
                }
                sortSimilarityByDescription(matches, keyword);
                matches.display();
                System.out.println("Enter selected issue ID to check issue \nor 's' to search \nor 'c' to create issue\nor 'b' to return to project dashboard:");
                String option = s.next();
                s.nextLine();
                if (Issue.isInteger(option)) {
                    int numIndex = Integer.parseInt(option);
                    Issue.setIssueID(numIndex);
                    matches.displayIssueDetails(numIndex);
                } else if (option.equals("s")) {
                    FuzzySearch.search();
                } else if (option.equals("c")) {
                    Issue.addIssue();
                } else if (option.equals("b")) {
                    Project.displayProject();
                }
            } catch (NoResultException ex) {
                ex.printStackTrace();
            } finally {
                em.close();
            }
        } else if (searchStatus == 3) {
            EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
            EntityTransaction et = null;
            String[] words;
            String[] keywords;
            Issue a = null;
            // the lowercase c refers to the object
            // :ID is a parameterized query thats value is set below
            String strQuery = "SELECT c FROM Project c WHERE c.id IS NOT NULL";
            // Issue the query and get a matching Project
            TypedQuery<Project> tq = em.createQuery(strQuery, Project.class);
            List<Project> projectList = new ArrayList<>();
            try {
                // Get matching project object and output
                projectList = tq.getResultList();
                for (int i = 0; i < projectList.get(Project.getProjectID() - 1).getIssues().size(); i++) {
                    String commentText = "";
                    for (int j = 0; j < projectList.get(Project.getProjectID() - 1).getIssues().get(i).getComments().size(); j++) {
                        commentText += (projectList.get(Project.getProjectID() - 1).getIssues().get(i).getComments().get(j).getText() + " ");
                    }
                    words = commentText.split(" ");
                    if (!keyword.contains(" ")) { //if keyword is single word
                        for (int j = 0; j < words.length; j++) { //loop through commentText to check with keyword
                            if (matchScore(words[j].toLowerCase(), keyword.toLowerCase()) >= 0.53) {
                                matches.offer(projectList.get(Project.getProjectID() - 1).getIssues().get(i));
                                break;
                            }
                        }
                    } else { //if keyword contains multiple word
                        keywords = keyword.split(" ");
                        outer:
                        for (int j = 0; j < keywords.length; j++) { //loop through keywords to check 
                            for (int k = 0; k < words.length; k++) {
                                if (matchScore(words[k].toLowerCase(), keywords[j].toLowerCase()) >= 0.53) {
                                    matches.offer(projectList.get(Project.getProjectID() - 1).getIssues().get(i));
                                    break outer;
                                }
                            }
                        }
                    }
                }
                sortSimilarityByComment(matches, keyword);
                matches.display();
                System.out.println("Enter selected issue ID to check issue \nor 's' to search \nor 'c' to create issue\nor 'h' to check the changelog \nor 'b' to return to project dashboard:");
                String option = s.next();
                s.nextLine();
                if (Issue.isInteger(option)) {

                    int numIndex = Integer.parseInt(option);
                    Issue.setIssueID(numIndex);
                    matches.displayIssueDetails(numIndex);
                } else if (option.equals("s")) {
                    FuzzySearch.search();
                } else if (option.equals("c")) {

                    Issue.addIssue();

                } else if (option.equals("b")) {
                    Project.displayProject();
                }

            } catch (NoResultException ex) {
                ex.printStackTrace();
            } finally {
                em.close();
            }
        }
    }

    //Levenshtein Distance (searching algo)
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    //method to calc similarity score
    public static double matchScore(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
            /* both strings are zero length */ }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    // use to sort the issue based on similarity by title
    public static Issuequeue sortSimilarityByTitle(Issuequeue list, String keyword) {
        ArrayList<Issue> sortList = new ArrayList<>();
        while (!list.isEmpty()) {
            sortList.add(list.poll());
        }
        Comparator<Issue> similarity = new Comparator<>() {
            @Override
            public int compare(Issue s1, Issue e2) {
                if (matchScore(s1.getTitle(), keyword) > matchScore(e2.getTitle(), keyword)) {
                    return -1;
                } else if (matchScore(s1.getTitle(), keyword) < matchScore(e2.getTitle(), keyword)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(sortList, similarity);
        for (int i = 0; i < sortList.size(); i++) {
            list.offer(sortList.get(i));
        }
        return list;
    }

    // use to sort the issue based on similarity by description
    public static Issuequeue sortSimilarityByDescription(Issuequeue list, String keyword) {
        ArrayList<Issue> sortList = new ArrayList<>();
        while (!list.isEmpty()) {
            sortList.add(list.poll());
        }
        Comparator<Issue> similarity = new Comparator<>() {
            @Override
            public int compare(Issue s1, Issue e2) {
                if (matchScore(s1.getDescriptionText(), keyword) > matchScore(e2.getDescriptionText(), keyword)) {
                    return -1;
                } else if (matchScore(s1.getDescriptionText(), keyword) < matchScore(e2.getDescriptionText(), keyword)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(sortList, similarity);
        for (int i = 0; i < sortList.size(); i++) {
            list.offer(sortList.get(i));
        }
        return list;
    }

    // use to sort the issue based on similarity by comment
    public static Issuequeue sortSimilarityByComment(Issuequeue list, String keyword) {
        ArrayList<Issue> sortList = new ArrayList<>();
        while (!list.isEmpty()) {
            sortList.add(list.poll());
        }
        Comparator<Issue> similarity = new Comparator<>() {
            @Override
            public int compare(Issue s1, Issue e2) {
                if (matchScore(s1.getComments().toString(), keyword) > matchScore(e2.getComments().toString(), keyword)) {
                    return -1;
                } else if (matchScore(s1.getComments().toString(), keyword) < matchScore(e2.getComments().toString(), keyword)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(sortList, similarity);
        for (int i = 0; i < sortList.size(); i++) {
            list.offer(sortList.get(i));
        }
        return list;
    }
}