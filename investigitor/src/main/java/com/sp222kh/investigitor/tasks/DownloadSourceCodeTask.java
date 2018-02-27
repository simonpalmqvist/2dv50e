package com.sp222kh.investigitor.tasks;


import com.sp222kh.investigitor.Project;
import com.sp222kh.investigitor.ProjectRepository;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DownloadSourceCodeTask implements Task {

    private ProjectRepository projectRepository;

    public DownloadSourceCodeTask(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void run() throws Exception {
        for (Project p : projectRepository.findAll()) {
            String path = "../repos/" + p.getPathToRepo();
            String command = "git clone --depth 1 https://github.com" + p.getPathToRepo() + ".git " + path;

            try {
                (new File(path)).mkdirs();
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
