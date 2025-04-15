package login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class UserDataController {
    private HashMap<String, User> users = new HashMap<>();
    private final String filePath = "src/main/resources/UserData/loginInfo.csv";
    
    public void createFile() {
        File csvFile = new File(filePath);
            try {
                csvFile.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error.");
            }
    }
    
    public void readFile() {
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                
                User user = new User(fields[0], fields[1], Integer.parseInt(fields[2]), Integer.parseInt(fields[3]));
                users.put(fields[0], user);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public void writeToFile(User user) {
        users.clear();
        readFile();
        for (HashMap.Entry<String, User> userList : users.entrySet()) {
            if (!user.getUserName().equals(userList.getKey())) {
                PrintWriter printwriter;
                try {
                    printwriter = new PrintWriter(new FileOutputStream(filePath, true), true);
                    printwriter.println(user.toString());
                    printwriter.close();
                    users.put(user.getUserName(), user);
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
