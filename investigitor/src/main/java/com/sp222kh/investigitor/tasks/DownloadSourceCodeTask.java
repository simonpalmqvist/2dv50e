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
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DownloadSourceCodeTask implements Task {

    private final ProjectRepository projectRepository;
    private final FileInfoRepository fileInfoRepository;
    private final String CLONE_FOLDER;
    private static final Logger log = LoggerFactory.getLogger(DownloadSourceCodeTask.class);

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

    public DownloadSourceCodeTask(ProjectRepository projectRepository, FileInfoRepository fileInfoRepository, String CLONE_FOLDER) {
        this.projectRepository = projectRepository;
        this.fileInfoRepository = fileInfoRepository;
        this.CLONE_FOLDER = CLONE_FOLDER;
    }

    @Override
    public void run() throws Exception {
        for (Project p : projectRepository.findAll()) {
            String path = CLONE_FOLDER + p.getPathToRepo();

            if(Files.exists(new File(path).toPath())) return;

            String command = "git clone --depth 1 https://github.com" + p.getPathToRepo() + ".git " + path;
            List<FileInfo> fileInfo = new ArrayList<>();

            try {
                (new File(path)).mkdirs();
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor(1, TimeUnit.HOURS);
                cleanUpAndIndexFiles(p.getId(), new File(path), fileInfo);

                if(fileInfo.size() == 0) throw new CloneException("Could not clone");

                fileInfoRepository.save(fileInfo);
            } catch (IOException | InterruptedException e) {
                log.info("Delete {} because of issues cloning or indexing files", p.getName());
                FileUtils.deleteDirectory(new File(path));
                projectRepository.delete(p.getId());
                e.printStackTrace();
            }
        }
    }

    private void cleanUpAndIndexFiles(long id, File file, List<FileInfo> fileInfo) throws IOException {
        if (file.isDirectory()) {
            for (final File file2 : file.listFiles()) {
                cleanUpAndIndexFiles(id, file2, fileInfo);
            }
        } else if (FilenameUtils.getExtension(file.getName()).equals("java") ||
                FILES_TO_KEEP.contains(file.getName().toLowerCase())) {
            fileInfo.add(new FileInfo(id, file));
        } else {
            file.delete();
        }
    }
}
