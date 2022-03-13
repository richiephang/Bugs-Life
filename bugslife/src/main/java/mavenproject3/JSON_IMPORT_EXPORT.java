package mavenproject3;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author richi
 */
public class JSON_IMPORT_EXPORT {

    private static PreparedStatement pS;

    private static ResultSet result;

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("hibernateTest");

    public static void importExport() throws IOException {
        Scanner in = new Scanner(System.in);
        while (true) {

            try {
                System.out.println("Enter '1' to import JSON to MySQL Database, '2' to export data from MySQL to JSON file");
                int input = in.nextInt();
                in.nextLine(); //buffer
                if (input == 1) {
                    System.out.println("Enter video's file path : ");
                    System.out.println("eg: C:/Users/downloads/data.json");
                    String filepath = in.nextLine();
                    importJson(filepath);
                    break;
                } else if (input == 2) {
                    System.out.println("Enter video's file path : ");
                    System.out.println("eg: C:/Users/downloads/data.json");
                    String filepath = in.nextLine();
                    exportJson(filepath);
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid value!\n");
                in.next();
            }
        }
    }

    public static void exportJson(String filepath) throws IOException {

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
        List<changelog> listC = new ArrayList<>();
        List<adminlog> listA = new ArrayList<>();
       
        try {
            // Get matching project object and output
            projectList = tq.getResultList();
            // Get matching issue object and output
            listU = tq2.getResultList();
            try {
                Connection changelogSQL = new Connection();

                String searchChangeLOG = "SELECT * FROM `changelog`";
                pS = changelogSQL.getConnection().prepareStatement(searchChangeLOG);
                result = pS.executeQuery();
                while (result.next()) {
                    int changelog_id = result.getInt("changelog_id");
                    String edit_time = result.getString("edit_time");
                    String edittor = result.getString("edittor");
                    String projectName = result.getString("project_name");
                    int projectID = result.getInt("project_id");
                    String issueName = result.getString("issue_name");
                    int issueID = result.getInt("issue_id");
                    String detail = result.getString("detail");
                    listC.add(new changelog(changelog_id, projectName, projectID, issueName, issueID, detail, edit_time, edittor));

                }
                changelogSQL.getConnection().close();
            } catch (SQLException ex) {
                System.out.println("export changelog error");
            }
            try {
                Connection changelogSQL = new Connection();

                String searchAdminLog = "SELECT * FROM `adminlog`";
                pS = changelogSQL.getConnection().prepareStatement(searchAdminLog);
                result = pS.executeQuery();
                while (result.next()) {
                    int adminlog_id = result.getInt("adminlog_id");
                    String username = result.getString("username");
                    String timestamp = result.getString("timestamp");
                    String status = result.getString("status");
                    String reason = result.getString("reason");
                    listA.add(new adminlog(adminlog_id,username,timestamp,status,reason));

                }
                changelogSQL.getConnection().close();
            } catch (SQLException ex) {
                System.out.println("export adminlog error");
            }
            ObjectMapper mapper = new ObjectMapper();

            Example root = new Example();
            root.setProjects(projectList);
            root.setUsers(listU);
            root.setChangelogList(listC);
            root.setAdminlogList(listA);
            String json = mapper.writeValueAsString(root);
            try (FileWriter file = new FileWriter(filepath)) {

                file.write(json);
                System.out.println("Successfully updated json object to file...!!");
            }
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

    }

    public static void importJson(String filepath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //read file
        Example root = mapper.readValue(new File(filepath), Example.class);
        int sizeProject = root.getProjects().size();
        for (int i = 0; i < sizeProject; i++) {
            int sizeIssue = root.getProjects().get(i).getIssues().size();
            for (int j = 0; j < sizeIssue; j++) {
                String title = root.getProjects().get(i).getIssues().get(j).getTitle();
                String ctime = root.getProjects().get(i).getIssues().get(j).changeDateFormat();
                String cname = root.getProjects().get(i).getIssues().get(j).getCreatedBy();
                String allowedEdittor = cname;
                String viewer = "All User";
                String aname = root.getProjects().get(i).getIssues().get(j).getAssignee();
                String changeStatus = "";
                if (aname.equals("") || aname.equals(" ")) {
                    changeStatus = cname;
                } else {
                    changeStatus = aname + " & " + cname;
                }
                Connection userSQL = new Connection();
                try {

                    //? is unspecified value, to substitute in an integer, string, double or blob value.
                    String register = "INSERT INTO access_control (creator,issueTitle,timestamp,allowed_edittor,allowed_viewer,allowed_changeStatus) VALUES(?,?,?,?,?,?)";

                    //insert record of register 
                    pS = userSQL.getConnection().prepareStatement(register);

                    // create the mysql insert preparedstatement
                    //.setString : placeholders that are only replaced with the actual values inside the system
                    pS.setString(1, cname);
                    pS.setString(2, title);
                    pS.setString(3, ctime);
                    pS.setString(4, allowedEdittor);
                    pS.setString(5, viewer);
                    pS.setString(6, changeStatus);

                    pS.executeUpdate(); //return int value

                    System.out.println("Access Control List update successfully ");

                } catch (SQLException e) {
                    System.out.println("Failed to update Access COntrol List. Try again!");
                }
            }
        }
        List<Project> projectList = root.getProjects();
        List<User> userList = root.getUsers();
        List<changelog> cList = root.getChangelogList();
        List<adminlog> aList = root.getAdminlogList();
        Connection userSQL = new Connection();
        for (int i = 0; i < cList.size(); i++) {
            try {

                //? is unspecified value, to substitute in an integer, string, double or blob value.
                String register = "INSERT INTO changelog (project_name,project_id,issue_name,issue_id,detail,edit_time,edittor) VALUES(?, ?,?,?,?,?,?)";

                //insert record of register 
                pS = userSQL.getConnection().prepareStatement(register);

                // create the mysql insert preparedstatement
                //.setString : placeholders that are only replaced with the actual values inside the system
                pS.setString(1, cList.get(i).getProjectName());
                pS.setInt(2, cList.get(i).getProjectId());
                pS.setString(3, cList.get(i).getIssueName());
                pS.setInt(4, cList.get(i).getIssueId());
                pS.setString(5, cList.get(i).getDetail());
                pS.setString(6, cList.get(i).getEditTime());
                pS.setString(7, cList.get(i).getEdittor());

                pS.executeUpdate(); //return int value

                System.out.println("change log update (assignee) Successfully ");

            } catch (SQLException e) {
                System.out.println("Failed to update changelog (assignee). Try again!");
            }
        }
        for (int i = 0; i < aList.size(); i++) {
            try {

                //? is unspecified value, to substitute in an integer, string, double or blob value.
                String register = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                //insert record of register 
                pS = userSQL.getConnection().prepareStatement(register);

                // create the mysql insert preparedstatement
                //.setString : placeholders that are only replaced with the actual values inside the system
                pS.setString(1, aList.get(i).getUsername());
                pS.setString(2, aList.get(i).getTimestamp());
                pS.setString(3, aList.get(i).getStatus());
                pS.setString(4, aList.get(i).getReason());

                pS.executeUpdate(); //return int value

                System.out.println("change log update (assignee) Successfully ");

            } catch (SQLException e) {
                System.out.println("Failed to update changelog (assignee). Try again!");
            }
        }

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        // Used to issue transactions on the EntityManager
        EntityTransaction et = null;

        try {
            // Get transaction and start
            et = em.getTransaction();
            et.begin();
            projectList.forEach(project -> project.getIssues().forEach(issue -> {
                issue.setProject(project);
                issue.getComments().forEach(comment -> {
                    comment.setIssue(issue);
                    comment.getReact().forEach(react -> react.setComment(comment));
                    comment.setCommentId(null);
                });
                issue.setId(null);
            }));
            projectList.forEach(project -> {
                project.setId(null);
                em.persist(project);
            });
            userList.forEach(user -> {
                user.setUserid(null);
                em.persist(user);
            });

            // Save the project object
            et.commit();
        } catch (Exception ex) {
            // If there is an exception rollback changes
            if (et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        } finally {
            // Close EntityManager
            System.out.println("json data imported into database");
            em.close();
        }

    }
}
