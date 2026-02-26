import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {

    Connection connection;
    Scanner sc;

    Admin (Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    void admin() {
        try {
            System.out.println("------------------------------");
            System.out.print("Enter ADMIN varification code: ");
            int code = sc.nextInt();
            System.out.println("------------------------------");
            String query = "SELECT admin_code FROM admin_code_table";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int originalCode = resultSet.getInt("admin_code");
                if (code == originalCode) {
                    System.out.println("------------");
                    System.out.println("Hello Admin");
                    System.out.println("------------");

                    System.out.println("-----------");
                    System.out.println("1. Sign Up");
                    System.out.println("2. Sign In");
                    System.out.println("-----------");
                    System.out.print("Enter Choice: ");
                    int choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            create_admin_account();
                            break;
                        case 2:
                            signin_admin_account();
                            break;
                        default:
                            System.out.println("---------------");
                            System.out.println("Invalid Choice");
                            System.out.println("---------------");
                            break;
                    }
                } else {
                    System.out.println("-------------------");
                    System.err.println("Invalid ADMIN code");
                    System.out.println("-------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void create_admin_account() {
        try {
            String query = "INSERT INTO admin(id, name, email, username, pass) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            System.out.println("---------------------------------------");
            System.out.println("You will be provided an random ADMIN ID");

            long id = 0;

            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            System.out.print("Enter Username: ");
            String username = sc.nextLine();
            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            String idQuery = "SELECT MAX(id) as max_id from admin";
            PreparedStatement idPreparedStatement = connection.prepareStatement(idQuery);
            ResultSet idResultSet = idPreparedStatement.executeQuery();

            if (idResultSet.next()) {
                id = idResultSet.getLong("max_id");
                if (id == 0) {
                    id = 1000100;
                } else {
                    id += 1;
                }
            }
            
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, username);
            preparedStatement.setString(5, password);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("-----------------------------");
                System.out.println("Account Created Successfully");
                System.out.println("Your Admin ID: " + id);
                System.out.println("-----------------------------");
                
                signin_admin_account();

            } else {
                System.out.println("-----------------------");
                System.out.println("Account is not Created");
                System.out.println("-----------------------");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void signin_admin_account() {
        try {
            String query = "SELECT * FROM admin WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            sc.nextLine(); // bug
            System.out.println("-----------------------");
            System.out.print("Enter Username: ");
            String username = sc.nextLine();
            preparedStatement.setString(1, username);
            ResultSet adminResultSet = preparedStatement.executeQuery();

            if (adminResultSet.next()) {
                String originalPass = adminResultSet.getString("pass");
                System.out.print("Enter Password: ");
                String password = sc.nextLine();

                if (password.equals(originalPass)) {
                    String name = adminResultSet.getString("name");
                    System.out.println("-------------------------");
                    System.out.println("Welcome " + name.toUpperCase());
                    System.out.println("-------------------------");

                    AdminAccount admin = new AdminAccount(connection, sc);
                    admin.admin_power(adminResultSet.getLong("id"));
                } else {
                    System.out.println("-------------------");
                    System.out.println("Incorrect Password");
                    System.out.println("-------------------");
                }
            } else {
                System.out.println("-------------------");
                System.out.println("Username not found");
                System.out.println("-------------------");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
