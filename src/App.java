import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {

    private static final String url = System.getenv("APAY_DB_URL");
    private static final String username = System.getenv("APAY_DB_USER");
    private static final String password = System.getenv("APAY_DB_PASS");
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            // connection.setAutoCommit(false);
            System.out.println("Are you a ADMIN? (Y / N)");
            String isAdmin = sc.next();
            if (isAdmin.toUpperCase().equals("Y")) {
                admin(connection);
            } else {
                user();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void admin(Connection connection) {
        try {
            System.out.println("Enter ADMIN varification code: ");
            int code = sc.nextInt();
            String query = "SELECT admin_code FROM admin_code_table";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int originalCode = resultSet.getInt("admin_code");
                if (code == originalCode) {
                    System.out.println("Hello Admin");

                    System.out.print("Press 1 for Sign Up / 2 for Sign In: ");
                    int choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            create_admin_account(connection);
                            break;
                        case 2:
                            signin_admin_account(connection);
                            break;
                        default:
                            System.out.println("Invalid Choice");
                            break;
                    }
                } else {
                    System.err.println("x");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void create_admin_account(Connection connection) {
        try {
            String query = "INSERT INTO admin(name, username, pass) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            System.out.println("You will be provided an random ADMIN ID");
            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Username: ");
            String username = sc.nextLine();
            System.out.print("Enter Password: ");
            long password = sc.nextLong();

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setLong(3, password);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account Created Successfully");
            } else {
                System.out.println("Account is not created");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    static void signin_admin_account(Connection connection) {
        try {
            String query = "SELECT * FROM admin WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            sc.nextLine();
            System.out.print("Enter Username: ");
            String username = sc.nextLine();
            preparedStatement.setString(1, username);
            ResultSet adminResultSet = preparedStatement.executeQuery();

            if (adminResultSet.next()) {
                long originalPass = adminResultSet.getLong("pass");
                System.out.print("Enter Password: ");
                long password = sc.nextLong();

                if (password == originalPass) {
                    String name = adminResultSet.getString("name");
                    System.out.println("Welcome " + name.toUpperCase());

                    admin_power(connection, adminResultSet);

                } else {
                    System.out.println("Incorrect Password");
                }
            } else {
                System.out.println("Username not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void admin_power(Connection connection, ResultSet adminResultSet) {
        try {
            boolean willContinue = true;
            while (willContinue) {
                System.out.println("1. View Your Details");
                System.out.println("2. Change Your Details");
                System.out.println("3. View User Details");
                System.out.println("4. View User Transactions");
                System.out.println("5. Suspend User"); // will be a loop
                System.out.println("6. Active User"); // will be a loop
                System.out.println("7. Exit"); // will be a loop

                System.out.print("Enter Choice: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("ID: " + adminResultSet.getLong("id"));
                        System.out.println("Name: " + adminResultSet.getString("name"));
                        System.out.println("Username: " + adminResultSet.getString("username"));
                        break;
                    case 2:
                        System.out.println("1. Change Name");
                        System.out.println("2. Change Username");
                        System.out.println("3. Change Password");

                        System.out.print("Enter Choice: ");
                        int change = sc.nextInt();
                        switch (change) {
                            case 1:
                                try {
                                    String changeName = "UPDATE admin SET name = ? WHERE id = ?";
                                    PreparedStatement preparedStatement = connection.prepareStatement(changeName);

                                    long storedId = adminResultSet.getLong("id");
                                    long storedPass = adminResultSet.getLong("pass");

                                    sc.nextLine();
                                    System.out.print("Enter new name: ");
                                    String newName = sc.nextLine();

                                    System.out.print("Enter Password: ");
                                    long newPass = sc.nextLong();

                                    if (newPass == storedPass) {
                                        preparedStatement.setString(1, newName);
                                        preparedStatement.setLong(2, storedId);
                                        int rowsAffected = preparedStatement.executeUpdate();
                                        if (rowsAffected > 0) {
                                            System.out.println("Name Updated Successfully");
                                            signin_admin_account(connection);
                                        } else {
                                            System.out.println("Name did not Update");
                                        }
                                    } else {
                                        System.out.println("Incorrect Password");
                                    }
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                }

                                break;
                            case 2:

                                break;
                            case 3:

                                break;

                            default:
                                break;
                        }
                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                    case 5:

                        break;
                    case 6:

                        break;
                    case 7:
                        willContinue = false;
                        break;

                    default:
                        break;
                }
            }
        } catch (SQLException e) {
        }

    }

    static void user() {
        System.out.println("Hello User");
    }

    static void create_user_account() {

    }
}
