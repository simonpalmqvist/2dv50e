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
import java.util.List;

public class ImportProjectCommitsTask implements Task {
    private final CopyManager copyManager;
    private final String wildcard;
    private final String directory;
    private static final Logger log = LoggerFactory.getLogger(ImportProjectCommitsTask.class);

    public ImportProjectCommitsTask(CopyManager copyManager, String directory, String wildcard) {
        this.copyManager = copyManager;
        this.wildcard = wildcard;
        this.directory = directory;
    }

    @Override
    public void run() throws Exception {
        Collection<File> files = FileUtils.listFiles(new File(directory), new WildcardFileFilter(wildcard), null);

        for(File file : files) {
            log.info("Importing file: " + file.getName());
            String sql = "COPY project_commit (project_id, commit_id) FROM stdin CSV DELIMITER ',' NULL '\\N'";
            Reader in = new BufferedReader(new FileReader(file));
            copyManager.copyIn(sql, in);
        }
    }
}
