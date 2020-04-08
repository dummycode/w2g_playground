package edu.gatech.w2gplayground.Models;

public class User extends Model {
    String id;
    String authKey;
    String firstName;
    String lastName;

    public User(String id, String authKey, String firstName, String lastName) {
        this.id = id;
        this.authKey = authKey;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getAuthKey() {
        return authKey;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
