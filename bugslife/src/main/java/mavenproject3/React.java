package mavenproject3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.persistence.*;
import javax.transaction.Transactional;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reaction",
    "count"
})
@Transactional
@Entity
@Table(name = "reaction")
// React class that implements Serializable library
public class React implements Serializable {

    // instant variables
    @JsonIgnore    // Create an EntityManagerFactory when you start the application
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("hibernateTest");

    @JsonIgnore
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "react_id")
    @JsonIgnore
    private int react_id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "reaction")
    @JsonProperty("reaction")
    private String reaction;

    @Column(name = "count")
    @JsonProperty("count")
    private Integer count;

    // empty constructor
    public React() {
    }

    // constructor for user input data
    @JsonCreator
    public React(@JsonProperty("reaction") String reaction, @JsonProperty("count") Integer count) {
        this.reaction = reaction;
        this.count = count;
    }

    // -----accessor & mutator-----
    @JsonProperty("reaction")
    public String getReaction() {
        return reaction;
    }

    @JsonProperty("reaction")
    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    @JsonProperty("count")
    public Integer getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(Integer count) {
        this.count = count;
    }

    public int getReact_id() {
        return react_id;
    }

    public void setReact_id(int react_id) {
        this.react_id = react_id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    // toString method
    @Override
    public String toString() {
        return "React{" + "reaction=" + getReaction() + ", count=" + getCount() + '}';
    }

    // allow user to add reaction
    public static void addReact() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a comment ID to react: ");
        Comment.setCommentID(input.nextInt());
        String username = User.getLoginName();
        int reactionIndex = 0;
        input.nextLine();
        System.out.println("Which reaction you want to reaction ?");
        System.out.println("Enter '1'--angry '2'--happy '3'--thumb up '4'--smile '5'--sad '6'--love '7'--cry");
        int reaction = input.nextInt();
        switch (reaction) {
            case 1:
                reactionIndex = 0;
                break;
            case 2:
                reactionIndex = 1;
                break;
            case 3:
                reactionIndex = 2;
                break;

            case 4:
                reactionIndex = 3;
                break;
            case 5:
                reactionIndex = 4;
                break;
            case 6:
                reactionIndex = 5;
                break;
            case 7:
                reactionIndex = 6;
                break;
        }

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction et = null;

        // the lowercase c refers to the object
        // :ID is a parameterized query thats value is set below
        String strQuery = "SELECT c FROM Project c WHERE c.id IS NOT NULL";
        String strQuery2 = "SELECT c FROM Issue c WHERE c.id IS NOT NULL";

        // Issue the query and get a matching Project
        TypedQuery<Project> tq = em.createQuery(strQuery, Project.class);
        // Issue the query and get a matching Issue
        TypedQuery<Issue> tq2 = em.createQuery(strQuery2, Issue.class);
        List<Project> projectList = new ArrayList<>();
        List<Issue> issueList = new ArrayList<>();

        try {
            // Get matching project object, issue object and output
            projectList = tq.getResultList();
            issueList = tq2.getResultList();

        } catch (NoResultException ex) {
            ex.printStackTrace();
        }

        React r = new React();

        try {
            // Get transaction and start
            et = em.getTransaction();
            et.begin();
            int count = issueList.get(Issue.getIssueID() - 1).getComments().get(Comment.getCommentID() - 1).getReact().get(reactionIndex).getCount();
            r = issueList.get(Issue.getIssueID() - 1).getComments().get(Comment.getCommentID() - 1).getReact().get(reactionIndex);
            r.setCount(count + 1);
            em.persist(r);
            et.commit();
        } catch (Exception ex) {
            // If there is an exception rollback changes
            if (et != null) {
                et.rollback();
            }
            System.out.println("react sql error");
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}