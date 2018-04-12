package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;

public class FilterQualityProjectsTask implements Task {

    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;

    public FilterQualityProjectsTask(ProjectRepository projectRepository, CommitRepository commitRepository) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public void run() throws Exception {
        projectRepository.deleteNonQualityProjects();
        projectRepository.deleteWatchersWithoutProject();
        commitRepository.deleteProjectCommitsWithoutProject();
        commitRepository.deleteCommitWithoutProject();
    }
}
