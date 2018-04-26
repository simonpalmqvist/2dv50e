package com.sp222kh.investigitor.tasks;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collection;

public class ImportProjectCommitsTask implements Task {
    private final CopyManager copyManager;
    private final String file;

    public ImportProjectCommitsTask(CopyManager copyManager, String file) {
        this.copyManager = copyManager;
        this.file = file;
    }

    @Override
    public void run() throws Exception {
        String sql = "COPY project_commit (project_id, commit_id) FROM stdin CSV DELIMITER ',' NULL '\\N'";
        Reader in = new BufferedReader(new FileReader(new File(file)));
        copyManager.copyIn(sql, in);
    }
}
