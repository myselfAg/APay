import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {

    Connection connection;
    Scanner sc;

    User (Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    void user() {
        System.out.println("-----------");
        System.out.println("Hello User");
        System.out.println("-----------");
        System.out.println("1. Sign Up");
        System.out.println("2. Sign In");
        System.out.println("-----------");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        switch (choice) {
            case 1:
                create_user_account();
                break;
            case 2:
                signin_user_account();
                break;
            default:
                System.out.println("---------------");
                System.out.println("Invalid Choice");
                System.out.println("---------------");
                break;
        }
    }

    void create_user_account() {
        try {
            String query = "INSERT INTO user(account_number, name, email, pass, pin, amount) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            System.out.println("---------------------------------------");
            System.out.println("You will be provided an random USER ID");

            long accountNumber = 0;

            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            System.out.print("Enter Password: ");
            String password = sc.nextLine();
            System.out.print("Enter PIN: ");
            long pin = sc.nextLong();
            System.out.print("Enter initial amount: ");
            double amount = sc.nextDouble();

            String accountNumberQuery = "SELECT MAX(account_number) as max_account from user";
            PreparedStatement accountNumberPreparedStatement = connection.prepareStatement(accountNumberQuery);
            ResultSet accountNumberResultSet = accountNumberPreparedStatement.executeQuery();

            if (accountNumberResultSet.next()) {
                accountNumber = accountNumberResultSet.getLong("max_account");
                if (accountNumber == 0) {
                    accountNumber = 100100;
                } else {
                    accountNumber += 1;
                }
            }
            
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.setLong(5, pin);
            preparedStatement.setDouble(6, amount);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("-----------------------------");
                System.out.println("Account Created Successfully");
                System.out.println("Your Account Number: " + accountNumber);
                System.out.println("-----------------------------");
                signin_user_account();

            } else {
                System.out.println("-----------------------");
                System.out.println("Account is not Created");
                System.out.println("-----------------------");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void signin_user_account() {
        try {
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            sc.nextLine();
            System.out.println("---------------------");
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            preparedStatement.setString(1, email);
            ResultSet userResultSet = preparedStatement.executeQuery();

            if (userResultSet.next()) {

                boolean isDeleted = userResultSet.getBoolean("isDeleted");
                if (isDeleted) {
                    System.out.println("-------------------------------------");
                    System.out.println("Your account does not exists anymore");
                    System.out.println("-------------------------------------");
                    user();
                } else {
                    boolean isSuspend = userResultSet.getBoolean("isSuspend");
                    if (isSuspend) {
                        System.out.println("--------------------------");
                        System.out.println("Your Account is Suspended");
                        System.out.println("--------------------------");
                        user();
                    } else {
                        String originalPass = userResultSet.getString("pass");
                        System.out.print("Enter Password: ");
                        String password = sc.nextLine();

                        if (password.equals(originalPass)) {
                            
                            Account account = new Account(connection, sc);

                            long accountnumber = userResultSet.getLong("account_number");
                            String name = userResultSet.getString("name");
                            System.out.println("--------------------------");
                            System.out.println("Welcome " + name.toUpperCase());
                            System.out.println("--------------------------");

                            account.user_power(accountnumber);
                            // , name, email, userResultSet.getString("pass"), userResultSet.getLong("pin"), userResultSet.getDouble("amount"), formattedDate

                        } else {
                            System.out.println("-------------------");
                            System.out.println("Incorrect Password");
                            System.out.println("-------------------");
                        }
                    }
                }
            } else {
                System.out.println("------------------");
                System.out.println("Account not found");
                System.out.println("------------------");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
