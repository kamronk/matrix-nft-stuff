package com.kk.matrixapi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

  @GetMapping("/hello-world")
  @ResponseBody
  public String sayHello(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
    return "yo";
  }

}
