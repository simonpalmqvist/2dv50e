package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.models.FileInfo;
import com.sp222kh.investigitor.models.Project;
import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.FileInfoRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilterProjectsTask implements Task {

    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;

    public FilterProjectsTask(ProjectRepository projectRepository, CommitRepository commitRepository) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public void run() throws Exception {
        projectRepository.deleteNonJavaProjects();
        projectRepository.deleteWatchersWithoutProject();
        commitRepository.deleteProjectCommitsWithoutProject();
        commitRepository.deleteCommitWithoutProject();
    }
}
