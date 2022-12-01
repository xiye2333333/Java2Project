package com.example.springproject.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.springproject.domain.Repo;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Service
public class RepoServiceImpl implements RepoService {

    @Override
    public Repo findInfo() {

        URL url = null;
        try {
//            url = new URL("https://api.github.com/repos/spring-projects/spring-boot/contributors");
            url = new URL("https://api.github.com/repos/xiye2333333/NUS-Game-Project/contributors");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept", "application/vnd.github.v3+json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Authorization","Bearer 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");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        con.disconnect();
        String json = content.toString();
        //find who committed the most
        JSONArray jsonArray = JSONArray.parseArray(json);
        int max = 0;
        String name = "";
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            int commits = (int) jsonObject.get("contributions");
            if (commits > max) {
                max = commits;
                name = (String) jsonObject.get("login");
            }
        }
//        System.out.println(name);
        Repo repo = new Repo();
        repo.setMost_active_developer(name);
        return repo;
    }
}
