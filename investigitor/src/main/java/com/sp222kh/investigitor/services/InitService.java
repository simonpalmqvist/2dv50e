package com.sp222kh.investigitor.services;

import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.FileInfoRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import com.sp222kh.investigitor.csv.*;
import com.sp222kh.investigitor.repositories.StatusRepository;
import com.sp222kh.investigitor.tasks.*;
import net.sf.jsefa.csv.CsvDeserializer;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;

@Service
public class InitService {

    @Value("${ghtorrent.dump.url}")
    private String DUMP_URL;

    @Value("${ghtorrent.dump.folder}")
    private String DUMP_FOLDER;

    @Value("${ghtorrent.dump.file}")
    private String DUMP_FILE;

    @Value("${ghtorrent.clone.folder}")
    private String CLONE_FOLDER;

    private String PROJECT_CSV_NAME = "projects.csv";
    private String PROJECT_COMMIT_CSV_NAME = "project_commits.csv";
    private String COMMIT_CSV_NAME = "commits.csv";
    private String WATCHERS_CSV_NAME = "watchers.csv";

    private StatusRepository statusRepository;
    private ProjectRepository projectRepository;
    private CommitRepository commitRepository;
    private FileInfoRepository fileInfoRepository;

    private CsvConfiguration csvConfiguration = new CsvConfiguration();

    public InitService(StatusRepository statusRepository, ProjectRepository projectRepository,
                       CommitRepository commitRepository, FileInfoRepository fileInfoRepository) {
        this.statusRepository = statusRepository;
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.fileInfoRepository = fileInfoRepository;
    }

    @PostConstruct
    public void init() throws Exception {
        File dumpArchive = new File(DUMP_FILE);
        File dumpFolder = new File(DUMP_FOLDER);

        csvConfiguration.setFieldDelimiter(',');
        csvConfiguration.getSimpleTypeConverterProvider().registerConverterType(Long.class, LongConverter.class);
        csvConfiguration.getSimpleTypeConverterProvider().registerConverterType(Boolean.class, BoolConverter.class);

        TaskRunner taskRunner = new TaskRunner(new Task[]{
                new DownloadDatabaseDumpTask(new URL(DUMP_URL), dumpArchive),
                new ExtractDatabaseDumpTask(new TarGZipUnArchiver(), dumpArchive, dumpFolder),
                new DeleteDatabaseDumpArchiveTask(dumpArchive),
                new AddActiveJavaProjectsTask(projectRepository, createDeserializer(ProjectItem.class),
                        dumpFolder.getAbsolutePath() +  "/" + PROJECT_CSV_NAME),
                new AddProjectCommitsTask(projectRepository, commitRepository,
                        createDeserializer(ProjectCommitItem.class), createDeserializer(CommitItem.class),
                        dumpFolder.getAbsolutePath() +  "/" + PROJECT_COMMIT_CSV_NAME,
                        dumpFolder.getAbsolutePath() +  "/" + COMMIT_CSV_NAME),
                new UpdateAndFilterProjectsTask(projectRepository, commitRepository,
                        createDeserializer(WatcherItem.class), dumpFolder.getAbsolutePath() + "/" + WATCHERS_CSV_NAME),
                new DeleteDatabaseDumpTask(dumpFolder),
                new DownloadSourceCodeTask(projectRepository, fileInfoRepository)
        }, statusRepository);

        taskRunner.run();
    }

    private CsvDeserializer createDeserializer(Class csvClass) {
        return CsvIOFactory.createFactory(csvConfiguration, csvClass).createDeserializer();
    }

}
