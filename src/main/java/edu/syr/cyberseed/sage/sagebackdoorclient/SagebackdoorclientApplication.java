package edu.syr.cyberseed.sage.sagebackdoorclient;

import edu.syr.cyberseed.sage.sagebackdoorclient.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
public class SagebackdoorclientApplication implements CommandLineRunner {

	@Autowired
	private DatabaseService databaseService;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SagebackdoorclientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	        System.out.println(databaseService.parseCommandline(args));

		exit(0);

	}
}
