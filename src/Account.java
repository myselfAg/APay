import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Account {

    Connection connection;
    Scanner sc;

    Account (Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    void user_power(long accountNumber) {

        try {
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
                            System.out.println("Available Balance: " + accountResultSet.getDouble("amount"));

                            LocalDateTime dateTime = accountResultSet.getObject("created_on", LocalDateTime.class);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                            String formattedDate = dateTime.format(formatter);

                            System.out.println("Created On: " + formattedDate);
                            System.out.println("-------------------------");
                        }
                        break;
                    case 2:
                        String balanceQuery = "SELECT amount FROM user WHERE account_number = ?";
                        PreparedStatement amountPreparedStatement = connection.prepareStatement(balanceQuery);
                        amountPreparedStatement.setLong(1, accountNumber);

                        ResultSet resultSet = amountPreparedStatement.executeQuery();
                        if (resultSet.next()) {
                            System.out.println("-------------------------");
                            System.out.println("Available Balance: " + resultSet.getDouble("amount"));
                            System.out.println("-------------------------");
                        }
                        break;
                    case 3:
                        try {
                            connection.setAutoCommit(false);
                            String depositQuery = "UPDATE user SET amount = amount + ? WHERE account_number = ?";
                            PreparedStatement depositPreparedStatement = connection.prepareStatement(depositQuery);

                            System.out.println("---------------------");
                            System.out.print("Enter deposit amount: ");
                            double depositAmount = sc.nextDouble();

                            if (depositAmount > 0) {
                                depositPreparedStatement.setDouble(1, depositAmount);
                                depositPreparedStatement.setLong(2, accountNumber);
                                int depositRowsAffected = depositPreparedStatement.executeUpdate();

                                if (depositRowsAffected > 0) {
                                    connection.commit();
                                    System.out.println("------------------------------------");
                                    System.out.println(depositAmount + " rs Deposited Successfully");
                                    System.out.println("------------------------------------");
                                } else {
                                    connection.rollback();
                                    System.out.println("--------------------");
                                    System.out.println("Amount did not Deposit");
                                    System.out.println("--------------------");
                                }
                            } else {
                                connection.rollback();
                                System.out.println("------------------------");
                                System.out.println("Amount must be positive");
                                System.out.println("------------------------");
                            }
                        } catch (SQLException e) {
                            connection.rollback();
                            System.out.println("---------------------------------------");
                            System.out.println("Transaction Failed" + e.getMessage());
                            System.out.println("---------------------------------------");
                        } finally {
                            connection.setAutoCommit(true);
                        }

                        break;
                    case 4:
                        try {
                            connection.setAutoCommit(false);
                            String withdrawQuery = "UPDATE user SET amount = amount - ? WHERE account_number = ?";
                            PreparedStatement withdrawPreparedStatement = connection.prepareStatement(withdrawQuery);

                            System.out.println("-----------------------");
                            System.out.print("Enter withdrawl amount: ");
                            double withdrawAmount = sc.nextDouble();

                            String balanceCheckQuery = "SELECT amount FROM user WHERE account_number = ?";
                            PreparedStatement balanceCheckPreparedStatement = connection.prepareStatement(balanceCheckQuery);
                            balanceCheckPreparedStatement.setLong(1, accountNumber);
                            ResultSet balanceCheckResultSet = balanceCheckPreparedStatement.executeQuery();

                            if (balanceCheckResultSet.next()) {
                                double avalBalance = balanceCheckResultSet.getDouble("amount");
                                if (withdrawAmount > 0 && avalBalance >= withdrawAmount) {

                                    withdrawPreparedStatement.setDouble(1, withdrawAmount);
                                    withdrawPreparedStatement.setLong(2, accountNumber);

                                    int withdrawRowsAffected = withdrawPreparedStatement.executeUpdate();

                                    if (withdrawRowsAffected > 0) {
                                        connection.commit();
                                        System.out.println("-----------------------------------------");
                                        System.out.println(withdrawAmount + " rs Withdrawn Successfully");
                                        System.out.println("-----------------------------------------");
                                    } else {
                                        connection.rollback();
                                        System.out.println("------------------------");
                                        System.out.println("Amount did not withdraw");
                                        System.out.println("------------------------");
                                    }
                                } else {
                                    connection.rollback();
                                    System.out.println("---------------------");
                                    System.out.println("Insufficient Balance");
                                    System.out.println("---------------------");
                                }
                            }
                        } catch (SQLException e) {
                            connection.rollback();
                            System.out.println("-------------------------------------");
                            System.out.println("Transaction Failed: " + e.getMessage());
                            System.out.println("-------------------------------------");
                        } finally {
                            connection.setAutoCommit(true);
                        }

                        break;

                    case 5:
                        try {
                            connection.setAutoCommit(false);
                            String senderDetail = "SELECT * FROM user WHERE account_number = ?";
                            String debitQuery = "UPDATE user SET amount = amount - ? WHERE account_number = ?";
                            String creditQuery = "UPDATE user SET amount = amount + ? WHERE account_number = ?";

                            PreparedStatement checkDetailsPreparedStatement = connection.prepareStatement(senderDetail);
                            PreparedStatement debitPreparedStatement = connection.prepareStatement(debitQuery);
                            PreparedStatement creditPreparedStatement = connection.prepareStatement(creditQuery);

                            checkDetailsPreparedStatement.setLong(1, accountNumber);
                            ResultSet checkDetailsResultSet = checkDetailsPreparedStatement.executeQuery();

                            System.out.println("---------------------");
                            System.out.print("Enter account number: ");
                            long receiverAccountNumber = sc.nextLong();

                            System.out.print("Enter amount: ");
                            double sendingAmount = sc.nextDouble();

                            if (checkDetailsResultSet.next()) {
                                if (sendingAmount > 0) {
                                    System.out.print("Enter PIN: ");
                                    long enteredPin = sc.nextLong();
                                    if (checkDetailsResultSet.getLong("pin") == enteredPin) {
                                        if (checkDetailsResultSet.getLong("amount") >= sendingAmount) {

                                            debitPreparedStatement.setDouble(1, sendingAmount);
                                            debitPreparedStatement.setLong(2, accountNumber);

                                            creditPreparedStatement.setDouble(1, sendingAmount);
                                            creditPreparedStatement.setLong(2, receiverAccountNumber);

                                            int debitRowsAffected = debitPreparedStatement.executeUpdate();
                                            int creditRowsAffected = creditPreparedStatement.executeUpdate();

                                            if (debitRowsAffected > 0 && creditRowsAffected > 0) {
                                                connection.commit();
                                                System.out.println("------------------------------------");
                                                System.out.println(sendingAmount + " rs sent Successfully");
                                                System.out.println("------------------------------------");
                                            } else {
                                                connection.rollback();
                                                System.out.println("--------------------");
                                                System.out.println("Amount did not send");
                                                System.out.println("--------------------");
                                            }
                                        } else {
                                            System.out.println("------------------------");
                                            System.out.println("Insufficient Balance");
                                            System.out.println("------------------------");
                                            connection.rollback();
                                        }
                                    } else {
                                        System.out.println("------------");
                                        System.out.println("Invalid PIN");
                                        System.out.println("------------");
                                        connection.rollback();
                                    }

                                } else {
                                    System.out.println("------------------------");
                                    System.out.println("Amount must be positive");
                                    System.out.println("------------------------");
                                    connection.rollback();
                                }
                            }

                        } catch (SQLException e) {
                            connection.rollback();
                            System.out.println("---------------------------------------");
                            System.out.println("Transaction Failed" + e.getMessage());
                            System.out.println("---------------------------------------");
                        } finally {
                            connection.setAutoCommit(true);
                        }
                        break;

                    case 6:
                        // String transactionHistoryQuery = "SELECT * FROM user_transaction WHERE fromWhomAccNo = ? OR toWhomAccNo = ?";
                        // PreparedStatement transactionHistoryPreparedStatement = connection
                        //         .prepareStatement(transactionHistoryQuery);
                        // transactionHistoryPreparedStatement.setLong(1, accountNumber);
                        // transactionHistoryPreparedStatement.setLong(2, accountNumber);

                        // ResultSet transactionHistoryResultSet = transactionHistoryPreparedStatement.executeQuery();
                        // while (transactionHistoryResultSet.next()) {
                        //     if (transactionHistoryResultSet.getLong("fromWhomAccNo") == accountNumber) {
                        //         System.out.println("-------------------------");
                        //         System.out.println("Name: " + transactionHistoryResultSet.getString("toWhomName"));
                        //         System.out.println("Amount: " + transactionHistoryResultSet.getLong("amount"));

                        //         LocalDateTime dateTime = transactionHistoryResultSet.getObject("transaction_date",
                        //                 LocalDateTime.class);
                        //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        //         String formattedDate = dateTime.format(formatter);

                        //         System.out.println("Amount Received: " + formattedDate);
                        //         System.out.println("-------------------------");

                        //     } else {
                        //         System.out.println("-------------------------");
                        //         System.out.println("Name: " + transactionHistoryResultSet.getString("fromWhomName"));
                        //         System.out.println("Amount: +" + transactionHistoryResultSet.getLong("amount"));

                        //         LocalDateTime dateTime = transactionHistoryResultSet.getObject("transaction_date",
                        //                 LocalDateTime.class);
                        //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        //         String formattedDate = dateTime.format(formatter);

                        //         System.out.println("Amount Received: " + formattedDate);
                        //         System.out.println("-------------------------");
                        //     }
                        // }
                        break;
                    case 7:
                        System.out.println("-------------------------");
                        System.out.println("1. Change Name");
                        System.out.println("2. Change Password");
                        System.out.println("3. Change PIN");
                        System.out.println("4. Exit");
                        System.out.println("-------------------------");

                        System.out.print("Enter Choice: ");
                        int change = sc.nextInt();
                        switch (change) {
                            case 1:
                                try {
                                    String oldPassQuery = "SELECT * FROM user WHERE account_number = ?";
                                    PreparedStatement oldPassPreparedStatement = connection.prepareStatement(oldPassQuery);
                                    oldPassPreparedStatement.setLong(1, accountNumber);
                                    ResultSet oldPassResultSet = oldPassPreparedStatement.executeQuery();
                                    if (!oldPassResultSet.next()) {
                                        break;
                                    }
                                    String storedPass = oldPassResultSet.getString("pass");

                                    String changeNameQuery = "UPDATE user SET name = ? WHERE account_number = ?";
                                    PreparedStatement updateNamePreparedStatement = connection.prepareStatement(changeNameQuery);

                                    sc.nextLine();
                                    System.out.println("-------------------------");
                                    System.out.print("Enter new name: ");
                                    String newName = sc.nextLine();

                                    System.out.print("Enter Password to update name: ");
                                    String enteredPass = sc.nextLine();

                                    if (enteredPass.equals(storedPass)) {
                                        updateNamePreparedStatement.setString(1, newName);
                                        updateNamePreparedStatement.setLong(2, accountNumber);
                                        int updateNameRowsAffected = updateNamePreparedStatement.executeUpdate();
                                        if (updateNameRowsAffected > 0) {
                                            System.out.println("--------------------------");
                                            System.out.println("Name Updated Successfully");
                                            System.out.println("--------------------------");
                                            // User user = new User(connection, sc);
                                            // user.signin_user_account();
                                        } else {
                                            System.out.println("--------------------");
                                            System.out.println("Name did not Update");
                                            System.out.println("--------------------");
                                        }
                                    } else {
                                        System.out.println("-------------------");
                                        System.out.println("Incorrect Password");
                                        System.out.println("-------------------");
                                    }
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                }

                                break;
                            case 2:
                                try {
                                    String oldPassQuery = "SELECT * FROM user WHERE account_number = ?";
                                    PreparedStatement oldPassPreparedStatement = connection.prepareStatement(oldPassQuery);
                                    oldPassPreparedStatement.setLong(1, accountNumber);
                                    ResultSet oldPassResultSet = oldPassPreparedStatement.executeQuery();
                                    if (!oldPassResultSet.next()) {
                                        break;
                                    }
                                    String storedPass = oldPassResultSet.getString("pass");

                                    String changePassQuery = "UPDATE user SET pass = ? WHERE account_number = ?";
                                    PreparedStatement updatePassPreparedStatement = connection.prepareStatement(changePassQuery);

                                    sc.nextLine();
                                    System.out.println("-------------------");
                                    System.out.print("Enter new password: ");
                                    String newPassword = sc.nextLine();

                                    System.out.print("Enter old password to updated to new password: ");
                                    String enteredPass = sc.nextLine();

                                    if (enteredPass.equals(storedPass)) {
                                        updatePassPreparedStatement.setString(1, newPassword);
                                        updatePassPreparedStatement.setLong(2, accountNumber);
                                        int updatePassRowsAffected = updatePassPreparedStatement.executeUpdate();
                                        if (updatePassRowsAffected > 0) {
                                            System.out.println("------------------------------");
                                            System.out.println("Password Updated Successfully");
                                            System.out.println("------------------------------");
                                            // signin_user_account();
                                        } else {
                                            System.out.println("------------------------");
                                            System.out.println("Password did not Update");
                                            System.out.println("------------------------");
                                        }
                                    } else {
                                        System.out.println("-------------------");
                                        System.out.println("Incorrect Password");
                                        System.out.println("-------------------");
                                    }
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                }
                                break;
                            case 3:
                                try {
                                    String oldPassQuery = "SELECT * FROM user WHERE account_number = ?";
                                    PreparedStatement oldPassPreparedStatement = connection.prepareStatement(oldPassQuery);
                                    oldPassPreparedStatement.setLong(1, accountNumber);
                                    ResultSet oldPassResultSet = oldPassPreparedStatement.executeQuery();
                                    if (!oldPassResultSet.next()) {
                                        break;
                                    }
                                    String storedPass = oldPassResultSet.getString("pass");

                                    String changePinQuery = "UPDATE user SET pin = ? WHERE account_number = ?";
                                    PreparedStatement updatePinPreparedStatement = connection.prepareStatement(changePinQuery);

                                    System.out.println("---------------------------------");
                                    System.out.print("Enter new pin: ");
                                    long newPin = sc.nextLong();

                                    System.out.print("Enter password to update the pin: ");
                                    String enteredPass = sc.nextLine();

                                    if (enteredPass.equals(storedPass)) {
                                        updatePinPreparedStatement.setLong(1, newPin);
                                        updatePinPreparedStatement.setLong(2, accountNumber);
                                        int updatePinRowsAffected = updatePinPreparedStatement.executeUpdate();
                                        if (updatePinRowsAffected > 0) {
                                            System.out.println("-------------------------");
                                            System.out.println("PIN Updated Successfully");
                                            System.out.println("-------------------------");
                                            // willContinue = false;
                                            // signin_user_account();
                                        } else {
                                            System.out.println("-------------------");
                                            System.out.println("PIN did not Update");
                                            System.out.println("-------------------");
                                        }
                                    } else {
                                        System.out.println("-------------------");
                                        System.out.println("Incorrect Password");
                                        System.out.println("-------------------");
                                    }
                                } catch (SQLException e) {
                                    System.out.println(e.getMessage());
                                }
                                break;
                            case 4:
                                break;
                            default:
                                System.out.println("---------------");
                                System.out.println("Invalid Choice");
                                System.out.println("---------------");
                                break;
                        }
                        break;
                    case 8:
                        try {
                            String oldPassQuery = "SELECT * FROM user WHERE account_number = ?";
                            PreparedStatement oldPassPreparedStatement = connection.prepareStatement(oldPassQuery);
                            oldPassPreparedStatement.setLong(1, accountNumber);
                            ResultSet oldPassResultSet = oldPassPreparedStatement.executeQuery();
                            if (!oldPassResultSet.next()) {
                                break;
                            }
                            String storedPass = oldPassResultSet.getString("pass");

                            String deleteQuery = "UPDATE user SET isDeleted = true WHERE account_number = ?";
                            PreparedStatement deletePreparedStatement = connection.prepareStatement(deleteQuery);

                            deletePreparedStatement.setLong(1, accountNumber);
                            
                            sc.nextLine();
                            System.out.println("------------------------------------------");
                            System.out.print("Enter password to delete the account: ");
                            String enteredPass = sc.nextLine();

                            if (enteredPass.equals(storedPass)) {

                                int deleteRowsAffected = deletePreparedStatement.executeUpdate();
                                if (deleteRowsAffected > 0) {
                                    System.out.println("-----------------------------");
                                    System.out.println("Account deleted Successfully");
                                    System.out.println("-----------------------------");
                                    User user = new User(connection, sc);
                                    user.user();

                                } else {
                                    System.out.println("------------------------");
                                    System.out.println("Account does not exists");
                                    System.out.println("------------------------");
                                }
                            } else {
                                System.out.println("----------------------------------------------");
                                System.out.println("Incorrect Password. Cannot delete the account");
                                System.out.println("----------------------------------------------");
                            }
                        } catch (SQLException e) {
                            System.out.println("------------------------");
                            System.out.println("Account does not exists");
                            System.out.println("------------------------");
                        }
                        break;
                    case 9:
                        willContinue = false;
                        break;

                    default:
                        System.out.println("---------------");
                        System.out.println("Invalid Choice");
                        System.out.println("---------------");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}