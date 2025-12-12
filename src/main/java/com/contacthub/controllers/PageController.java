package com.contacthub.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @GetMapping("/home")
    public String home(){
        return"Jay Ganesh...!";
    }

}











