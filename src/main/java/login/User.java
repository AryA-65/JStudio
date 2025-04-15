package login;


public class User {
    private String userName;
    private String password;
    private int key1;
    private int key2;

    public User(String userName, String password, int key1, int key2) {
        this.userName = userName;
        this.password = password;
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public String toString() {
        return userName + "," + password + "," + key1 + "," + key2;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getKey1() {
        return key1;
    }

    public int getKey2() {
        return key2;
    }
}
