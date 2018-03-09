package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.models.Commit;
import com.sp222kh.investigitor.repositories.CommitRepository;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import com.sp222kh.investigitor.csv.CommitItem;
import com.sp222kh.investigitor.csv.ProjectCommitItem;
import net.sf.jsefa.Deserializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class AddProjectCommitsTask implements Task {
    private ProjectRepository projectRepository;
    private CommitRepository commitRepository;
    private Deserializer projectCommitDeserializer;
    private Deserializer commitDeserializer;
    private String projectCommitFile;
    private String commitFile;

    private List<Commit> commitBulk = new ArrayList<>();

    public AddProjectCommitsTask(ProjectRepository projectRepository, CommitRepository commitRepository,
                                 Deserializer projectCommitDeserializer, Deserializer commitDeserializer,
                                 String projectCommitFile, String commitFile) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.projectCommitDeserializer = projectCommitDeserializer;
        this.commitDeserializer = commitDeserializer;
        this.projectCommitFile = projectCommitFile;
        this.commitFile = commitFile;
    }

    @Override
    public void run() throws Exception {
        Map<Long, Set<Long>> projectCommits = getCommitIdsForProjects();
        storeCommits(projectCommits);

    }

    private Map<Long, Set<Long>> getCommitIdsForProjects() throws FileNotFoundException {
        Set<Long> projects = projectRepository.findAllProjectIds();
        Map<Long, Set<Long>> projectCommits = new HashMap<>();

        projectCommitDeserializer.open(new FileReader(projectCommitFile));

        while (projectCommitDeserializer.hasNext()) {
            ProjectCommitItem pc = projectCommitDeserializer.next();

            // A commit id can belong to several projects
            if (projects.contains(pc.projectId)) {
                if(!projectCommits.containsKey(pc.commitId))
                    projectCommits.put(pc.commitId, new HashSet<>());

                projectCommits
                        .get(pc.commitId)
                        .add(pc.projectId);
            }
        }

        projectCommitDeserializer.close(true);

        return projectCommits;
    }

    private void storeCommits(Map<Long, Set<Long>> projectCommits) throws FileNotFoundException {
        commitDeserializer.open(new FileReader(commitFile));

        while (commitDeserializer.hasNext()) {
            CommitItem c = commitDeserializer.next();

            if (projectCommits.containsKey(c.id)) {
                for (long projectId : projectCommits.get(c.id)) {
                    store(new Commit(projectId, c));
                }
            }
        }
        commitRepository.save(commitBulk);

        commitDeserializer.close(true);
    }

    private void store(Commit commit) {
        commitBulk.add(commit);
        if (commitBulk.size() > 100) {
            commitRepository.save(commitBulk);
            commitBulk.clear();
        }
    }
}
