import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AdminAccount {
    Connection connection;
    Scanner sc;

    AdminAccount (Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    void admin_power(long id) {
        try {
            boolean willContinue = true;
            while (willContinue) {
                System.out.println("-------------------------");
                System.out.println("1. View Your Details");
                System.out.println("2. Change Your Details");
                System.out.println("3. View User Details");
                System.out.println("4. View User Transactions");
                System.out.println("5. Suspend User");
                System.out.println("6. Active User");
                System.out.println("7. View Deleted Account");
                System.out.println("8. View Suspended Account");
                System.out.println("9. Exit");
                System.out.println("-------------------------");

                System.out.print("Enter Choice: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        String adminQuery = "SELECT * FROM admin WHERE id = ?";
                        PreparedStatement adminPreparedStatement = connection.prepareStatement(adminQuery);
                        adminPreparedStatement.setLong(1, id);
                        ResultSet adminResultSet = adminPreparedStatement.executeQuery();

                        if (adminResultSet.next()) {
                            System.out.println("-------------------------");
                            System.out.println("ID: " + adminResultSet.getLong("id"));
                            System.out.println("Name: " + adminResultSet.getString("name"));
                            System.out.println("Email: " + adminResultSet.getString("email"));
                            System.out.println("Username: " + adminResultSet.getString("username"));
                            LocalDateTime adminDateTime = adminResultSet.getObject("created_on", LocalDateTime.class);
                            DateTimeFormatter adminCreatedOnformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                            String formattedDate = adminDateTime.format(adminCreatedOnformatter);

                            System.out.println("Created On: " + formattedDate);
                            System.out.println("-------------------------");
                        }
                        
                        break;
                    case 2:
                        System.out.println("-------------------------");
                        System.out.println("1. Change Name");
                        System.out.println("2. Change Password");
                        System.out.println("3. Change Username");
                        System.out.println("4. Exit");
                        System.out.println("-------------------------");

                        System.out.print("Enter Choice: ");
                        int change = sc.nextInt();
                        switch (change) {
                            case 1:
                                try {
                                    String oldPassQuery = "SELECT * FROM admin WHERE id = ?";
                                    PreparedStatement oldPassPreparedStatement = connection.prepareStatement(oldPassQuery);
                                    oldPassPreparedStatement.setLong(1, id);
                                    ResultSet oldPassResultSet = oldPassPreparedStatement.executeQuery();
                                    if (!oldPassResultSet.next()) {
                                        break;
                                    }
                                    String storedPass = oldPassResultSet.getString("pass");

                                    String changeNameQuery = "UPDATE admin SET name = ? WHERE id = ?";
                                    PreparedStatement updateNamePreparedStatement = connection.prepareStatement(changeNameQuery);

                                    sc.nextLine();
                                    System.out.println("-------------------------");
                                    System.out.print("Enter new name: ");
                                    String newName = sc.nextLine();

                                    System.out.print("Enter Password to update name: ");
                                    String enteredPass = sc.nextLine();

                                    if (enteredPass.equals(storedPass)) {
                                        updateNamePreparedStatement.setString(1, newName);
                                        updateNamePreparedStatement.setLong(2, id);
                                        int updateNameRowsAffected = updateNamePreparedStatement.executeUpdate();
                                        if (updateNameRowsAffected > 0) {
                                            System.out.println("--------------------------");
                                            System.out.println("Name Updated Successfully");
                                            System.out.println("--------------------------");
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
                                    String oldPassQuery = "SELECT * FROM admin WHERE id = ?";
                                    PreparedStatement oldPassPreparedStatement = connection.prepareStatement(oldPassQuery);
                                    oldPassPreparedStatement.setLong(1, id);
                                    ResultSet oldPassResultSet = oldPassPreparedStatement.executeQuery();
                                    if (!oldPassResultSet.next()) {
                                        break;
                                    }
                                    String storedPass = oldPassResultSet.getString("pass");

                                    String changePassQuery = "UPDATE admin SET pass = ? WHERE id = ?";
                                    PreparedStatement updatePassPreparedStatement = connection.prepareStatement(changePassQuery);

                                    sc.nextLine();
                                    System.out.println("-------------------");
                                    System.out.print("Enter new password: ");
                                    String newPassword = sc.nextLine();

                                    System.out.print("Enter old password to updated to new password: ");
                                    String enteredPass = sc.nextLine();

                                    if (enteredPass.equals(storedPass)) {
                                        updatePassPreparedStatement.setString(1, newPassword);
                                        updatePassPreparedStatement.setLong(2, id);
                                        int updatePassRowsAffected = updatePassPreparedStatement.executeUpdate();
                                        if (updatePassRowsAffected > 0) {
                                            System.out.println("------------------------------");
                                            System.out.println("Password Updated Successfully");
                                            System.out.println("------------------------------");
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
                                    String oldPassQuery = "SELECT * FROM admin WHERE id = ?";
                                    PreparedStatement oldPassPreparedStatement = connection.prepareStatement(oldPassQuery);
                                    oldPassPreparedStatement.setLong(1, id);
                                    ResultSet oldPassResultSet = oldPassPreparedStatement.executeQuery();
                                    if (!oldPassResultSet.next()) {
                                        break;
                                    }
                                    String storedPass = oldPassResultSet.getString("pass");

                                    String updateUsernameQuery = "UPDATE admin SET username = ? WHERE id = ?";
                                    PreparedStatement updateUsernamePreparedStatement = connection.prepareStatement(updateUsernameQuery);

                                    sc.nextLine();
                                    System.out.println("-------------------");
                                    System.out.print("Enter new username: ");
                                    String newUsername = sc.nextLine();

                                    System.out.print("Enter password to update the useranme: ");
                                    String enteredPass = sc.nextLine();

                                    if (enteredPass.equals(storedPass)) {
                                        updateUsernamePreparedStatement.setString(1, newUsername);
                                        updateUsernamePreparedStatement.setLong(2, id);
                                        int updateUsernameRowsAffected = updateUsernamePreparedStatement.executeUpdate();
                                        if (updateUsernameRowsAffected > 0) {
                                            System.out.println("------------------------------");
                                            System.out.println("Username Updated Successfully");
                                            System.out.println("------------------------------");
                                        } else {
                                            System.out.println("------------------------");
                                            System.out.println("Username did not Update");
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

                            case 4:
                                break;

                            default:
                                System.out.println("---------------");
                                System.out.println("Invalid Choice");
                                System.out.println("---------------");
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
                            System.out.println("Email: " + accountResultSet.getString("email"));
                            System.out.println("Available Balance: " + accountResultSet.getLong("amount"));
                            LocalDateTime dateTime = accountResultSet.getObject("created_on", LocalDateTime.class);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                            String formattedDate = dateTime.format(formatter);

                            System.out.println("Created On: " + formattedDate);
                            boolean isSus = accountResultSet.getBoolean("isSuspend");
                            if (isSus) {
                                System.out.println("Suspended");
                            } else {
                                System.out.println("Active");
                            }
                            boolean isDeleted = accountResultSet.getBoolean("isDeleted");
                            if (isDeleted) {
                                System.out.println("Account Deleted");
                            }
                            System.out.println("-------------------------");
                        }

                        break;
                    case 4:
                        // String transactionHistoryQuery = "SELECT * FROM user_transaction";
                        // PreparedStatement transactionHistoryPreparedStatement = connection.prepareStatement(transactionHistoryQuery);

                        // ResultSet transactionHistoryResultSet = transactionHistoryPreparedStatement.executeQuery();
                        // while (transactionHistoryResultSet.next()) {
                        //     System.out.println("-------------------------");

                        //     System.out.println("Transaction ID: " + transactionHistoryResultSet.getLong("id"));
                        //     System.out.println("From Whom Account Number: " + transactionHistoryResultSet.getLong("fromWhomAccNo"));
                        //     System.out.println("From Whom Name: " + transactionHistoryResultSet.getString("fromWhomName"));
                        //     System.out.println("To Whom Account Number: " + transactionHistoryResultSet.getLong("toWhomAccNo"));
                        //     System.out.println("To Whom Name: " + transactionHistoryResultSet.getString("toWhomName"));
                        //     System.out.println("Amount: " + transactionHistoryResultSet.getLong("amount"));

                        //     LocalDateTime transactionDateTime = transactionHistoryResultSet.getObject("transaction_date", LocalDateTime.class);
                        //     DateTimeFormatter transactionCreatedOnformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        //     String transactionCreatedOnformattedDate = transactionDateTime.format(transactionCreatedOnformatter);

                        //     System.out.println("Transaction Date: " + transactionCreatedOnformattedDate);
                        //     System.out.println("-------------------------");
                        // }

                        break;
                    case 5:
                        String adminPassQuery = "SELECT * FROM admin WHERE id = ?";
                        PreparedStatement adminPassPreparedStatement = connection.prepareStatement(adminPassQuery);
                        adminPassPreparedStatement.setLong(1, id);
                        ResultSet adminPassResultSet = adminPassPreparedStatement.executeQuery();
                        if (!adminPassResultSet.next()) {
                            break;
                        }
                        String adminPass = adminPassResultSet.getString("pass");
                        
                        String suspendQuery = "UPDATE user SET isSuspend = true WHERE account_number = ?";
                        PreparedStatement suspendPreparedStatement = connection.prepareStatement(suspendQuery);

                        System.out.println("----------------------------------");
                        System.out.print("Enter account number to suspend: ");
                        long suspendAccountNumber = sc.nextLong();

                        sc.nextLine();
                        System.out.print("Enter old password to updated to new password: ");
                        String enteredPass = sc.nextLine();

                        if (enteredPass.equals(adminPass)) {
                            suspendPreparedStatement.setLong(1, suspendAccountNumber);
                            int suspendRowsAffected = suspendPreparedStatement.executeUpdate();
                            if (suspendRowsAffected > 0) {
                                System.out.println("-------------------------------");
                                System.out.println("Account Suspended Successfully");
                                System.out.println("-------------------------------");
                            } else {
                                System.out.println("------------------------");
                                System.out.println("Account does not exists");
                                System.out.println("------------------------");
                            }
                        } else {
                            System.out.println("-------------------");
                            System.out.println("Incorrect Password");
                            System.out.println("-------------------");
                        }
                        break;
                    case 6:
                        String adminPass2Query = "SELECT * FROM admin WHERE id = ?";
                        PreparedStatement adminPass2PreparedStatement = connection.prepareStatement(adminPass2Query);
                        adminPass2PreparedStatement.setLong(1, id);
                        ResultSet adminPass2ResultSet = adminPass2PreparedStatement.executeQuery();
                        if (!adminPass2ResultSet.next()) {
                            break;
                        }

                        String adminPass2 = adminPass2ResultSet.getString("pass");
                        String activeQuery = "UPDATE user SET isSuspend = false WHERE account_number = ?";
                        PreparedStatement activePreparedStatement = connection.prepareStatement(activeQuery);

                        System.out.println("----------------------------------");
                        System.out.print("Enter account number to active: ");
                        long activeAccountNumber = sc.nextLong();

                        activePreparedStatement.setLong(1, activeAccountNumber);

                        sc.nextLine();
                        System.out.print("Enter old password to updated to new password: ");
                        String enteredPass2 = sc.nextLine();

                        if (enteredPass2.equals(adminPass2)) {
                            int activeRowsAffected = activePreparedStatement.executeUpdate();
                            if (activeRowsAffected > 0) {
                                System.out.println("-------------------------------");
                                System.out.println("Account Activated Successfully");
                                System.out.println("-------------------------------");
                            } else {
                                System.out.println("------------------------");
                                System.out.println("Account does not exists");
                                System.out.println("------------------------");
                            }
                        } else {
                            System.out.println("-------------------");
                            System.out.println("Incorrect Password");
                            System.out.println("-------------------");
                        }
                        
                        break;

                    case 7:
                        String deletedAccountQuery = "SELECT * FROM user WHERE isDeleted = true";
                        PreparedStatement deletedAccountPreparedStatement = connection.prepareStatement(deletedAccountQuery);

                        ResultSet deletedAccountResultSet = deletedAccountPreparedStatement.executeQuery();
                        System.out.println("---------------------");
                        System.out.println("Deleted Account List");
                        while (deletedAccountResultSet.next()) {
                            System.out.println("-------------------------");
                            System.out.println("Account Number: " + deletedAccountResultSet.getLong("account_number"));
                            System.out.println("Name: " + deletedAccountResultSet.getString("name"));
                            System.out.println("Last Available Balance: " + deletedAccountResultSet.getLong("amount"));
                            System.out.println("-------------------------");
                        }
                        break;
                    case 8:
                        String suspendedAccountQuery = "SELECT * FROM user WHERE isSuspend = true";
                        PreparedStatement suspendedAccountPreparedStatement = connection
                                .prepareStatement(suspendedAccountQuery);

                        ResultSet suspendedAccountResultSet = suspendedAccountPreparedStatement.executeQuery();
                        System.out.println("---------------------");
                        System.out.println("Suspended Account List");
                        while (suspendedAccountResultSet.next()) {
                            System.out.println("-------------------------");
                            System.out
                                    .println("Account Number: " + suspendedAccountResultSet.getLong("account_number"));
                            System.out.println("Name: " + suspendedAccountResultSet.getString("name"));
                            System.out
                                    .println("Last Available Balance: " + suspendedAccountResultSet.getLong("amount"));
                            System.out.println("-------------------------");
                        }
                        break;

                    case 9:
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

}
