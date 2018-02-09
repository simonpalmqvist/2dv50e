package com.sp222kh.miner;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MinerApplication {

    private static final Logger log = LoggerFactory.getLogger(MinerApplication.class);


    public static void main(String[] args) {

		SpringApplication.run(MinerApplication.class, args);
	}

    @Bean
    public CommandLineRunner demo(ProjectRepository repository) {
        return (args) -> {
            // save a couple of projects
            repository.save(new Project("Java something", "A java project that bla bla"));
            repository.save(new Project("Chloe", "O'Brian"));
            repository.save(new Project("Kim", "Bauer"));
            repository.save(new Project("David", "Palmer"));
            repository.save(new Project("Michelle", "Dessler"));

            // fetch all projects
            log.info("Projects found with findAll():");
            log.info("-------------------------------");
            for (Project project : repository.findAll()) {
                log.info(project.toString());
            }
            log.info("");

            // fetch an individual project by ID
            Project project = repository.findOne(1L);
            log.info("Project found with findOne(1L):");
            log.info("--------------------------------");
            log.info(project.toString());
            log.info("");

            // fetch projects by name
            log.info("Project found with findByName('Java something'):");
            log.info("--------------------------------------------");
            for (Project p : repository.findByName("Java something")) {
                log.info(p.toString());
            }
            log.info("");
        };
    }
}
