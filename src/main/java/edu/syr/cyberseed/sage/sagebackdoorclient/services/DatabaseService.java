package edu.syr.cyberseed.sage.sagebackdoorclient.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Value("${smirk.backdoor.prop1:unknown}")
    private String prop1;

    public String parseCommandline(String... args) {

        if (args.length > 0) {
            switch (args[0]) {
                case "setITAdmin":
                    System.out.println("setITAdmin");
                    break;
                case "loadData":
                    System.out.println("loadData");
                    break;
                case "getBackupCfg":
                    System.out.println("getBackupCfg");
                    break;
                case "loadBackupCfg":
                    System.out.println("loadBackupCfg");
                    break;
                case "DumpDB":
                    System.out.println("DumpDB");
                    break;
                default:
                    System.out.println("Invalid commandline, options are: <setITAdmin|loadData|getBackupCfg|loadBackupCfg|DumpDB");
            }
        }
        else {
            System.out.println("No commandline parameters specified.");
            }

        return "";
    }
}
