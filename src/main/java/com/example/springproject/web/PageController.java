package com.example.springproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
  @RequestMapping("/repo1")
  public String repo1() {
    return "showRepo1";
  }

  @RequestMapping("/repo2")
  public String repo2() {
    return "showRepo2";
  }
}
