package com.nashtech.rootkies.utils;

public class Validation {
    public static boolean validatePassword(String password){
        return password.length() >= 6 && password.length() <= 20;
    }
}
