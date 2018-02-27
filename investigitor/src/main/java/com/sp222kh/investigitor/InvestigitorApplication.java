package com.sp222kh.investigitor;

import com.sp222kh.investigitor.csv.*;
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
import java.io.IOException;
import java.net.URL;
import java.util.*;

@SpringBootApplication
public class InvestigitorApplication {

    private static final Logger log = LoggerFactory.getLogger(InvestigitorApplication.class);

    @Value("${gittorrent.dump.url}")
    private String DUMP_URL;

    @Value("${gittorrent.dump.folder}")
    private String DUMP_FOLDER;

    private String DIR = "./";
    private String DUMP_FILE = "./latest.tar.gz";

    public static void main(String[] args) {

		SpringApplication.run(InvestigitorApplication.class, args);
	}

    @Bean
    public CommandLineRunner demo(ProjectRepository repository, CommitRepository commitRepository) {
        return (args) -> {

            // https://stackoverflow.com/questions/22163662/how-to-create-a-java-cron-job

            // Download file
            log.info("Download started");
            File dump = new File(DIR + DUMP_FILE);
            File destDir = new File(DIR);
            FileUtils.copyURLToFile(new URL(DUMP_URL), dump);
            log.info("Download finished");

            // Extract csvs
            log.info("Extracting files started");
            final TarGZipUnArchiver ua = new TarGZipUnArchiver();
            ConsoleLoggerManager manager = new ConsoleLoggerManager();
            manager.initialize();
            ua.enableLogging(manager.getLoggerForComponent("wo"));
            ua.setSourceFile(dump);
            ua.setDestDirectory(destDir);
            ua.extract();
            log.info("Extracting files finished");

            // Delete dump
            log.info("Remove TAR started");
            dump.delete();
            log.info("Remove TAR Finished");

            // Deserialize csv entries and store them in db
            CsvConfiguration config = new CsvConfiguration();
            config.setFieldDelimiter(',');
            config.getSimpleTypeConverterProvider().registerConverterType(Long.class, LongConverter.class);
            config.getSimpleTypeConverterProvider().registerConverterType(Boolean.class, BoolConverter.class);

            log.info("Importing projects started");
            Deserializer deserializer = CsvIOFactory.createFactory(config, ProjectItem.class).createDeserializer();
            deserializer.open(new FileReader(DIR + DUMP_FOLDER + "projects.csv"));

            HashMap<Long, Integer> projects = new HashMap<>();

            while (deserializer.hasNext()) {
                ProjectItem p = deserializer.next();

                if (!p.deleted && p.language.equals("Java")) {
                    repository.save(new Project(p));
                    projects.put(p.id, 0);
                }
            }

            deserializer.close(true);
            log.info("Importing projects finished");

            log.info("Importing project commits started");
            deserializer = CsvIOFactory.createFactory(config, ProjectCommitItem.class).createDeserializer();
            deserializer.open(new FileReader(DIR + DUMP_FOLDER + "project_commits.csv"));

            HashMap<Long, Set<Long>> projectCommits = new HashMap<>();

            while (deserializer.hasNext()) {
                ProjectCommitItem pc = deserializer.next();

                if (projects.containsKey(pc.projectId)) {
                    if(!projectCommits.containsKey(pc.commitId)) projectCommits.put(pc.commitId, new HashSet<>());
                    projectCommits
                            .get(pc.commitId)
                            .add(pc.projectId);
                }
            }

            deserializer.close(true);
            log.info("Importing project commits finished");

            log.info("Importing commits started");
            deserializer = CsvIOFactory.createFactory(config, CommitItem.class).createDeserializer();
            deserializer.open(new FileReader(DIR + DUMP_FOLDER + "commits.csv"));

            List<Commit> commitBulk = new ArrayList<>();

            while (deserializer.hasNext()) {
                CommitItem c = deserializer.next();

                if (projectCommits.containsKey(c.id)) {
                    for (long projectId : projectCommits.get(c.id)) {
                        commitBulk.add(new Commit(projectId, c));
                        if (commitBulk.size() > 100) {
                            commitRepository.save(commitBulk);
                            commitBulk.clear();
                        }
                    }
                }
            }
            commitRepository.save(commitBulk);

            deserializer.close(true);
            log.info("Importing commits finished");

            log.info("Importing watchers started");
            deserializer = CsvIOFactory.createFactory(config, WatcherItem.class).createDeserializer();
            deserializer.open(new FileReader(DIR + DUMP_FOLDER + "watchers.csv"));

            while (deserializer.hasNext()) {
                WatcherItem w = deserializer.next();

                if (projects.containsKey(w.projectId)) {
                    projects.put(w.projectId, projects.get(w.projectId) + 1);
                }
            }

            deserializer.close(true);
            log.info("Importing watchers finished");

            log.info("Update and filter projects started");

            for (Long id : projects.keySet()) {
                Project p = repository.findOne(id);
                p.setContributors(commitRepository.findContributorsForProject(id));
                p.setLastCommit(commitRepository.findLatestCommitDateForProject(id));
                p.setWatchers(projects.get(id));

                float activeDays = 0;

                if (p.getLastCommit() != null)
                    activeDays = (p.getLastCommit().getTime() - p.getCreatedAt().getTime()) / (1000*60*60*24);

                if(p.getWatchers() > 0 && p.getContributors() > 1 && activeDays > 99) {
                    repository.save(p);
                } else {
                    repository.delete(id);
                    commitRepository.deleteByProjectId(id);
                }
            }
            log.info("Update and filter projects finished");

            log.info("Remove duplicates started");
            for (Long id : projects.keySet()) {
                if(repository.exists(id)) {
                    List<Long> duplicates = commitRepository.findProjectDuplicates(id);

                    //Remove the original repository so it's not deleted.
                    duplicates.remove(0);

                    for(Long duplicate : duplicates) {
                        repository.delete(duplicate);
                        commitRepository.deleteByProjectId(duplicate);
                    }
                }
            }
            log.info("Remove duplicates finished");

            // Remove csvs
            log.info("Remove CSVs started");
            FileUtils.deleteDirectory(new File(DIR + DUMP_FOLDER));
            log.info("Remove CSVs finished");

            log.info("Clone Repositories started");
            for (Project p : repository.findAll()) {
                log.info("Starting " + p.getPathToRepo());
                String path = "../repos/" + p.getPathToRepo();
                String command = "git clone --depth 1 https://github.com" + p.getPathToRepo() + ".git " + path;

                try {
                    (new File(path)).mkdirs();
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("Clone Repositories finished");
        };
    }
}
