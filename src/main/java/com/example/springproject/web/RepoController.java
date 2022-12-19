package com.example.springproject.web;

import com.example.springproject.domain.Repo;
import com.example.springproject.service.RepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;


@RestController
@RequestMapping("/repo")
public class RepoController {

    /**
     * 读取json文件，返回json串
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Autowired
    private RepoService repoService;

    @GetMapping("/getInfo1")
    public Repo getInfo_1(){
        return repoService.findInfo1();
    }

    @GetMapping("/refreshInfo")
    public int refreshInfo(){
        return repoService.refreshInfo();
    }

    @GetMapping("/getInfo2")
    public Repo getInfo_2(){
        return repoService.findInfo2();
    }

}
