package com.sp222kh.investigitor.tasks;

import org.postgresql.copy.CopyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class ImportProjectsTask implements Task {
    private CopyManager copyManager;
    private String file;

    public ImportProjectsTask(CopyManager copyManager, String file) {
        this.copyManager = copyManager;
        this.file = file;
    }

    @Override
    public void run() throws Exception {
        String sql = "COPY project (id, url, owner_id, name, description, language, created_at, forked_from, deleted, updated_at) FROM stdin CSV DELIMITER ',' NULL '\\N' ESCAPE '\\'";
        Reader in = new BufferedReader(new FileReader(new File(file)));
        copyManager.copyIn(sql, in);
    }
}
