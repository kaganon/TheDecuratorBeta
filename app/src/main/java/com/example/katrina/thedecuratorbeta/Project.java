package com.example.katrina.thedecuratorbeta;

public class Project {
    private String title;
    private String budget;

    public Project() {
    }

    public Project(String title, String budget) {
        this.title = title;
        this.budget = budget;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }
}
