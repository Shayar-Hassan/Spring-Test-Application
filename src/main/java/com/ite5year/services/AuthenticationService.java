package com.ite5year.services;

import com.ite5year.models.ApplicationUser;

public class AuthenticationService {

    ApplicationUser loggedInUser;


    public ApplicationUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(ApplicationUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
