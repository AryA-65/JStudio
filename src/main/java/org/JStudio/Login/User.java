package org.JStudio.Login;

/**
 * User model class that stores information for a user
 * @author Theodore, Ahmet
 */
public class User {
    private String userName;
    private String password;
    private int key1;
    private int key2;

    // Constructor that creates a user with a username, password, and
    // encryption/decryption keys
    public User(String userName, String password, int key1, int key2) {
        this.userName = userName;
        this.password = password;
        this.key1 = key1;
        this.key2 = key2;
    }

    /**
     * Converts the user to a string
     * @return the user in string format
     */
    @Override
    public String toString() {
        return userName + "," + password + "," + key1 + "," + key2;
    }

    /**
     * Gets the username of a user
     * @return the user's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the password of a user
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the first key of a user
     * @return the user's first key
     */
    public int getKey1() {
        return key1;
    }

    /**
     * Gets the second key of a user
     * @return the user's second key
     */
    public int getKey2() {
        return key2;
    }
}
