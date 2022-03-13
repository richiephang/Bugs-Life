package mavenproject3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "adminlog_id",
    "username",
    "timestamp",
    "status",
    "reason"
})

public class adminlog {

    @JsonProperty("adminlog_id")
    private Integer adminlogId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("status")
    private String status;
    @JsonProperty("reason")
    private String reason;

    @JsonIgnore
    private static PreparedStatement pS;
    @JsonIgnore
    private static ResultSet result;

    public adminlog() {
    }

    @JsonCreator //constructor for json read and write
    public adminlog(@JsonProperty("adminlog_id") Integer adminlogId, @JsonProperty("username") String username, @JsonProperty("timestamp") String timestamp, @JsonProperty("status") String status, @JsonProperty("reason") String reason) {
        this.adminlogId = adminlogId;
        this.username = username;
        this.timestamp = timestamp;
        this.status = status;
        this.reason = reason;
    }

    //getter and setters
    @JsonProperty("adminlog_id")
    public Integer getAdminlogId() {
        return adminlogId;
    }

    @JsonProperty("adminlog_id")
    public void setAdminlogId(Integer adminlogId) {
        this.adminlogId = adminlogId;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    //method to display admin log (for tracing abnormal activities)
    public static void adminLogReport() {
        System.out.println("******Admin Log******");
        try {
            Connection adminlogSQL = new Connection();

            //getting data from mysql
            String searchAdminLOG = "SELECT * FROM `adminlog`";
            pS = adminlogSQL.getConnection().prepareStatement(searchAdminLOG);
            result = pS.executeQuery();
            while (result.next()) {
                String username = result.getString("username");
                String timestamp = result.getString("timestamp");
                String status = result.getString("status");
                String reason = result.getString("reason");

                System.out.println("Login/Register Time : " + timestamp + "\nUsername : " + username + "\nStatus : " + status + "\nReason : " + reason);;
                System.out.println("");
            }
            adminlogSQL.getConnection().close();
        } catch (SQLException ex) {
            System.out.println("adminlog MySQL error");
        }
    }
}
