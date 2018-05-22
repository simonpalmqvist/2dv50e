package com.sp222kh.investigitor.tasks;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.postgresql.copy.CopyManager;

import java.io.*;
import java.util.Objects;

public class ImportProjectsTask implements Task {
    private final String filteredFile;
    private final String file;
    private final CopyManager copyManager;

    public ImportProjectsTask(CopyManager copyManager, String file) {
        this.copyManager = copyManager;
        this.file = file;
        this.filteredFile = file + ".filtered";
    }

    @Override
    public void run() throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter(filteredFile), 32768);
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.MYSQL.withHeader(
                "id",
                "url",
                "owner_id",
                "name",
                "description",
                "language",
                "created_at",
                "forked_from",
                "deleted",
                "updated_at",
                "creator").withDelimiter(',').withQuote('"').withEscape('\\').parse(in);

        for (CSVRecord record : records) {
            String lang = record.get("language");
            String deleted = record.get("deleted");
            String description = record.get("description");
            if (lang != null && lang.equals("Java") && deleted.equals("0")) {
                if(description != null) description = "\"" + description.replaceAll("\\R", "").replaceAll("\\\\", "\\\\\\\\").replaceAll("[\"]", "\\\\\"") + "\"";
                else                    description = "\\N";
                out.write(record.get("id") + ",\"" + record.get("url") + "\",\"" + record.get("name") + "\"," + description + ",\"" + record.get("created_at") + "\"\n");
            }
        }
        out.close();

        String sql = "COPY project (id, url, name, description, created_at) FROM stdin CSV DELIMITER ',' NULL '\\N' ESCAPE '\\'";
        in = new BufferedReader(new FileReader(new File(filteredFile)));
        copyManager.copyIn(sql, in);
    }
}
