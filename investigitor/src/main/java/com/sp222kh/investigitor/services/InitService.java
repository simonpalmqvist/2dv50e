package com.sp222kh.investigitor.services;

import com.sp222kh.investigitor.repositories.*;
import com.sp222kh.investigitor.tasks.*;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;

import static org.rauschig.jarchivelib.ArchiveFormat.TAR;
import static org.rauschig.jarchivelib.CompressionType.GZIP;

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

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final String PROJECT_CSV_NAME = "projects.csv";
    private final String PROJECT_COMMIT_CSV_WILDCARD = "project_commits_split_*";
    private final String COMMIT_CSV_NAME = "commits.csv";
    private final String WATCHERS_CSV_NAME = "watchers.csv";

    private final StatusRepository statusRepository;
    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;
    private final FileInfoRepository fileInfoRepository;
    private final SoftwareMetricsRepository softwareMetricsRepository;

    public InitService(StatusRepository statusRepository, ProjectRepository projectRepository,
                       CommitRepository commitRepository, FileInfoRepository fileInfoRepository,
                       SoftwareMetricsRepository softwareMetricsRepository) {
        this.statusRepository = statusRepository;
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.fileInfoRepository = fileInfoRepository;
        this.softwareMetricsRepository = softwareMetricsRepository;
    }

    @PostConstruct
    public void init() throws Exception {
        File dumpArchive = new File(DUMP_FILE);
        File dumpFolder = new File(DUMP_FOLDER);

        CopyManager copyManager = jdbcTemplate
                .getDataSource()
                .getConnection()
                .unwrap(PGConnection.class)
                .getCopyAPI();

        TaskRunner taskRunner = new TaskRunner(new Task[]{
                new DownloadDatabaseDumpTask(new URL(DUMP_URL), dumpArchive),
                new ExtractDatabaseDumpTask(ArchiverFactory.createArchiver(TAR, GZIP), dumpArchive, dumpFolder),
                new DeleteDatabaseDumpArchiveTask(dumpArchive),
                new ImportProjectsTask(copyManager, dumpFolder.getAbsolutePath() + "/" + PROJECT_CSV_NAME),
                new ImportProjectCommitsTask(copyManager, dumpFolder.getAbsolutePath(), PROJECT_COMMIT_CSV_WILDCARD),
                new ImportCommitsTask(copyManager, dumpFolder.getAbsolutePath() + "/" + COMMIT_CSV_NAME),
                new ImportWatchersTask(copyManager, dumpFolder.getAbsolutePath() + "/" + WATCHERS_CSV_NAME),
                new DeleteDatabaseDumpTask(dumpFolder),
                new CreateIndexesTask(projectRepository, commitRepository)/*,
                new FilterProjectsTask(projectRepository, commitRepository),
                new FilterQualityProjectsTask(projectRepository, commitRepository),
                new FilterDuplicateProjectsTask(projectRepository, commitRepository),
                new DownloadSourceCodeTask(projectRepository, fileInfoRepository, CLONE_FOLDER),
                new CollectSoftwareMetricsTask(projectRepository, softwareMetricsRepository, CLONE_FOLDER)*/
        }, statusRepository);

        taskRunner.run();
    }
}
