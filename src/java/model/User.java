/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;


/**
 *
 * @author ToiLaDuyGitHub
 */
public class User {
    private int userID;
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private int roleID;
    private String phoneNum;
    private String address;
    private LocalDateTime registrationDate;
    private boolean isActive;
    private boolean isResetRequested;

    public User(int userID, String username, int roleID) {
        this.userID = userID;
        this.username = username;
        this.roleID = roleID;
    }

    public User(String username, String passwordHash, String firstName, String lastName, int roleID, String phoneNum, String address, LocalDateTime registrationDate, boolean isActive, boolean isResetRequested) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleID = roleID;
        this.phoneNum = phoneNum;
        this.address = address;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.isResetRequested = isResetRequested;
    }

    //Full-attibuted constructor
    public User(int userID, String username, String passwordHash, String firstName, String lastName, int roleID, String phoneNum, String address, LocalDateTime registrationDate, boolean isActive, boolean isResetRequested) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleID = roleID;
        this.phoneNum = phoneNum;
        this.address = address;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.isResetRequested = isResetRequested;
    }
    

    public User(int userID, String username, String firstName, String lastName, String phoneNum, boolean isActive) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.isActive = isActive;
    }

    public User(int userID, String username, String firstName, String lastName, String phoneNum,int roleID, boolean isActive) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.roleID  = roleID;
        this.isActive = isActive;
    }

    public User(int userID, String username, String passwordHash, String firstName, String lastName, 
                String phoneNum, String address, int roleID) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.address = address;
        this.roleID = roleID;
    }
    public User(String username, boolean isResetRequested) {
        this.username = username;
        this.isResetRequested = isResetRequested;
    }

    public User(String username, String firstName, String lastName, boolean isResetRequested) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isResetRequested = isResetRequested;
    }
    
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsResetRequested() {
        return isResetRequested;
    }

    public void setIsResetRequested(boolean isResetRequested) {
        this.isResetRequested = isResetRequested;
    }
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}
