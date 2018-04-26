package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateIndexesTask implements Task {

    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;
    private static final Logger log = LoggerFactory.getLogger(CreateIndexesTask.class);

    public CreateIndexesTask(ProjectRepository projectRepository, CommitRepository commitRepository) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public void run() throws Exception {
        log.info("Formatting dates");
        commitRepository.updateCreateDates("0000-00-00 00:00:00");
        log.info("Adding index for project_id in table watcher");
        projectRepository.addWatcherProjectIdIndex();
        log.info("Adding index for project_id in table project_commit");
        commitRepository.addProjectCommitProjectIdIndex();
        log.info("Adding index for commit_id in table project_commit");
        commitRepository.addProjectCommitCommitIdIndex();
    }
}
