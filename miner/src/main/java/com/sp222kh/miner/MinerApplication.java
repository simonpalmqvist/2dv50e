package com.sp222kh.miner;


import com.sp222kh.miner.csv.*;
import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.*;

@SpringBootApplication
public class MinerApplication {

    private static final Logger log = LoggerFactory.getLogger(MinerApplication.class);

    @Value("${gittorrent.dump.url.latest}")
    private String DUMP_URL;
    private String DUMP_PATH = "./latest.tar.gz";

    public static void main(String[] args) {

		SpringApplication.run(MinerApplication.class, args);
	}

    @Bean
    public CommandLineRunner demo(ProjectRepository repository, CommitRepository commitRepository) {
        return (args) -> {

            // https://stackoverflow.com/questions/22163662/how-to-create-a-java-cron-job

            // Download file
            File dump = new File(DUMP_PATH);
            File destDir = new File("./");
            FileUtils.copyURLToFile(new URL(DUMP_URL), dump);

            // Extract csvs
            final TarGZipUnArchiver ua = new TarGZipUnArchiver();
            ConsoleLoggerManager manager = new ConsoleLoggerManager();
            manager.initialize();
            ua.enableLogging(manager.getLoggerForComponent("wo"));
            ua.setSourceFile(dump);
            ua.setDestDirectory(destDir);
            ua.extract();

            // Delete dump
            dump.delete();

            // Deserialize csv entries and store them in db
            // http://mariemjabloun.blogspot.de/2014/10/jsefa-tutorial-and-how-to-support-for.html
            CsvConfiguration config = new CsvConfiguration();
            config.setFieldDelimiter(',');
            config.getSimpleTypeConverterProvider().registerConverterType(Long.class, LongConverter.class);
            config.getSimpleTypeConverterProvider().registerConverterType(Boolean.class, BoolConverter.class);

            Deserializer deserializer = CsvIOFactory.createFactory(config, ProjectItem.class).createDeserializer();
            deserializer.open(new FileReader("./latest/projects.csv"));

            HashSet<Long> projects = new HashSet<>();
            HashMap<Long, Set<Long>> projectCommits = new HashMap<>();

            while (deserializer.hasNext()) {
                ProjectItem p = deserializer.next();

                if (!p.deleted && p.language.equals("Java")) {
                    repository.save(new Project(p));
                    projects.add(p.id);
                }
            }

            deserializer.close(true);

            deserializer = CsvIOFactory.createFactory(config, ProjectCommitItem.class).createDeserializer();
            deserializer.open(new FileReader("./latest/project_commits.csv"));

            while (deserializer.hasNext()) {
                ProjectCommitItem pc = deserializer.next();

                if (projects.contains(pc.projectId)) {
                    if(!projectCommits.containsKey(pc.commitId)) projectCommits.put(pc.commitId, new HashSet<>());
                    projectCommits
                            .get(pc.commitId)
                            .add(pc.projectId);
                }
            }

            deserializer.close(true);

            deserializer = CsvIOFactory.createFactory(config, CommitItem.class).createDeserializer();
            deserializer.open(new FileReader("./latest/commits.csv"));

            while (deserializer.hasNext()) {
                CommitItem c = deserializer.next();

                if (projectCommits.containsKey(c.id)) {
                    for (long projectId : projectCommits.get(c.id)) {
                        commitRepository.save(new Commit(projectId, c));
                    }
                }
            }

            deserializer.close(true);

            // Remove csvs
            FileUtils.deleteDirectory(new File("./latest"));
        };
    }
}
