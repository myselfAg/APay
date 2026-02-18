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
                        String accountQuery = "SELECT * FROM user";
                        PreparedStatement accountPreparedStatement = connection.prepareStatement(accountQuery);

                        ResultSet accountResultSet = accountPreparedStatement.executeQuery();
                        while (accountResultSet.next()) {
                            System.out.println("-------------------------");
                            System.out.println("Account Number: " + accountResultSet.getLong("account_number"));
                            System.out.println("Name: " + accountResultSet.getString("name"));
                            System.out.println("Available Balance: " + accountResultSet.getLong("amount"));
                            boolean isSus = accountResultSet.getBoolean("isSuspend");
                            if (isSus) {
                                System.out.println("Suspended");
                            } else {
                                System.out.println("Active");
                            }
                            System.out.println("-------------------------");
                        }

                        break;
                    case 4:

                        break;
                    case 5:
                        String suspendQuery = "UPDATE user SET isSuspend = true WHERE account_number = ?";
                        PreparedStatement suspendPreparedStatement = connection.prepareStatement(suspendQuery);

                        System.out.print("Enter account number to suspend: ");
                        long suspendAccountNumber = sc.nextLong();

                        suspendPreparedStatement.setLong(1, suspendAccountNumber);

                        int suspendRowsAffected = suspendPreparedStatement.executeUpdate();
                        if (suspendRowsAffected > 0) {
                            System.out.println("Account Suspended Successfully");
                        } else {
                            System.out.println("Account does not exists");
                        }

                        break;
                    case 6: //active user
                        String activeQuery = "UPDATE user SET isSuspend = false WHERE account_number = ?";
                        PreparedStatement activePreparedStatement = connection.prepareStatement(activeQuery);

                        System.out.print("Enter account number to active: ");
                        long activeAccountNumber = sc.nextLong();

                        activePreparedStatement.setLong(1, activeAccountNumber);

                        int activeRowsAffected = activePreparedStatement.executeUpdate();
                        if (activeRowsAffected > 0) {
                            System.out.println("Account Activated Successfully");
                        } else {
                            System.out.println("Account does not exists");
                        }
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
            System.out.print("Enter Account Number: ");
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

        try {
            long accountNumber = userResultSet.getLong("account_number");
            boolean willContinue = true;
            while (willContinue) {
                System.out.println("-------------------------");
                System.out.println("1. View Account Details");
                System.out.println("2. View Balance");
                System.out.println("3. Deposit Amount");
                System.out.println("4. Withdraw Amount");
                System.out.println("5. Send Money");
                System.out.println("6. Transaction History");
                System.out.println("7. Update Account Details");
                System.out.println("8. Delete Account");
                System.out.println("9. Exit");
                System.out.println("-------------------------");

                System.out.print("Enter Choice: ");

                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        String accountQuery = "SELECT * FROM user WHERE account_number = ?";
                        PreparedStatement accountPreparedStatement = connection.prepareStatement(accountQuery);
                        accountPreparedStatement.setLong(1, accountNumber);

                        ResultSet accountResultSet = accountPreparedStatement.executeQuery();
                        if (accountResultSet.next()) {
                            System.out.println("-------------------------");
                            System.out.println("Account Number: " + accountResultSet.getLong("account_number"));
                            System.out.println("Name: " + accountResultSet.getString("name"));
                            System.out.println("Available Balance: " + accountResultSet.getLong("amount"));
                            System.out.println("-------------------------");
                        }
                        break;
                    case 2:
                        String balanceQuery = "SELECT amount FROM user WHERE account_number = ?";
                        PreparedStatement amountPreparedStatement = connection.prepareStatement(balanceQuery);
                        amountPreparedStatement.setLong(1, accountNumber);

                        ResultSet resultSet = amountPreparedStatement.executeQuery();
                        if (resultSet.next()) {
                            System.out.println("Available Balance: " + resultSet.getLong("amount"));
                        }
                        break;
                    case 3:
                        try {
                            connection.setAutoCommit(false);
                            String depositQuery = "UPDATE user SET amount = amount + ? WHERE account_number = ?";
                            PreparedStatement depositPreparedStatement = connection.prepareStatement(depositQuery);

                            System.out.print("Enter deposit amount: ");
                            long depositAmount = sc.nextLong();

                            if (depositAmount > 0) {
                                depositPreparedStatement.setLong(1, depositAmount);
                                depositPreparedStatement.setLong(2, accountNumber);

                                int depositRowsAffected = depositPreparedStatement.executeUpdate();

                                if (depositRowsAffected > 0) {
                                    System.out.println(depositAmount + " rs Added Successfully");
                                } else {
                                    System.out.println("Amount is not added");
                                }
                            } else {
                                System.out.println("Amount must be positive");
                            }
                        } catch (SQLException e) {
                            connection.rollback();
                            System.out.println("Transaction Failed" + e.getMessage());
                        } finally {
                            connection.setAutoCommit(true);
                        }

                        break;
                    case 4:
                        try {
                            connection.setAutoCommit(false);
                            String withdrawQuery = "UPDATE user SET amount = amount - ? WHERE account_number = ?";
                            PreparedStatement withdrawPreparedStatement = connection.prepareStatement(withdrawQuery);

                            System.out.print("Enter withdrawl amount: ");
                            long withdrawAmount = sc.nextLong();

                            String balanceCheckQuery = "SELECT amount FROM user WHERE account_number = ?";
                            PreparedStatement balanceCheckPreparedStatement = connection.prepareStatement(balanceCheckQuery);
                            balanceCheckPreparedStatement.setLong(1, accountNumber);

                            ResultSet balanceCheckResultSet = balanceCheckPreparedStatement.executeQuery();
                            if (balanceCheckResultSet.next()) {
                                long avalBalance = balanceCheckResultSet.getLong("amount");
                                if (withdrawAmount > 0 && avalBalance >= withdrawAmount) {

                                    withdrawPreparedStatement.setLong(1, withdrawAmount);
                                    withdrawPreparedStatement.setLong(2, accountNumber);

                                    int withdrawRowsAffected = withdrawPreparedStatement.executeUpdate();

                                    if (withdrawRowsAffected > 0) {
                                        connection.commit();
                                        System.out.println(withdrawAmount + " rs Withdrawn Successfully");
                                    } else {
                                        connection.rollback();
                                        System.out.println("Amount is not withdrawn");
                                    }
                                } else {
                                    System.out.println("Insufficient Balance");
                                    connection.rollback();
                                }
                            }
                        } catch (SQLException e) {
                            connection.rollback();
                            System.out.println("Transaction Failed: " + e.getMessage());
                        } finally {
                            connection.setAutoCommit(true);
                        }

                        break;
                    case 5: // sent money to another account
                        try {
                            
                        } catch (Exception e) {

                        } finally {

                        }
                        break;
                    case 6: // view transaction history

                        break;
                    case 7: // update account details

                        break;
                    case 8: // delete account

                        break;
                    case 9:
                        willContinue = false;
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
