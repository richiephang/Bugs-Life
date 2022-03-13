package mavenproject3;

import java.sql.*;

public class Connection {

    protected static java.sql.Connection con;

    public java.sql.Connection getConnection() {
        try {
            //    Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bugs_life", "root", "");
            return con;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
}
