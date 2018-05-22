package com.sp222kh.investigitor.tasks;

import com.sp222kh.investigitor.repositories.CommitRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.postgresql.copy.CopyManager;

import java.io.*;
import java.util.Set;

public class ImportCommitsTask implements Task {
    private final CommitRepository commitRepository;
    private final CopyManager copyManager;
    private final String file;
    private final String filteredFile;

    public ImportCommitsTask(CommitRepository commitRepository, CopyManager copyManager, String file) {
        this.commitRepository = commitRepository;
        this.copyManager = copyManager;
        this.file = file;
        this.filteredFile = file + ".filtered";

    }

    @Override
    public void run() throws Exception {
        Set<Long> ids = commitRepository.findAllCommitIds();

        BufferedWriter out = new BufferedWriter(new FileWriter(filteredFile), 32768);
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.MYSQL.withHeader(
                "id",
                "sha",
                "author_id",
                "committer_id",
                "project_id",
                "created_at").withDelimiter(',').withQuote('"').withEscape('\\').parse(in);

        for (CSVRecord record : records) {
            if (ids.contains(Long.parseLong(record.get("id")))) {
                String createdAt = record.get("created_at");
                createdAt = createdAt.equals("0000-00-00 00:00:00") ? "\\N" : "\"" + createdAt + "\"";
                out.write(record.get("id") + "," + record.get("author_id") + "," + createdAt + "\n");
            }
        }
        out.close();
        in.close();

        String sql = "COPY commit (id, author_id, created_at) FROM stdin CSV DELIMITER ',' NULL '\\N'";
        in = new BufferedReader(new FileReader(new File(filteredFile)));
        copyManager.copyIn(sql, in);
    }
}
