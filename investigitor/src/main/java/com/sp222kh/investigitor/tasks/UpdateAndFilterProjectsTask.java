package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.CommitRepository;
import com.sp222kh.investigitor.Project;
import com.sp222kh.investigitor.ProjectRepository;
import com.sp222kh.investigitor.csv.WatcherItem;
import net.sf.jsefa.Deserializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class UpdateAndFilterProjectsTask implements Task {
    private ProjectRepository projectRepository;
    private CommitRepository commitRepository;
    private Deserializer deserializer;
    private String file;
    private float ONE_DAY = 1000*60*60*24;

    public UpdateAndFilterProjectsTask(ProjectRepository projectRepository, CommitRepository commitRepository,
                                       Deserializer deserializer, String file) {
        this.projectRepository = projectRepository;
        this.commitRepository = commitRepository;
        this.deserializer = deserializer;
        this.file = file;
    }

    @Override
    public void run() throws Exception {
        Map<Long, Integer> projects = projectRepository
                .findAllProjectIds()
                .stream()
                .collect(Collectors.toMap(p -> p, p -> 0));

        addWatchers(projects);
        updateProjects(projects);
        removeDuplicates(projects);
    }

    private void addWatchers(Map<Long, Integer> projects) throws FileNotFoundException {
        deserializer.open(new FileReader(file));

        while (deserializer.hasNext()) {
            WatcherItem w = deserializer.next();

            if (projects.containsKey(w.projectId)) {
                projects.put(w.projectId, projects.get(w.projectId) + 1);
            }
        }

        deserializer.close(true);
    }

    private void updateProjects(Map<Long, Integer> projects) {
        for (Long id : projects.keySet()) {
            Project p = projectRepository.findOne(id);
            p.setContributors(commitRepository.findContributorsForProject(id));
            p.setLastCommit(commitRepository.findLatestCommitDateForProject(id));
            p.setWatchers(projects.get(id));

            float activeDays = 0;

            if (p.getLastCommit() != null)
                activeDays = (p.getLastCommit().getTime() - p.getCreatedAt().getTime()) / ONE_DAY;

            // We are only interested in projects with more than 0 watchers, more than 1 contributor and been active over 99 days
            if(p.getWatchers() > 0 && p.getContributors() > 1 && activeDays > 99) {
                projectRepository.save(p);
            } else {
                projectRepository.delete(id);
                commitRepository.deleteByProjectId(id);
            }
        }
    }

    private void removeDuplicates(Map<Long, Integer> projects) {
        for (Long id : projects.keySet()) {
            if(projectRepository.exists(id)) {
                List<Long> duplicates = commitRepository.findProjectDuplicates(id);

                //keep the original repository and delete the others.
                duplicates.remove(0);

                for(Long duplicate : duplicates) {
                    projectRepository.delete(duplicate);
                    commitRepository.deleteByProjectId(duplicate);
                }
            }
        }
    }
}
