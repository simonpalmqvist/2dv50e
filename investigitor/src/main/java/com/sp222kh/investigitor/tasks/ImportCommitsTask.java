package com.sp222kh.investigitor.tasks;

import org.postgresql.copy.CopyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class ImportCommitsTask implements Task {
    private CopyManager copyManager;
    private String file;

    public ImportCommitsTask(CopyManager copyManager, String file) {
        this.copyManager = copyManager;
        this.file = file;
    }

    @Override
    public void run() throws Exception {
        String sql = "COPY commit (id, sha, author_id, committer_id, project_id, created_at) FROM stdin CSV DELIMITER ',' NULL '\\N'";
        Reader in = new BufferedReader(new FileReader(new File(file)));
        copyManager.copyIn(sql, in);
    }
}
