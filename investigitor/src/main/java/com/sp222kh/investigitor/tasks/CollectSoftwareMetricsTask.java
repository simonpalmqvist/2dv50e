package com.sp222kh.investigitor.tasks;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKReport;
import com.sp222kh.investigitor.models.Project;
import com.sp222kh.investigitor.models.SoftwareMetrics;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import com.sp222kh.investigitor.repositories.SoftwareMetricsRepository;

import java.io.File;
import java.util.stream.Collectors;

public class CollectSoftwareMetricsTask implements Task {

    private final ProjectRepository projectRepository;
    private final SoftwareMetricsRepository softwareMetricsRepository;
    private final String cloneFolder;

    public CollectSoftwareMetricsTask(ProjectRepository projectRepository, SoftwareMetricsRepository softwareMetricsRepository, String CLONE_FOLDER) {
        this.projectRepository = projectRepository;
        this.softwareMetricsRepository = softwareMetricsRepository;
        cloneFolder = CLONE_FOLDER;
    }

    @Override
    public void run() throws Exception {
        for (Project p : projectRepository.findAll()) {
            String path = cloneFolder + p.getPathToRepo();

            if(softwareMetricsRepository.existsByProjectId(p.getId()) || !(new File(path)).exists()) continue;

            CKReport report = new CK().calculate(path);

            softwareMetricsRepository.save(report.all()
                    .stream()
                    .filter(result -> !result.isError())
                    .map(result -> new SoftwareMetrics(p.getId(), result))
                    .collect(Collectors.toList())
            );
        }
    }
}