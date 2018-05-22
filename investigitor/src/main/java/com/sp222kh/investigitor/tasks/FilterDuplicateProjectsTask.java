package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class FilterDuplicateProjectsTask implements Task {

    private final ProjectRepository projectRepository;
    private final CommitRepository commitRepository;
    private Connection connection;

    public FilterDuplicateProjectsTask(ProjectRepository projectRepository, CommitRepository commitRepository, Connection connection) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.connection = connection;
    }

    @Override
    public void run() throws Exception {
        Set<Long> allDuplicates = new HashSet<>();

        for (Long id : projectRepository.findAllProjectIds()) {
            List<BigInteger> duplicates = commitRepository.findProjectDuplicates(id);

            //keep the original repository and delete the others.
            duplicates.remove(0);

            for(BigInteger duplicate : duplicates) allDuplicates.add(duplicate.longValue());
        }

        if(allDuplicates.size() > 0) {
            projectRepository.deleteDuplicateProjects(allDuplicates);
            projectRepository.deleteWatchersWithoutProject();
            commitRepository.deleteProjectCommitsWithoutProject();
            vacuum();
            commitRepository.deleteCommitWithoutProject();
            vacuum();
        }
    }

    private void vacuum() throws SQLException {
        connection.createStatement().execute("VACUUM FULL ANALYZE");
    }
}
