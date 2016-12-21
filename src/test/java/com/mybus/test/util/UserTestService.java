package com.mybus.test.util;

import com.mybus.model.User;
import com.mybus.model.UserType;

/**
 * Created by srinikandula on 12/13/16.
 */
public class UserTestService {
    public static User createNew() {
        User user = new User("fname", "lname", "uname", "pwd", "e@email.com", 1234567, "add1", "add2",
                "city", "state", UserType.ADMIN,"plan3");
        return user;
    }

}
