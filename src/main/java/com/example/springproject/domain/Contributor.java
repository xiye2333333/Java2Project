package com.example.springproject.domain;

public class Contributor {
  String name;
  int commits;
  int issues;
  int pull_requests;

  public Contributor(String name, int commits, int issues, int pull_requests) {
    this.name = name;
    this.commits = commits;
    this.issues = issues;
    this.pull_requests = pull_requests;
  }

  public Contributor() {
    this.name = "";
    this.commits = 0;
    this.issues = 0;
    this.pull_requests = 0;
  }

  public String getName() {
    return name;
  }

  public int getCommits() {
    return commits;
  }

  public int getIssues() {
    return issues;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setIssues(int issues) {
    this.issues = issues;
  }

  public void setPull_requests(int pull_requests) {
    this.pull_requests = pull_requests;
  }

  public int getPull_requests() {
    return pull_requests;
  }

  public void setCommits(int commits) {
    this.commits = commits;
  }
}
