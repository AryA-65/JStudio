package org.JStudio.Login;

public class MainLogin {
    public static void main(String[] args) {
        User user1 = new User("user1", "abcefg", 5, 20);
        User user2 = new User("user2", "abcd", 6, 20);
        User user3 = new User("user1", "abcejkeefg", 5, 20);
        User user4 = new User("user2", "abcjenioed", 6, 20);
        User user5 = new User("user3", "abcekvefg", 5, 20);
        User user6 = new User("user4", "jkfjj", 6, 20);
        

        UserDataController u = new UserDataController();
        u.createFile();
        u.writeToFile(user1);
        u.writeToFile(user2);
        u.writeToFile(user3);
        u.writeToFile(user4);
        u.writeToFile(user5);
        u.writeToFile(user6);
u.readFile();

        System.out.println(u.getUsers().toString());
    }
}
