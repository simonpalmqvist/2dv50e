package com.sp222kh.investigitor.tasks;


import com.sp222kh.investigitor.FileInfo;
import com.sp222kh.investigitor.FileInfoRepository;
import com.sp222kh.investigitor.Project;
import com.sp222kh.investigitor.ProjectRepository;

import java.io.File;
import java.io.IOException;

public class GetFileInfoTask implements Task {

    private ProjectRepository projectRepository;
    private FileInfoRepository fileInfoRepository;

    public GetFileInfoTask(ProjectRepository projectRepository, FileInfoRepository fileInfoRepository) {
        this.projectRepository = projectRepository;
        this.fileInfoRepository = fileInfoRepository;
    }

    @Override
    public void run() throws Exception {
        for (Project p : projectRepository.findAll()) {
            String path = "../repos/" + p.getPathToRepo();
            findFiles(p.getId(), new File(path));
        }
    }


    private void findFiles(long id, File file) throws IOException {
        if(file.isDirectory()) {
            for(final File file2 : file.listFiles()) {
                findFiles(id, file2);
            }
        } else {
            fileInfoRepository.save(new FileInfo(id, file));
        }
    }
}
