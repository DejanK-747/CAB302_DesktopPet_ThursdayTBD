package com.cab302thursdaytbd.Model;

public interface IUserDAO {

    int registerUser(String username, String password);

    int loginUser(String username, String password);
}