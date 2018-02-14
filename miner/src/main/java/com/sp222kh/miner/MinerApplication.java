package com.sp222kh.miner;


import com.sp222kh.miner.csv.BoolConverter;
import com.sp222kh.miner.csv.LongConverter;
import com.sp222kh.miner.csv.ProjectItem;
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

@SpringBootApplication
public class MinerApplication {

    private static final Logger log = LoggerFactory.getLogger(MinerApplication.class);

    @Value("${gittorrent.dump.url.latest}")
    String gittorrentUrl;

    String DUMP_PATH = "./latest.tar.gz";

    public static void main(String[] args) {

		SpringApplication.run(MinerApplication.class, args);
	}

    @Bean
    public CommandLineRunner demo(ProjectRepository repository) {
        return (args) -> {

            // Download file
            File dump = new File(DUMP_PATH);
            File destDir = new File("./");
            FileUtils.copyURLToFile(new URL(gittorrentUrl), dump);

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

            while (deserializer.hasNext()) {
                ProjectItem p = deserializer.next();
                if (!p.deleted && p.language.equals("Java")) repository.save(new Project(p));
            }
            deserializer.close(true);

            // Remove csvs
            FileUtils.deleteDirectory(new File("./latest"));

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
