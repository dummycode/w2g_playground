package edu.gatech.w2gplayground.Models.Generators;

import edu.gatech.w2gplayground.Models.User;

/**
 * Generator for User model
 */
public class UserGenerator extends Generator {
    public static User user() {
        return new User(randId(), randId(),"John", "Doe");
    }

    public static User userWithAuthKey(String authKey) {
        return new User(randId(), authKey, "John", "Doe");
    }
}
