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
                user(connection);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void admin(Connection connection) {
        try {
            System.out.print("Enter ADMIN varification code: ");
            int code = sc.nextInt();
            String query = "SELECT admin_code FROM admin_code_table";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int originalCode = resultSet.getInt("admin_code");
                if (code == originalCode) {
                    System.out.println("Hello Admin");

                    System.out.println("1. Sign Up");
                    System.out.println("2. Sign In");
                    System.out.print("Enter Choice: ");
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
                    System.err.println("Invalid ADMIN code");
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
                System.out.println("-------------------------");
                System.out.println("1. View Your Details");
                System.out.println("2. Change Your Details");
                System.out.println("3. View User Details");
                System.out.println("4. View User Transactions");
                System.out.println("5. Suspend User"); // will be a loop
                System.out.println("6. Active User"); // will be a loop
                System.out.println("7. Exit"); // will be a loop
                System.out.println("-------------------------");
                
                System.out.print("Enter Choice: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("-------------------------");
                        System.out.println("ID: " + adminResultSet.getLong("id"));
                        System.out.println("Name: " + adminResultSet.getString("name"));
                        System.out.println("Username: " + adminResultSet.getString("username"));
                        System.out.println("-------------------------");
                        break;
                    case 2:
                        System.out.println("-------------------------");
                        System.out.println("1. Change Name");
                        System.out.println("2. Change Username");
                        System.out.println("3. Change Password");
                        System.out.println("-------------------------");

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
                                try {
                                    String changeName = "UPDATE admin SET username = ? WHERE id = ?";
                                    PreparedStatement preparedStatement = connection.prepareStatement(changeName);

                                    long storedId = adminResultSet.getLong("id");
                                    long storedPass = adminResultSet.getLong("pass");

                                    sc.nextLine();
                                    System.out.print("Enter new username: ");
                                    String newUsername = sc.nextLine();

                                    System.out.print("Enter Password: ");
                                    long newPass = sc.nextLong();

                                    if (newPass == storedPass) {
                                        preparedStatement.setString(1, newUsername);
                                        preparedStatement.setLong(2, storedId);
                                        int rowsAffected = preparedStatement.executeUpdate();
                                        if (rowsAffected > 0) {
                                            System.out.println("Userame Updated Successfully");
                                            signin_admin_account(connection);
                                        } else {
                                            System.out.println("Username did not Update");
                                        }
                                    } else {
                                        System.out.println("Incorrect Password");
                                    }
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                }
                                break;
                            case 3:
                                try {
                                    String changeName = "UPDATE admin SET pass = ? WHERE id = ?";
                                    PreparedStatement preparedStatement = connection.prepareStatement(changeName);

                                    long storedId = adminResultSet.getLong("id");
                                    long storedPass = adminResultSet.getLong("pass");

                                    sc.nextLine();
                                    System.out.print("Enter new password: ");
                                    long newPass = sc.nextLong();

                                    System.out.print("Enter old password to update the password: ");
                                    long oldPass = sc.nextLong();

                                    if (oldPass == storedPass) {
                                        preparedStatement.setLong(1, newPass);
                                        preparedStatement.setLong(2, storedId);
                                        int rowsAffected = preparedStatement.executeUpdate();
                                        if (rowsAffected > 0) {
                                            System.out.println("Password Updated Successfully");
                                            willContinue = false;
                                            signin_admin_account(connection);
                                        } else {
                                            System.out.println("Password did not Update");
                                        }
                                    } else {
                                        System.out.println("Incorrect Password");
                                    }
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                }
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
                        System.out.println("Invalid Choice");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    static void user(Connection connection) {
        System.out.println("Hello User");
        System.out.println("-------------------------");
        System.out.println("1. Sign Up");
        System.out.println("2. Sign In");
        System.out.println("-------------------------");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                create_user_account(connection);
                break;
            case 2:
                signin_user_account(connection);
                break;
            default:
                System.out.println("Invalid Choice");
                break;
        }

    }

    static void create_user_account(Connection connection) {
        try {
            String query = "INSERT INTO user(name, pass, pin, amount) VALUES(?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            System.out.println("You will be provided an random USER ID");
            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Password: ");
            long password = sc.nextLong();
            System.out.print("Enter PIN: ");
            long pin = sc.nextLong();
            System.out.print("Enter amount: ");
            long amount = sc.nextLong();

            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, password);
            preparedStatement.setLong(3, pin);
            preparedStatement.setLong(4, amount);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account Created Successfully");
            } else {
                System.out.println("Account is not Created");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void signin_user_account(Connection connection) {
        try {
            String query = "SELECT * FROM user WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            sc.nextLine();
            System.out.print("Enter ID: ");
            long username = sc.nextLong();
            preparedStatement.setLong(1, username);
            ResultSet userResultSet = preparedStatement.executeQuery();

            if (userResultSet.next()) {
                long originalPass = userResultSet.getLong("pass");
                System.out.print("Enter Password: ");
                long password = sc.nextLong();

                if (password == originalPass) {
                    String name = userResultSet.getString("name");
                    System.out.println("Welcome " + name.toUpperCase());

                    user_power(connection, userResultSet);

                } else {
                    System.out.println("Incorrect Password");
                }
            } else {
                System.out.println("Account not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void user_power(Connection connection, ResultSet userResultSet) {
        System.out.println("User Power");
    }
}
