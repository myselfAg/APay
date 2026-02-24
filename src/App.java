import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    private static final String url = System.getenv("APAY_DB_URL");
    private static final String username = System.getenv("APAY_DB_USER");
    private static final String password = System.getenv("APAY_DB_PASS");
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner sc = new Scanner(System.in);
            Admin admin = new Admin(connection, sc);
            User user = new User(connection, sc);
            System.out.println("-------------------------");
            System.out.print("Are you a ADMIN? (Y / N): ");
            String isAdmin = sc.next();
            System.out.println("-------------------------");
            if (isAdmin.toUpperCase().equals("Y")) {
                admin.admin();
            } else {
                user.user();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }    
}