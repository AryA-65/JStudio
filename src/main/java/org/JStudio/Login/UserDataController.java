package org.JStudio.Login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Class used for storing and accessing user login data
 */
public class UserDataController {
    public HashMap<String, User> users = new HashMap<>();
    private final String filePath = "src/main/resources/loginInfo.csv";
    private String finalLine;

    /**
     * Creates a file used for storing all user login data
     */
    public void createFile() {
        File csvFile = new File(filePath);
        try {
            csvFile.createNewFile();
        } catch (IOException ex) {
            System.out.println("Error.");
        }
    }

    /**
     * Reads the content of the .csv file containing all user login data
     */
    public void readFile() {
        boolean emptyFirstLine = true;
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            
            // Add all users in the hashmap until there are none left
            while ((line = reader.readLine()) != null) {
                finalLine = line;
                emptyFirstLine = false;
                String[] fields = line.split(",");
                
                // Check for empty lines/wrong format lines and skip if there are
                if (fields.length == 4) {
                    // Create a user to be stored in the list of existing users
                    User user = new User(fields[0], fields[1], Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
                    users.put(fields[0], user);
                }
            }
            
            // If no lines are present, add the admin account
            if (emptyFirstLine) {
                PrintWriter printwriter;
                try {
                    printwriter = new PrintWriter(new FileOutputStream(filePath, true), true);
                    printwriter.println("admin," + "password," + "1," + "1");
                    printwriter.close();
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Adds a user to the login data file if said user doesn't exist
     * @param user the new user to be added
     */
    public void writeToFile(User user) {
        users.clear();
        readFile();
        if (!users.containsKey(user.getUserName())) {
            PrintWriter printwriter;
            try {
                checkFinalLine();
                printwriter = new PrintWriter(new FileOutputStream(filePath, true), true);
                printwriter.println(user.toString());
                printwriter.close();
                return;
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Checks if there is an empty line after each user and adds one to avoid
     * the new user being added on the same line as the previous one
     */
    private void checkFinalLine() {
        PrintWriter printwriter;
        try {
            printwriter = new PrintWriter(new FileOutputStream(filePath, true), true);
            // If theres no empty line at the end, add one
            if (!finalLine.isEmpty() && finalLine!=null) {
                printwriter.println();
            }
            printwriter.close();
            return;
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Checks whether a username exists in the users HashMap
     * @param username the username to search for
     * @return true if the username exists, false otherwise
     */
    public boolean isUserInFile(String username) {
        return users.containsKey(username);
    }
    
    /**
     * Retrieves the password for a given username from the users HashMap
     * @param username the username to search for
     * @return the encrypted password as a String if the user is found, null otherwise
     */
    public String getPasswordForUser(String username) {
        User user = users.get(username);
        return (user != null) ? user.getPassword() : null;
    }

    /**
     * Gets all the saved users
     */
    public HashMap<String, User> getUsers() {
        return users;
    }
}
