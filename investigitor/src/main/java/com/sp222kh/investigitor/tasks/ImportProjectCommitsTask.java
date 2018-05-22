package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.repositories.ProjectRepository;
import org.postgresql.copy.CopyManager;

import java.io.*;
import java.util.Set;

public class ImportProjectCommitsTask implements Task {
    private ProjectRepository projectRepository;
    private final CopyManager copyManager;
    private final String file;
    private final String filteredFile;

    public ImportProjectCommitsTask(ProjectRepository projectRepository, CopyManager copyManager, String file) {
        this.projectRepository = projectRepository;
        this.copyManager = copyManager;
        this.file = file;
        this.filteredFile = file + ".filtered";
    }

    @Override
    public void run() throws Exception {
        Set<Long> ids = projectRepository.findAllProjectIds();

        BufferedWriter out = new BufferedWriter(new FileWriter(filteredFile), 32768);
        BufferedReader in = new BufferedReader(new FileReader(new File(file)));
        String line;

        while ((line = in.readLine()) != null) {
            String[] content = line.split(",");

            if(ids.contains(Long.parseLong(content[0]))) out.write(line + "\n");
        }
        in.close();
        out.close();

        String sql = "COPY project_commit (project_id, commit_id) FROM stdin CSV DELIMITER ',' NULL '\\N'";
        in = new BufferedReader(new FileReader(new File(filteredFile)));
        copyManager.copyIn(sql, in);
    }
}
