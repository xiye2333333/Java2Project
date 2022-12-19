package com.example.springproject.service;

import static com.example.springproject.web.RepoController.readJsonFile;

import com.alibaba.fastjson.JSONArray;
import com.example.springproject.domain.Contributor;
import com.example.springproject.domain.Repo;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RepoServiceImpl implements RepoService {

  @Override
  public Repo findInfo1() {
    Repo repo = new Repo();
    AnalyzeContributors(repo, "src/main/resources/static/scikit_opt/contributors.json");
    AnalyzeIssues(repo, "src/main/resources/static/scikit_opt/issues.json");
    AnalyzeReleasesAndCommits(repo, "src/main/resources/static/scikit_opt/releases.json",
        "src/main/resources/static/scikit_opt/commits.json");
    AnalyzeIssuesText(repo, "src/main/resources/static/scikit_opt/issues.json",
        "src/main/resources/static/scikit_opt/issues_comments.json",
        "src/main/resources/static/scikit_opt/issues_title_wordCloud.png");
    return repo;

  }

  @Override
  public Repo findInfo2() {
    Repo repo = new Repo();
    AnalyzeContributors(repo, "src/main/resources/static/docker/contributors.json");
    AnalyzeIssues(repo, "src/main/resources/static/docker/issues.json");
    AnalyzeReleasesAndCommits(repo, "src/main/resources/static/docker/releases.json",
        "src/main/resources/static/docker/commits.json");
    AnalyzeIssuesText(repo, "src/main/resources/static/docker/issues.json",
        "src/main/resources/static/docker/issues_comments.json",
        "src/main/resources/static/docker/issues_title_wordCloud.png");
    return repo;
  }

  //sort contributors by number of contributions
  private void AnalyzeContributors(Repo repo, String filePath) {
    String jsonStr = readJsonFile(filePath);
    JSONArray jsonArray = JSONArray.parseArray(jsonStr);
    ArrayList<Contributor> contributors = new ArrayList<>();
    for (int i = 0; i < jsonArray.size(); i++) {
      String name = jsonArray.getJSONObject(i).getString("login");
      int contributions = jsonArray.getJSONObject(i).getIntValue("contributions");
      Contributor contributor = new Contributor();
      contributor.setName(name);
      contributor.setCommits(contributions);
      contributors.add(contributor);
    }
    repo.setDeveloperNum(contributors.size());
    contributors.sort((o1, o2) -> o2.getCommits() - o1.getCommits());
    //record first 10 contributors to repo
    int length = Math.min(contributors.size(), 10);
    for (int i = 0; i < length; i++) {
      repo.getMost_active_developer().add(contributors.get(i));
    }
  }

  private void AnalyzeIssues(Repo repo, String filePath) {
    String jsonStr = readJsonFile(filePath);
    JSONArray jsonArray = JSONArray.parseArray(jsonStr);
    int open_issues = 0;
    int close_issues = 0;
    int total_days = 0;
    int max_days = 0;
    int min_days = Integer.MAX_VALUE;
    ArrayList<Double> solve_days = new ArrayList<>();
    for (int i = 0; i < jsonArray.size(); i++) {
      String state = jsonArray.getJSONObject(i).getString("state");
      if (state.equals("open")) {
        open_issues++;
      } else {
        close_issues++;
        String created_at = jsonArray.getJSONObject(i).getString("created_at");
        String closed_at = jsonArray.getJSONObject(i).getString("closed_at");
        int days = getDays(created_at, closed_at);
        total_days += days;
        max_days = Math.max(max_days, days);
        min_days = Math.min(min_days, days);
        solve_days.add((double) days);
      }
    }
    repo.setOpen_issues(open_issues);
    repo.setClose_issues(close_issues);
    repo.setIssue_solve_average((double) total_days / close_issues);
    repo.setIssue_solve_variance(getVariance(solve_days));
    repo.setIssue_solve_max_day(max_days);
    repo.setIssue_solve_min_day(min_days);
  }

  private void AnalyzeIssuesText(Repo repo, String issuesFilePath, String commentsFilePath,
                                 String wordCloudFilePath) {
    String issuesJsonStr = readJsonFile(issuesFilePath);
    String commentsJsonStr = readJsonFile(commentsFilePath);
    JSONArray issuesJsonArray = JSONArray.parseArray(issuesJsonStr);
    JSONArray commentsJsonArray = JSONArray.parseArray(commentsJsonStr);
    ArrayList<String> issues_title = new ArrayList<>();
    ArrayList<String> issues_body = new ArrayList<>();
    ArrayList<String> comments_body = new ArrayList<>();
    for (int i = 0; i < issuesJsonArray.size(); i++) {
      String title = issuesJsonArray.getJSONObject(i).getString("title");
      String body = issuesJsonArray.getJSONObject(i).getString("body");
      issues_title.add(title);
      if (body != null) {
        issues_body.add(body);
      }
    }
    for (int i = 0; i < commentsJsonArray.size(); i++) {
      String body = commentsJsonArray.getJSONObject(i).getString("body");
      comments_body.add(body);
    }
    //create word cloud
    final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
//        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

    final List<WordFrequency> wordFrequencies_issues_title;
    wordFrequencies_issues_title = frequencyAnalyzer.load(issues_title);
    final Dimension dimension = new Dimension(600, 600);
    final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
    wordCloud.setPadding(2);
    wordCloud.setBackground(new CircleBackground(300));
    wordCloud.setColorPalette(
        new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1),
            new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
    wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
    wordCloud.build(wordFrequencies_issues_title);
    wordCloud.writeToFile(wordCloudFilePath);
//        System.out.println(issues_body);
    repo.setIssues_title_wordCloud_path(wordCloudFilePath);


    final FrequencyAnalyzer frequencyAnalyzer2 = new FrequencyAnalyzer();
    final List<WordFrequency> wordFrequencies_issues_body;
    wordFrequencies_issues_body = frequencyAnalyzer2.load(issues_body);
    final Dimension dimension2 = new Dimension(600, 600);
    final WordCloud wordCloud2 = new WordCloud(dimension2, CollisionMode.PIXEL_PERFECT);
    wordCloud2.setPadding(2);
    wordCloud2.setBackground(new CircleBackground(300));
    wordCloud2.setColorPalette(
        new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1),
            new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
    wordCloud2.setFontScalar(new SqrtFontScalar(10, 40));
    wordCloud2.build(wordFrequencies_issues_body);
    wordCloud2.writeToFile(wordCloudFilePath.replace("issues_title", "issues_body"));
    repo.setIssues_body_wordCloud_path(wordCloudFilePath.replace("issues_title", "issues_body"));

    final FrequencyAnalyzer frequencyAnalyzer3 = new FrequencyAnalyzer();
    final List<WordFrequency> wordFrequencies_comments_body;
    wordFrequencies_comments_body = frequencyAnalyzer3.load(comments_body);
    final Dimension dimension3 = new Dimension(600, 600);
    final WordCloud wordCloud3 = new WordCloud(dimension3, CollisionMode.PIXEL_PERFECT);
    wordCloud3.setPadding(2);
    wordCloud3.setBackground(new CircleBackground(300));
    wordCloud3.setColorPalette(
        new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1),
            new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
    wordCloud3.setFontScalar(new SqrtFontScalar(10, 40));
    wordCloud3.build(wordFrequencies_comments_body);
    wordCloud3.writeToFile(wordCloudFilePath.replace("issues_title", "comments_body"));
    repo.setComments_body_wordCloud_path(
        wordCloudFilePath.replace("issues_title", "comments_body"));

  }

  private int getDays(String created_at, String closed_at) {
    String[] created = created_at.split("T")[0].split("-");
    String[] closed = closed_at.split("T")[0].split("-");
    int year = Integer.parseInt(closed[0]) - Integer.parseInt(created[0]);
    int month = Integer.parseInt(closed[1]) - Integer.parseInt(created[1]);
    int day = Integer.parseInt(closed[2]) - Integer.parseInt(created[2]);
    return year * 365 + month * 30 + day;
  }

  //number of releases,number of commits between releases
  private void AnalyzeReleasesAndCommits(Repo repo, String releaseFilePath, String commitFilePath) {
    String releaseJsonStr = readJsonFile(releaseFilePath);
    JSONArray releaseJsonArray = JSONArray.parseArray(releaseJsonStr);
    String commitJsonStr = readJsonFile(commitFilePath);
    JSONArray commitJsonArray = JSONArray.parseArray(commitJsonStr);
    //get number of commits between releases
    int releaseNum = releaseJsonArray.size();
    Date[] releaseDates = new Date[releaseNum];
    int commitNum = commitJsonArray.size();
    Date[] commitDates = new Date[commitNum];
    int[] commits_between_releases = new int[releaseNum - 1];
    for (int i = 0; i < releaseNum; i++) {
      String releaseDate = releaseJsonArray.getJSONObject(i).getString("published_at");
      releaseDates[i] = convertUTC(releaseDate);
    }
    for (int i = 0; i < releaseNum - 1; i++) {
      for (int j = 0; j < commitNum; j++) {
        String commitDate =
            commitJsonArray.getJSONObject(j).getJSONObject("commit").getJSONObject("committer")
                .getString("date");
        Date date = convertUTC(commitDate);
        commitDates[j] = date;
        if (date.before(releaseDates[i]) && date.after(releaseDates[i + 1])) {
          commits_between_releases[i]++;
        }
      }
    }
    repo.setReleases(releaseNum);
    repo.setCommit_between_releases(commits_between_releases);
    repo.setCommit_time(commitDates);

    Map<String, Integer> commitMap = new HashMap<>();
    int[] commit_weekday = new int[7];
    for (int i = 0; i < commitNum; i++) {
      String commitDate =
          commitJsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("committer")
              .getString("date");
      Date date = convertUTC(commitDate);
      String month = date.getYear() + "-" + date.getMonth();
      if (commitMap.containsKey(month)) {
        commitMap.put(month, commitMap.get(month) + 1);
      } else {
        commitMap.put(month, 1);
      }
      commit_weekday[date.getDay()]++;
    }
    repo.setCommit_weekday(commit_weekday);
    List<Map.Entry<String, Integer>> list = new ArrayList<>(commitMap.entrySet());
    list.sort(new Comparator<Map.Entry<String, Integer>>() {
      @Override
      public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        int year1 = Integer.parseInt(o1.getKey().split("-")[0]);
        int year2 = Integer.parseInt(o2.getKey().split("-")[0]);
        int month1 = Integer.parseInt(o1.getKey().split("-")[1]);
        int month2 = Integer.parseInt(o2.getKey().split("-")[1]);
        if (year1 == year2) {
          return month1 - month2;
        } else {
          return year1 - year2;
        }
      }
    });
    repo.setCommit_frequency(list);


  }

  //UTC to Date
  private Date convertUTC(String utc) {
    String[] date = utc.split("T")[0].split("-");
    String[] time = utc.split("T")[1].split("Z")[0].split(":");
    int year = Integer.parseInt(date[0]);
    int month = Integer.parseInt(date[1]);
    int day = Integer.parseInt(date[2]);
    int hour = Integer.parseInt(time[0]);
    int minute = Integer.parseInt(time[1]);
    int second = Integer.parseInt(time[2]);
    return new Date(year, month, day, hour, minute, second);
  }


  private ArrayList<String> urls = new ArrayList<String>() {
    {
//            add("https://api.github.com/repos/spring-projects/spring-boot");
      add("https://api.github.com/repos/guofei9987/scikit-opt");
      add("https://api.github.com/repos/jenkinsci/docker");
    }
  };

  private ArrayList<String> dirs = new ArrayList<String>() {
    {
//            add("src/main/resources/static/spring_boot");
      add("src/main/resources/static/scikit_opt");
      add("src/main/resources/static/docker");
    }
  };

  private ArrayList<String> fileNames = new ArrayList<String>() {
    {
      add("contributors");
      add("commits");
      add("issues");
      add("releases");
      add("issues_comments");
    }
  };

  @Override
  public int refreshInfo() {
    for (int i = 0; i < urls.size(); i++) {
      for (int j = 0; j < fileNames.size(); j++) {
        String url;
        if (fileNames.get(j).contains("_")) {
          url = urls.get(i) + "/" + fileNames.get(j).replace("_", "/");
        } else {
          url = urls.get(i) + "/" + fileNames.get(j);
        }
//                String url = urls.get(i) + "/" + fileNames.get(j);
        String dir = dirs.get(i) + "/" + fileNames.get(j) + ".json";
        if (fileNames.get(j).equals("issues")) {
          url = url + "?state=all&per_page=100&page=";
        } else {
          url = url + "?per_page=100&page=";
        }
        int page = 1;
        int total = 0;
        JSONArray data = new JSONArray();
        while (true) {
          try {
            System.out.println(url + page);
            URL url1 = new URL(url + page);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Authorization", "*************");//token
            connection.connect();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
              builder.append(line);
            }
            reader.close();
            connection.disconnect();

            String json = builder.toString();
            JSONArray jsonArray = JSONArray.parseArray(json);
            if (jsonArray.size() == 0) {
              break;
            }
            data.addAll(jsonArray);
            total += jsonArray.size();
            page++;
          } catch (MalformedURLException e) {
            e.printStackTrace();
          } catch (ProtocolException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        File file = new File(dir);
        try {
          Files.createDirectories(Path.of(dirs.get(i)));
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (!file.exists()) {
          try {
            file.createNewFile();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        FileWriter fileWriter = null;
        try {
          fileWriter = new FileWriter(file);
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          fileWriter.write(data.toString());
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          fileWriter.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          fileWriter.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return 200;
  }


  public static double getVariance(ArrayList<Double> arr) {
    double sum = 0;
    for (int i = 0; i < arr.size(); i++) {
      sum += arr.get(i);
    }
    double mean = sum / arr.size();
    double temp = 0;
    for (int i = 0; i < arr.size(); i++) {
      temp += (arr.get(i) - mean) * (arr.get(i) - mean);
    }
    return temp / arr.size();

  }

}
