package login;

public class MainLogin {
    public static void main(String[] args) {
        User user1 = new User("user1", "abcefg", 5, 20);
        User user2 = new User("user2", "abcd", 6, 20);

        UserDataController u = new UserDataController();
        u.createFile();
        u.writeToFile(user1);
        u.writeToFile(user2);

        System.out.println(u.getUsers().toString());
    }
}
