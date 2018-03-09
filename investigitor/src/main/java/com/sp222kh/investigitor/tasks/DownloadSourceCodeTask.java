package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.models.FileInfo;
import com.sp222kh.investigitor.repositories.FileInfoRepository;
import com.sp222kh.investigitor.models.Project;
import com.sp222kh.investigitor.repositories.ProjectRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DownloadSourceCodeTask implements Task {

    private final ProjectRepository projectRepository;
    private final FileInfoRepository fileInfoRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    public static final String[] FILES = new String[]{
            "readme.md",
            "readme.txt",
            "readme",
            "license.txt",
            "license",
            ".travis.yml",
            "pom.xml",
            "build.gradle",
            "settings.gradle"
    };
    public static final Set<String> FILES_TO_KEEP = new HashSet<>(Arrays.asList(FILES));

    public DownloadSourceCodeTask(ProjectRepository projectRepository, FileInfoRepository fileInfoRepository) {
        this.projectRepository = projectRepository;
        this.fileInfoRepository = fileInfoRepository;
    }

    @Override
    public void run() throws Exception {
        for (Project p : projectRepository.findAll()) {
            if (p.isDownloaded()) return;

            String path = "../repos/" + p.getPathToRepo();
            String command = "git clone --depth 1 https://github.com" + p.getPathToRepo() + ".git " + path;

            try {
                (new File(path)).mkdirs();
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                cleanUpAndIndexFiles(p.getId(), new File(path));
                p.setDownloaded(true);
                projectRepository.save(p);
            } catch (IOException | InterruptedException e) {
                log.info("Delete {} because of issues cloning or indexing files", p.getName());
                FileUtils.deleteDirectory(new File(path));
                projectRepository.delete(p.getId());
                e.printStackTrace();
            }
        }
    }

    private void cleanUpAndIndexFiles(long id, File file) throws IOException {
        if (file.isDirectory()) {
            for (final File file2 : file.listFiles()) {
                cleanUpAndIndexFiles(id, file2);
            }
        } else if (FilenameUtils.getExtension(file.getName()).equals("java") ||
                FILES_TO_KEEP.contains(file.getName().toLowerCase())) {
            fileInfoRepository.save(new FileInfo(id, file));
        } else {
            file.delete();
        }
    }
}
