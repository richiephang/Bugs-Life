package mavenproject3;

import java.io.IOException;
import java.text.ParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

// tester class
public class tester {

    // main method
    public static void main(String[] args) throws IOException, ParseException {
        homepage();
    }

    // show the homepage action
    public static void homepage() throws IOException, ParseException {
        Scanner in = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("Enter '1' to login, '2' to register, '3' to exit program");
                int userInput = in.nextInt();
                if (userInput == 1) {
                    User.login();
                    break;
                } else if (userInput == 2) {
                    User.register();
                    break;
                } else if (userInput == 3) {
                    System.exit(0);
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid value!\n");
                in.next();
            }
        }

        while (!User.isLogin_status()) {
            System.out.println("Please login to proceed....");
            User.login();
        }
        if (User.getLoginName().equals("admin")) {
            adminHomepage();
        } else {
            Project.displayProject();
        }
    }

    // show the admin homepage action
    public static void adminHomepage() throws IOException, ParseException {
        Scanner in = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\nEnter '1' to view access control list\nOr '2' to trace the abnormal activities\n'3' to generate report\n'4' to Import or Export JSON data\n'5' to view full list changelog\n'6' to view projects and issues \n'-1' to logout");

                int userInput = in.nextInt();
                if (userInput == 1) {
                    access_control.adminLogReport();
                    adminHomepage();
                    break;
                } else if (userInput == 2) {
                    adminlog.adminLogReport();
                    adminHomepage();
                    break;
                } else if (userInput == 3) {
                    ReportGeneration.generateReport();
                    adminHomepage();
                    break;
                } else if (userInput == 4) {
                    JSON_IMPORT_EXPORT.importExport();
                    adminHomepage();
                    break;
                } else if (userInput == 6) {
                    Project.displayProject();
                    break;
                }else if (userInput == 5) {
                    changelog.viewChangelog();
                    adminHomepage();
                    break;
                
                } else if (userInput == -1) {
                    User.logout();
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid value!\n");
                in.next();
            }
        }
    }
}
