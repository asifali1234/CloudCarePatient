package com.genesis.cloudcarepatient;

import java.io.Serializable;

/**
 * Created by asif on 25/11/17.
 */

public class PatientBean implements Serializable {

    private String name;
    private String email;
    private String phn;
    private String address;
    private String bloodGroup;
    private String gender;
    private String age;
    private String existingUser;
    private String googleID;
    private String photoURL;
    private String adhaarUID;
    private String hash;
    private String nextHash;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getExistingUser() {
        return existingUser;
    }

    public void setExistingUser(String existingUser) {
        this.existingUser = existingUser;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getAdhaarUID() {
        return adhaarUID;
    }

    public void setAdhaarUID(String adhaarUID) {
        this.adhaarUID = adhaarUID;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getNextHash() {
        return nextHash;
    }

    public void setNextHash(String nextHash) {
        this.nextHash = nextHash;
    }
}
