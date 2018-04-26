package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatDateTask implements Task {

    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;
    private static final Logger log = LoggerFactory.getLogger(FormatDateTask.class);

    public FormatDateTask(ProjectRepository projectRepository, CommitRepository commitRepository) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public void run() throws Exception {
        log.info("Formatting dates");
        commitRepository.updateCreateDates("0000-00-00 00:00:00");
    }
}
