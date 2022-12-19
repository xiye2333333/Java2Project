package com.example.springproject.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Repo {
  String name;
  int developerNum;
  ArrayList<Contributor> most_active_developer;
  int open_issues;
  int close_issues;
  double issue_solve_average;
  double issue_solve_variance;
  int issue_solve_max_day;
  int issue_solve_min_day;
  int releases;
  int commit_times;
  Date[] commit_time;
  int[] commit_between_releases;
  List<Map.Entry<String, Integer>> commit_frequency;
  String[] commit_frequency_time;
  int[] commit_frequency_num;
  String comments_body_wordCloud_path;
  String issues_body_wordCloud_path;
  String issues_title_wordCloud_path;
  int[] commit_weekday;

  public String[] getCommit_frequency_time() {
    return commit_frequency_time;
  }

  public void setCommit_frequency_time(String[] commit_frequency_time) {
    this.commit_frequency_time = commit_frequency_time;
  }

  public int[] getCommit_frequency_num() {
    return commit_frequency_num;
  }

  public void setCommit_frequency_num(int[] commit_frequency_num) {
    this.commit_frequency_num = commit_frequency_num;
  }

  public double getIssue_solve_variance() {
    return issue_solve_variance;
  }

  public void setIssue_solve_variance(double issue_solve_variance) {
    this.issue_solve_variance = issue_solve_variance;
  }

  public Date[] getCommit_time() {
    return commit_time;
  }

  public void setCommit_time(Date[] commit_time) {
    this.commit_time = commit_time;
  }

  public int[] getCommit_between_releases() {
    return commit_between_releases;
  }

  public void setCommit_between_releases(int[] commit_between_releases) {
    this.commit_between_releases = commit_between_releases;
  }

  public Repo() {
    this.name = "name";
    this.developerNum = 0;
    this.most_active_developer = new ArrayList<>();
    this.open_issues = 0;
    this.close_issues = 0;
    this.issue_solve_average = 0;
    this.issue_solve_max_day = 0;
    this.issue_solve_min_day = 0;
    this.releases = 0;
    this.commit_times = 0;
    this.commit_time = new Date[0];
    this.commit_between_releases = new int[0];
    this.commit_frequency = new ArrayList<>();
    this.commit_frequency_time = new String[0];
    this.commit_frequency_num = new int[0];
    this.commit_weekday = new int[7];
  }

  public int[] getCommit_weekday() {
    return commit_weekday;
  }

  public void setCommit_weekday(int[] commit_weekday) {
    this.commit_weekday = commit_weekday;
  }

  public String getName() {
    return name;
  }

  public int getDeveloperNum() {
    return developerNum;
  }

  public ArrayList<Contributor> getMost_active_developer() {
    return most_active_developer;
  }

  public int getOpen_issues() {
    return open_issues;
  }


  public int getClose_issues() {
    return close_issues;
  }

  public double getIssue_solve_average() {
    return issue_solve_average;
  }

  public int getIssue_solve_max_day() {
    return issue_solve_max_day;
  }

  public int getIssue_solve_min_day() {
    return issue_solve_min_day;
  }

  public int getReleases() {
    return releases;
  }

  public int getCommit_times() {
    return commit_times;
  }


  public void setName(String name) {
    this.name = name;
  }

  public void setDeveloperNum(int developerNum) {
    this.developerNum = developerNum;
  }

  public void setMost_active_developer(ArrayList most_active_developer) {
    this.most_active_developer = most_active_developer;
  }

  public void setOpen_issues(int open_issues) {
    this.open_issues = open_issues;
  }

  public void setClose_issues(int close_issues) {
    this.close_issues = close_issues;
  }

  public void setIssue_solve_average(double issue_solve_average) {
    this.issue_solve_average = issue_solve_average;
  }

  public void setIssue_solve_max_day(int issue_solve_max_day) {
    this.issue_solve_max_day = issue_solve_max_day;
  }

  public void setIssue_solve_min_day(int issue_solve_min_day) {
    this.issue_solve_min_day = issue_solve_min_day;
  }

  public void setReleases(int releases) {
    this.releases = releases;
  }

  public void setCommit_times(int commit_times) {
    this.commit_times = commit_times;
  }

  public List<Map.Entry<String, Integer>> getCommit_frequency() {
    return commit_frequency;
  }

  public void setCommit_frequency(List<Map.Entry<String, Integer>> commit_frequency) {
    this.commit_frequency = commit_frequency;
    this.commit_frequency_time = new String[commit_frequency.size()];
    this.commit_frequency_num = new int[commit_frequency.size()];
    for (Map.Entry<String, Integer> entry : commit_frequency) {
      int month = Integer.parseInt(entry.getKey().split("-")[1]) + 1;
      this.commit_frequency_time[commit_frequency.indexOf(entry)] =
          entry.getKey().split("-")[0] + "-" + month;
      this.commit_frequency_num[commit_frequency.indexOf(entry)] = entry.getValue();
    }
//        System.out.println("commit_frequency_time: " + Arrays.toString(commit_frequency_time));
  }

  public String getComments_body_wordCloud_path() {
    return comments_body_wordCloud_path;
  }

  public void setComments_body_wordCloud_path(String comments_body_wordCloud_path) {
    this.comments_body_wordCloud_path = comments_body_wordCloud_path;
  }

  public String getIssues_body_wordCloud_path() {
    return issues_body_wordCloud_path;
  }

  public void setIssues_body_wordCloud_path(String issues_body_wordCloud_path) {
    this.issues_body_wordCloud_path = issues_body_wordCloud_path;
  }

  public String getIssues_title_wordCloud_path() {
    return issues_title_wordCloud_path;
  }

  public void setIssues_title_wordCloud_path(String issues_title_wordCloud_path) {
    this.issues_title_wordCloud_path = issues_title_wordCloud_path;
  }
}
