package mavenproject3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "issues"
})

@Entity
@Table(name = "project_table")
// Project class that implements Serializable library.
public class Project implements Serializable {
    // instant variables
    @JsonIgnore
    @Transient
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("hibernateTest");
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    @JsonProperty("id")
    private Integer id;

    @Column(name = "project_name")
    @JsonProperty("name")
    private String name;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonProperty("issues")
    private List<Issue> issues = new ArrayList<>();

    @Transient
    @JsonIgnore
    private static int projectID;

    // empty constructor
    public Project() {
    
    }
    
    // constructor for reading data.json
    @JsonCreator
    public Project(@JsonProperty("id") Integer id, @JsonProperty("name") String name, @JsonProperty("issues") ArrayList<Issue> issues) {
        this.id = id;
        this.name = name;
        this.issues = issues;
    }

    // constructor for user input data
    public Project(String name) {
        this.name = name;
        this.issues = issues;
    }

    // -----accessor & mutator-----
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("issues")
    public List<Issue> getIssues() {
        return issues;
    }

    @JsonProperty("issues")
    public void setIssues(ArrayList<Issue> issues) {
        this.issues = issues;
    }

    public static int getProjectID() {
        return projectID;
    }

    public static void setProjectID(int projectID) {
        Project.projectID = projectID;
    }

    // display project dashboard
    public static void displayProject() throws IOException, ParseException {
        Example e = new Example();
        Scanner in = new Scanner(System.in);
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        // the lowercase c refers to the object
        // :ID is a parameterized query thats value is set below
        String strQuery = "SELECT c FROM Project c WHERE c.id IS NOT NULL";

        // Issue the query and get a matching Project
        TypedQuery<Project> tq = em.createQuery(strQuery, Project.class);
        List<Project> a = new ArrayList<>();
        try {
            // Get matching project object and output
            a = tq.getResultList();
            e.addProject(a);
            System.out.println("Enter 'a' to sort Projects by alphanumerical \nor 'i' to sort by Project ID: ");
            String sortMethod = in.next();
            switch (sortMethod) {
                case "a":
                    e.sortAplhanumerically(a);
                    break;
                case "i":
                    e.sortProjectID(a);
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
                    System.out.println();
                    displayProject();
            }
            project();
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

    }

    // provide action for project dashboard
    public static void project() throws IOException, ParseException {
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("Do you want create project or check project? ");
            System.out.println("Enter '0' to create new project.\nEnter 'id' to check project\nEnter '-1' to logout");
            int input = in.nextInt();
            if (input == 0) {
                addProjectByUser();
            }else if (input == -1) {
                User.logout();
            } else {
                Project.setProjectID(input);
                Issue.displayIssueBoard();
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please try again.");
            System.out.println("");
            project();
        } catch (IndexOutOfBoundsException a) {
            System.out.println("Invalid input. Please try again.");
            System.out.println("");
            project();
        }
    }

    // allow user to create a new project
    public static void addProjectByUser() throws IOException, ParseException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter project name: ");
        String projectName = input.nextLine();

        // The EntityManager class allows operations such as create, read, update, delete
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        // Used to issue transactions on the EntityManager
        EntityTransaction et = null;

        try {
            // Get transaction and start
            et = em.getTransaction();
            et.begin();

            // Create and set values for new project
            Project c = new Project(projectName);

            // Save the project object
            em.persist(c);
            et.commit();
        } catch (Exception ex) {
            // If there is an exception rollback changes
            if (et != null) {
                et.rollback();
            }
            ex.printStackTrace();
        } finally {
            // Close EntityManager
            System.out.println("New project added into database");
            em.close();
        }
        displayProject();
    }
}