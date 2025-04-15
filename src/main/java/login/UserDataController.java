package login;

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
    private HashMap<String, User> users = new HashMap<>();
    private final String filePath = "src/main/resources/UserData/loginInfo.csv";

    /**
     * Creates a file used for storing all user login data
     */
    public void createFile() {
        File csvFile = new File(filePath);
        try {
            csvFile.createNewFile();
            PrintWriter printwriter;
            try {
                printwriter = new PrintWriter(new FileOutputStream(filePath, true), true);
                printwriter.println("admin," + "password," + "1," + "1");
                printwriter.close();
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        } catch (IOException ex) {
            System.out.println("Error.");
        }
    }

    /**
     * Reads the content of the .csv file containing all user login data
     */
    public void readFile() {
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                // Create a user to be stored in the list of existing users
                User user = new User(fields[0], fields[1], Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
                users.put(fields[0], user);
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
        for (String username : users.keySet()) {
            if (!users.containsKey(user.getUserName())) {
                PrintWriter printwriter;
                try {
                    printwriter = new PrintWriter(new FileOutputStream(filePath, true), true);
                    printwriter.println(user.toString());
                    printwriter.close();
                    return;
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
        }
    }

    public HashMap<String, User> getUsers() {
        return users;
    }
}

