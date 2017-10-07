package edu.syr.cyberseed.sage.sagebackdoorclient.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Value("${smirk.backdoor.prop1:unknown}")
    private String prop1;

    public String parseCommandline(String... args) {

        System.out.println("prop1 =" + prop1);
        System.out.println("args = ");
        for (String arg: args) {
            System.out.println(arg);
        }

        return "";
    }
}
