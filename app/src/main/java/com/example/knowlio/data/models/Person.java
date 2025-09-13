package com.example.knowlio.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a person with name and biography from the whoWereThey section
 */
public class Person {
    @SerializedName("name")
    public String name;
    
    @SerializedName("bio")
    public String bio;
    
    public Person() {}
    
    public Person(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    @Override
    public String toString() {
        if (name == null || bio == null) {
            return "Unknown Person";
        }
        return name + " - " + bio;
    }
}

