package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class FilterQualityProjectsTask implements Task {

    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;
    private Connection connection;
    private static final Logger log = LoggerFactory.getLogger(FilterQualityProjectsTask.class);

    public FilterQualityProjectsTask(ProjectRepository projectRepository, CommitRepository commitRepository, Connection connection) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.connection = connection;
    }

    @Override
    public void run() throws Exception {
        log.info("deleteing repositories");
        projectRepository.deleteNonQualityProjects();
        log.info("deleteing watchers");
        projectRepository.deleteWatchersWithoutProject();
        log.info("deleteing project commits");
        commitRepository.deleteProjectCommitsWithoutProject();
        log.info("Cleanup to speed up deletion of commits");
        vacuum();
        log.info("deleteing commits");
        commitRepository.deleteCommitWithoutProject();
        log.info("Final cleanup to speed up deletion of duplicates");
        vacuum();
    }

    private void vacuum() throws SQLException {
        connection.createStatement().execute("VACUUM FULL ANALYZE");
    }
}
