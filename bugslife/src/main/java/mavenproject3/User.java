package mavenproject3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Scanner;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userid",
    "username",
    "password"
})
@Entity
@Table(name = "user")
// User class that implements Serializable library
public class User implements Serializable {

    //instant variables
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ID")
    @JsonProperty("userid")
    private Integer userid;

    @Column(name = "username")
    @JsonProperty("username")
    private String username;

    @Column(name = "password")
    @JsonProperty("password")
    private String password;

    @Transient
    @JsonIgnore
    public static String loginName;

    @Transient
    @JsonIgnore
    public static boolean login_status = false;

    @Transient
    @JsonIgnore
    public static int numberSolved;

    @JsonIgnore
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("hibernateTest");

    @Transient
    @JsonIgnore
    private static PreparedStatement pS;

    // empty constructor
    public User() {
    }

    // constructor for reading data.json
    @JsonCreator
    public User(@JsonProperty("userid") Integer userid, @JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.userid = userid;
        this.username = username;
        this.password = password;
    }

    // constructor for user input data
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // -----accessor & mutator-----
    @JsonProperty("userid")
    public Integer getUserid() {
        return userid;
    }

    @JsonProperty("userid")
    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public static String getLoginName() {
        return loginName;
    }

    public static void setLoginName(String loginName) {
        User.loginName = loginName;
    }

    public static boolean isLogin_status() {
        return login_status;
    }

    public static void setLogin_status(boolean login_status) {
        User.login_status = login_status;
    }

    public static int getNumberSolved() {
        return numberSolved;
    }

    public static void setNumberSolved(int numberSolved) {
        User.numberSolved = numberSolved;
    }

    // allow user to register
    public static void register() throws IOException {

        Scanner in = new Scanner(System.in);

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        String strQuery = "SELECT c FROM User c WHERE c.userid IS NOT NULL";

        // Issue the query and get a matching User
        TypedQuery<User> tq = em.createQuery(strQuery, User.class);
        List<User> userList = new ArrayList<>();
        try {
            // Get matching user objects
            userList = tq.getResultList();

        } catch (NoResultException ex) {
            ex.printStackTrace();
        } finally {
            //em.close();
        }

        System.out.println("Enter username: ");
        String username = in.nextLine();

        while (true) {
            //obtain the first object from userlist with same username
            User obj = findUsername(userList, username);

            if (userList.contains(obj)) {
                System.out.println("Username has been taken, please input new username: ");
                username = in.next();
                java.util.Date date = new java.util.Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                String newtimestamp = ft.format(date);
                String status = "Register Failed";
                String reason = "Username has been taken";
                Connection userSQL = new Connection();
                try {

                    //? is unspecified value, to substitute in an integer, string, double or blob value.
                    String register = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                    //insert record of register 
                    pS = userSQL.getConnection().prepareStatement(register);

                    // create the mysql insert preparedstatement
                    //.setString : placeholders that are only replaced with the actual values inside the system
                    pS.setString(1, username);
                    pS.setString(2, newtimestamp);
                    pS.setString(3, status);
                    pS.setString(4, reason);
                    pS.executeUpdate();
                    System.out.println("Admin Log update successfully ");
                } catch (SQLException e) {
                    System.out.println("Failed to update Admin Log. Try again!");
                }
            } else {
                System.out.println("Enter password: ");
                String password = in.next();

                // Create and set values for new user
                User b = new User(username, password);

                // The EntityManager class allows operations such as create, read, update, delete
                // Used to issue transactions on the EntityManager
                EntityTransaction et = null;

                try {
                    // Get transaction and start
                    et = em.getTransaction();
                    et.begin();

                    // Save the user object
                    em.persist(b);
                    et.commit();
                } catch (Exception ex) {
                    // If there is an exception rollback changes
                    if (et != null) {
                        et.rollback();
                    }
                    ex.printStackTrace();
                } finally {
                    java.util.Date date = new java.util.Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                    String newtimestamp = ft.format(date);
                    String status = "Register successfully";
                    String reason = "-";
                    Connection userSQL = new Connection();
                    try {

                        //? is unspecified value, to substitute in an integer, string, double or blob value.
                        String register = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                        //insert record of register 
                        pS = userSQL.getConnection().prepareStatement(register);

                        // create the mysql insert preparedstatement
                        //.setString : placeholders that are only replaced with the actual values inside the system
                        pS.setString(1, username);
                        pS.setString(2, newtimestamp);
                        pS.setString(3, status);
                        pS.setString(4, reason);

                        pS.executeUpdate(); //return int value

                        System.out.println("Admin Log update successfully ");

                    } catch (SQLException e) {
                        System.out.println("Failed to update Admin Log. Try again!");
                    }
                    System.out.println("Successfully registered into database.");
                    // Close EntityManager
                    em.close();
                }
                break;
            }
        }
    }

    // allows user to login 
    public static void login() throws IOException, ParseException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username: ");
        String username = input.nextLine();
        System.out.println("Enter password: ");
        String password = input.nextLine();
        ObjectMapper objM = new ObjectMapper();

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        // the lowercase c refers to the object
        // :ID is a parameterized query thats value is set below
        String strQuery = "SELECT c FROM User c WHERE c.userid IS NOT NULL";

        // Issue the query and get a matching User
        TypedQuery<User> tq = em.createQuery(strQuery, User.class);
        List<User> userList = new ArrayList<>();
        try {
            // Get matching user objects
            userList = tq.getResultList();

        } catch (NoResultException ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }

        //obtain the first object from userlist with same username
        User obj = findUsername(userList, username);

        if (userList.contains(obj)) {
            if (obj.getPassword().equals(password)) {
                System.out.println("Welcome back " + userList.get(userList.indexOf(obj)).getUsername());
                setLogin_status(true);
                setLoginName(userList.get(userList.indexOf(obj)).getUsername());
                java.util.Date date = new java.util.Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                String newtimestamp = ft.format(date);
                String status = "Login successfully";
                String reason = "-";
                Connection userSQL = new Connection();
                try {

                    //? is unspecified value, to substitute in an integer, string, double or blob value.
                    String login = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                    //insert record of login
                    pS = userSQL.getConnection().prepareStatement(login);

                    // create the mysql insert preparedstatement
                    //.setString : placeholders that are only replaced with the actual values inside the system
                    pS.setString(1, userList.get(userList.indexOf(obj)).getUsername());
                    pS.setString(2, newtimestamp);
                    pS.setString(3, status);
                    pS.setString(4, reason);
                    pS.executeUpdate();

                    System.out.println("Admin Log update successfully ");
                } catch (SQLException e) {
                    System.out.println("Failed to update Admin Log. Try again!");
                }
            } else {
                int tries = 1;
                while (!obj.getPassword().equals(password)) {
                    System.out.println("Incorrect password");
                    tries++;
                    if (tries <= 3 && tries > 0) {
                        java.util.Date date = new java.util.Date();
                        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                        String newtimestamp = ft.format(date);
                        String status = "Login failed";
                        String reason = "Incorrect Password";
                        Connection userSQL = new Connection();
                        try {

                            //? is unspecified value, to substitute in an integer, string, double or blob value.
                            String login = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                            //insert record of register 
                            pS = userSQL.getConnection().prepareStatement(login);

                            // create the mysql insert preparedstatement
                            //.setString : placeholders that are only replaced with the actual values inside the system
                            pS.setString(1, userList.get(userList.indexOf(obj)).getUsername());
                            pS.setString(2, newtimestamp);
                            pS.setString(3, status);
                            pS.setString(4, reason);
                            pS.executeUpdate();

                            System.out.println("Admin Log update successfully ");
                        } catch (SQLException e) {
                            System.out.println("Failed to update Admin Log. Try again!");
                        }
                    }
                    System.out.println("Enter password: ");
                    password = input.nextLine();
                    if (obj.getPassword().equals(password)) {
                        java.util.Date date = new java.util.Date();
                        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                        String newtimestamp = ft.format(date);
                        String status = "Login successfully";
                        String reason = "-";
                        Connection userSQL = new Connection();
                        try {

                            //? is unspecified value, to substitute in an integer, string, double or blob value.
                            String login = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                            //insert record of login
                            pS = userSQL.getConnection().prepareStatement(login);

                            // create the mysql insert preparedstatement
                            //.setString : placeholders that are only replaced with the actual values inside the system
                            pS.setString(1, userList.get(userList.indexOf(obj)).getUsername());
                            pS.setString(2, newtimestamp);
                            pS.setString(3, status);
                            pS.setString(4, reason);
                            pS.executeUpdate();

                            System.out.println("Admin Log update successfully ");
                        } catch (SQLException e) {
                            System.out.println("Failed to update Admin Log. Try again!");
                        }
                        System.out.println("Welcome back " + userList.get(userList.indexOf(obj)).getUsername());
                        setLogin_status(true);
                        setLoginName(userList.get(userList.indexOf(obj)).getUsername());
                        tries--;
                    } else {
                        if (tries > 2) {
                            java.util.Date date = new java.util.Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                            String newtimestamp = ft.format(date);
                            String status = "Login abnormal";
                            String reason = "Incorrect password, number of tries = " + tries;
                            Connection userSQL = new Connection();
                            try {

                                //? is unspecified value, to substitute in an integer, string, double or blob value.
                                String login = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                                //insert record of login
                                pS = userSQL.getConnection().prepareStatement(login);

                                // create the mysql insert preparedstatement
                                //.setString : placeholders that are only replaced with the actual values inside the system
                                pS.setString(1, username);
                                pS.setString(2, newtimestamp);
                                pS.setString(3, status);
                                pS.setString(4, reason);
                                pS.executeUpdate();

                                System.out.println("Admin Log update successfully ");
                                System.out.println("Sorry, three attempts for login failed, please enter username & password again.");
                                tester.homepage();
                                break;
                            } catch (SQLException e) {
                                System.out.println("Failed to update Admin Log. Try again!");
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("username not found, Please register");
            java.util.Date date = new java.util.Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            String newtimestamp = ft.format(date);
            String status = "Login failed";
            String reason = "Username not found";
            Connection userSQL = new Connection();
            try {

                //? is unspecified value, to substitute in an integer, string, double or blob value.
                String login = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

                //insert record of login
                pS = userSQL.getConnection().prepareStatement(login);

                // create the mysql insert preparedstatement
                //.setString : placeholders that are only replaced with the actual values inside the system
                pS.setString(1, username);
                pS.setString(2, newtimestamp);
                pS.setString(3, status);
                pS.setString(4, reason);
                pS.executeUpdate();

                System.out.println("Admin Log update successfully ");
            } catch (SQLException e) {
                System.out.println("Failed to update Admin Log. Try again!");
            }
            Register();
        }
    }

    // recursion method for register if invalid input
    public static void Register() throws IOException, ParseException {
        Scanner input = new Scanner(System.in);
        System.out.println("Do you want register ? Enter 'y' if yes, 'n' if no");
        String Doregister = input.next();
        if (Doregister.equals("y")) {
            register();
        } else if (Doregister.equals("n")) {
            tester.homepage();
        } else if (!Doregister.equals("y") && !Doregister.equals("n")) {
            System.out.println("Invalid input. Please try again");
            System.out.println("");
            Register();
        }
    }

    // allow user to logout
    public static void logout() throws IOException, ParseException {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        String newtimestamp = ft.format(date);
        String status = "Logout successfully";
        String reason = "-";
        Connection userSQL = new Connection();
        try {

            //? is unspecified value, to substitute in an integer, string, double or blob value.
            String logout = "INSERT INTO adminlog (username,timestamp,status,reason) VALUES(?,?,?,?)";

            //insert record of logout 
            pS = userSQL.getConnection().prepareStatement(logout);

            // create the mysql insert preparedstatement
            //.setString : placeholders that are only replaced with the actual values inside the system
            pS.setString(1, User.getLoginName());
            pS.setString(2, newtimestamp);
            pS.setString(3, status);
            pS.setString(4, reason);
            pS.executeUpdate();

            System.out.println("Admin Log update successfully ");
        } catch (SQLException e) {
            System.out.println("Failed to update Admin Log. Try again!");
        }
        System.out.println("Good bye " + User.getLoginName() + ", we hope to see you again soon.");
        setLogin_status(false);
        setLoginName(null);
        tester.homepage();
    }

    // check either input username exists in the username list
    public static User findUsername(List<User> list, String username) {
        return list.stream().filter(userObj -> username.equals(userObj.getUsername())).findFirst().orElse(null);
    }
}