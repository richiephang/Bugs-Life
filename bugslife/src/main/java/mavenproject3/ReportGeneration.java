package mavenproject3;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;


public class ReportGeneration {

    public static long numResolved;
    public static long numInProgress;
    public static long numOpen;
    public static long numClose;
    public static Map<String, Long> occurrences;
    public static Map<String, Long> occurrencesDate;
    public static Map<String, Long> occurrencesDateRange;
    public static Date input1 = new Date();
    public static Date input2 = new Date();

    public static Date getInput1() {
        return input1;
    }

    public static void setInput1(Date input1) {
        ReportGeneration.input1 = input1;
    }

    public static Date getInput2() {
        return input2;
    }

    public static void setInput2(Date input2) {
        ReportGeneration.input2 = input2;
    }

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("hibernateTest");

    public static void generateReport() throws ParseException, IOException {
        Scanner input = new Scanner(System.in);     

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        // the lowercase c refers to the object
        // :ID is a parameterized query thats value is set below
        String strQuery = "SELECT c FROM Project c WHERE c.id IS NOT NULL";
        String strQuery2 = "SELECT c FROM User c WHERE c.userid IS NOT NULL";

        // Issue the query and get a matching Project
        TypedQuery<Project> tq = em.createQuery(strQuery, Project.class);
        // Issue the query and get a matching User
        TypedQuery<User> tq2 = em.createQuery(strQuery2, User.class);
        List<Project> projectList = new ArrayList<>();
        List<User> listU = new ArrayList<>();
        try {
            // Get matching project object and output
            projectList = tq.getResultList();
            // Get matching user object and output
            listU = tq2.getResultList();
            ArrayList<String> usersName = new ArrayList<>();
            ArrayList<String> outer = new ArrayList<>();
            //store user name as a list
            for (int i = 0; i < listU.size(); i++) {
                usersName.add(listU.get(i).getUsername());
            }

            //array of number of issue solved, each index here is same with username arraylist index
            long[] numSolvedbyAssignee = new long[usersName.size()];
            int[] numIssue = new int[7];

            //list of all timestamps
            ArrayList<Integer> all = new ArrayList<>();

            //list of timestamps in stated timerange
            for (int i = 0; i < projectList.size(); i++) {
                numResolved += findWeeklyStatus(projectList.get(i).getIssues(), "Resolved");
                numInProgress += findWeeklyStatus(projectList.get(i).getIssues(), "In Progress");
                numOpen += findWeeklyStatus(projectList.get(i).getIssues(), "Open");
                numClose += findWeeklyStatus(projectList.get(i).getIssues(), "Close");
                all.addAll(findIssuePerDay(projectList.get(i).getIssues()));
                findTag(outer, projectList.get(i).getIssues());


                //loop through each issue, find number of issue solved by each user, store it in array 
                for (int j = 0; j < usersName.size(); j++) {
                    numSolvedbyAssignee[j] += findTopPerformer(projectList.get(i).getIssues(), usersName.get(j), "Resolved");
                    //System.out.println("**"+findTopPerformer(projectList.get(i).getIssues(), usersName.get(j), "Resolved"));
                }

            }

            Collections.sort(all);

            ArrayList<String> dates = new ArrayList<>();
            all.stream().forEach(allObj -> dates.add(changeDateFormat2(new Date((long) allObj * 1000))));

            //find the index of highest solved , then the use the index to determine the top performer
            long max = numSolvedbyAssignee[0];
            int maxIndex = 0;
            for (int i = 1; i < numSolvedbyAssignee.length; i++) {
                if (numSolvedbyAssignee[i] > max) {
                    max = numSolvedbyAssignee[i];
                    maxIndex = i;
                }
            }

            //report output
            Calendar c = Calendar.getInstance();
            c.setFirstDayOfWeek(Calendar.MONDAY);
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            Date monday = c.getTime();
            long numUnresolved = numOpen + numInProgress;
            System.out.println("\n***************Weekly Report***************");
            System.out.println("From " + changeDateFormat(monday) + " to " + changeDateFormat(new Date()) + "\n*******************************************\n");
            System.out.println("Number of resolved issues: " + numResolved);
            System.out.println("Number of In Progress issues: " + numInProgress);
            System.out.println("Number of unresolved issues: " + numUnresolved);
            if (max == 0) {
                System.out.println("Top performer of the week: nobody");
            } else {
                System.out.println("Top performer of the week: " + usersName.get(maxIndex));
            }

            occurrencesDate = dates.stream().collect(Collectors.groupingBy(w -> w, (Supplier<LinkedHashMap<String, Long>>) LinkedHashMap::new, Collectors.counting()));
           
            if (outer.isEmpty()) {
                System.out.println("Most frequent label of the week: no label found");
                occurrences = outer.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));
            } else {
                occurrences = outer.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));

                long maxKey = Collections.max(occurrences.values());
                List<String> keys = new ArrayList<>();
                for (Entry<String, Long> entry : occurrences.entrySet()) {
                    if (entry.getValue() == maxKey) {
                        keys.add(entry.getKey());
                    }
                }
                System.out.print("Most frequent label of the week: ");
                for (int i = 0; i < keys.size(); i++) {
                    System.out.print("\"" + keys.get(i) + "\" ");
                }
            }
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
        System.out.println("");
        while (true) {
            try {
                System.out.println("\nEnter '1' to show Statistic of Status Graph\nOr '2' to show Statistic of Tag Graph\n'3' to Issue Frequency\n'4' to show Issue Frequency for Selected Time Range\n'5' to back to admin page");
                int userInput = input.nextInt();
                if (userInput == 1) {
                    StatusGraph.GraphStatus();
                } else if (userInput == 2) {
                    TagGraph.Graphtag();
                } else if (userInput == 3) {
                    IssueFrequencyGraph.GraphIssueF();
                } else if (userInput == 4) {
                    SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
                    input.nextLine();
                    System.out.println("Enter check-in date (dd mm yyyy):");
                    String cindate = input.nextLine();
                    System.out.println("Enter check-out date (dd mm yyyy):");
                    String coutdate = input.nextLine();
                    if (null != cindate && cindate.trim().length() > 0 && null != coutdate && coutdate.trim().length() > 0) {
                        input1 = myFormat.parse(cindate);
                        input2 = myFormat.parse(coutdate);
                    }

                    em = ENTITY_MANAGER_FACTORY.createEntityManager();

                    // the lowercase c refers to the object
                    // :ID is a parameterized query thats value is set below
                    strQuery = "SELECT c FROM Project c WHERE c.id IS NOT NULL";
                    strQuery2 = "SELECT c FROM User c WHERE c.userid IS NOT NULL";

                    // Issue the query and get a matching Project
                    tq = em.createQuery(strQuery, Project.class);
                    // Issue the query and get a matching User
                    tq2 = em.createQuery(strQuery2, User.class);
                    projectList = new ArrayList<>();
                    listU = new ArrayList<>();
                    try {
                        // Get matching project object and output
                        projectList = tq.getResultList();
                        // Get matching user object and output
                        listU = tq2.getResultList();
                        ArrayList<String> usersName = new ArrayList<>();
                        ArrayList<String> outer = new ArrayList<>();
                        //store user name as a list
                        for (int i = 0; i < listU.size(); i++) {
                            usersName.add(listU.get(i).getUsername());
                        }

                        //array of number of issue solved, each index here is same with username arraylist index
                        long[] numSolvedbyAssignee = new long[usersName.size()];
                        int[] numIssue = new int[7];

                        //list of all timestamps
                        ArrayList<Integer> all = new ArrayList<>();

                        //list of timestamps in stated timerange
                        ArrayList<Integer> range = new ArrayList<>();

                        for (int i = 0; i < projectList.size(); i++) {
                            range.addAll(findIssuerPerTimeRange(projectList.get(i).getIssues(), input1,input2));
                        }

                        Collections.sort(range);
                        ArrayList<String> datesRange = new ArrayList<>();
                        range.stream().forEach(allObj -> datesRange.add(changeDateFormat2(new Date((long) allObj * 1000))));

                        occurrencesDateRange = datesRange.stream().collect(Collectors.groupingBy(w -> w, (Supplier<LinkedHashMap<String, Long>>) LinkedHashMap::new, Collectors.counting()));
                    } catch (NoResultException ex) {
                        ex.printStackTrace();
                    } finally {
                        em.close();
                    }
                    IssuebyTimeRangeGraph.GraphIssueFTR();
                } else if (userInput == 5) {
                    tester.adminHomepage();
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid value!\n");
                input.next();
            }
        }

    }

    public static long findStatus(List<Issue> list, String status) {
        return list.stream().filter(listObj -> status.equals(listObj.getStatus())).count();
    }

    public static long findWeeklyStatus(List<Issue> list, String status) {
        return list.stream().filter(listObj -> status.equals(listObj.getStatus()) && (checkThisWeek(listObj.getStatusTimestamp()))).count();
    }

    public static long findTopPerformer(List<Issue> list, String usersname, String status) {
        return list.stream().filter(listObj -> status.equals(listObj.getStatus()) && (listObj.getAssignee().equals(usersname)) && (checkThisWeek(listObj.getStatusTimestamp()))).count();
    }

    public static void findTag(List<String> outer, List<Issue> list) {
        list.stream().filter(listObj -> checkThisWeek(listObj.getTimestamp())).forEach(listObj -> outer.addAll(listObj.getTag()));
    }

    public static ArrayList<Integer> findIssuePerDay(List<Issue> list) {
        ArrayList<Integer> timestamps = new ArrayList<>();
        list.stream().filter(listObj -> checkThisWeek(listObj.getTimestamp())).forEach(listObj -> timestamps.add(listObj.getTimestamp()));
        return timestamps;
    }

    public static ArrayList<Integer> findIssuerPerTimeRange(List<Issue> list, Date input1, Date input2) {
        ArrayList<Integer> timestamps = new ArrayList<>();
        list.stream().filter(listObj -> checkTimeRange(listObj.getTimestamp(), input1, input2)).forEach(listObj -> timestamps.add(listObj.getTimestamp()));
        return timestamps;
    }

    public static boolean checkTimeRange(Integer issueTimeStamp, Date input1, Date input2) {
        Date timestamp = new Date((long) issueTimeStamp * 1000);
        return timestamp.after(input1) && timestamp.before(input2);
    }

    public static boolean checkThisWeek(long unixtimestamp) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date monday = c.getTime();
        Date nextMonday = new Date(monday.getTime() + 7 * 24 * 60 * 60 * 1000);
        Date today = new Date((long) unixtimestamp * 1000);

        return today.after(monday) && today.before(nextMonday);
    }

    public static String changeDateFormat(Date a) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        return ft.format(a);
    }

    public static String changeDateFormat2(Date a) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        return ft.format(a);
    }

}
